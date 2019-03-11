package com.microsoft.graph.httpcore;

import java.io.IOException;

import com.microsoft.graph.httpcore.middlewareoption.IShouldRetry;
import com.microsoft.graph.httpcore.middlewareoption.MiddlewareType;
import com.microsoft.graph.httpcore.middlewareoption.RetryOptions;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class RetryHandler implements Interceptor{
	
	public final MiddlewareType MIDDLEWARE_TYPE = MiddlewareType.RETRY;

    private RetryOptions mRetryOption;
    
    /*
     * constant string being used
     */
    private final String RETRY_ATTEMPT_HEADER = "Retry-Attempt";
    private final String RETRY_AFTER = "Retry-After";
    private final String TRANSFER_ENCODING = "Transfer-Encoding";
    private final String TRANSFER_ENCODING_CHUNKED = "chunked";
    private final String APPLICATION_OCTET_STREAM = "application/octet-stream";
    private final String CONTENT_TYPE = "Content-Type";
    
    public static final int MSClientErrorCodeTooManyRequests = 429;
    public static final int MSClientErrorCodeServiceUnavailable  = 503;
    public static final int MSClientErrorCodeGatewayTimeout = 504;
    
    private final long DELAY_MILLISECONDS = 1000;
    
    /*
     * @retryOption Create Retry handler using retry option
     */
    public RetryHandler(RetryOptions retryOption) {
        this.mRetryOption = retryOption;
        if(this.mRetryOption == null) {
        	this.mRetryOption = new RetryOptions();
        }
    }
    /*
     * Initialize retry handler with default retry option
     */
    public RetryHandler() {
        this(null);
    }
    
	boolean retryRequest(Response response, int executionCount, Request request, RetryOptions retryOptions) {
		
		// Should retry option
		// Use should retry common for all requests
		IShouldRetry shouldRetryCallback = null;
		if(retryOptions != null) {
			shouldRetryCallback = retryOptions.shouldRetry();
		}
		
		boolean shouldRetry = false;
		// Status codes 429 503 504
		int statusCode = response.code();
		// Only requests with payloads that are buffered/rewindable are supported. 
		// Payloads with forward only streams will be have the responses returned 
		// without any retry attempt.
		shouldRetry = 
				(executionCount <= retryOptions.maxRetries()) 
				&& checkStatus(statusCode) && isBuffered(response, request)
				&& shouldRetryCallback != null 
				&& shouldRetryCallback.shouldRetry(retryOptions.delay(), executionCount, request, response);
		
		if(shouldRetry) {
			long retryInterval = getRetryAfter(response, retryOptions.delay(), executionCount);
			try {
				Thread.sleep(retryInterval);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return shouldRetry;
	}
	
	long getRetryAfter(Response response, long delay, int executionCount) {
		String retryAfterHeader = response.header(RETRY_AFTER);
		long retryDelay = RetryOptions.DEFAULT_DELAY;
		if(retryAfterHeader != null) {
			retryDelay = Long.parseLong(retryAfterHeader);
		} else {
			retryDelay = (long)((Math.pow(2.0, (double)executionCount)-1)*0.5);
			retryDelay = executionCount < 2 ? retryDelay : retryDelay + delay + (long)Math.random(); 
			retryDelay *= DELAY_MILLISECONDS;
		}
		return Math.min(retryDelay, RetryOptions.MAX_DELAY);
	}
	
	boolean checkStatus(int statusCode) {
	    return (statusCode == MSClientErrorCodeTooManyRequests || statusCode == MSClientErrorCodeServiceUnavailable 
	    		|| statusCode == MSClientErrorCodeGatewayTimeout);
	}
	
	boolean isBuffered(Response response, Request request) {
		String methodName = request.method();
		if(methodName.equalsIgnoreCase("GET") || methodName.equalsIgnoreCase("DELETE") || methodName.equalsIgnoreCase("HEAD") || methodName.equalsIgnoreCase("OPTIONS")) 
			return true;
		
		boolean isHTTPMethodPutPatchOrPost = methodName.equalsIgnoreCase("POST") ||
				methodName.equalsIgnoreCase("PUT") ||
				methodName.equalsIgnoreCase("PATCH");
		
		if(isHTTPMethodPutPatchOrPost) {
			boolean isStream = response.header(CONTENT_TYPE)!=null && response.header(CONTENT_TYPE).equalsIgnoreCase(APPLICATION_OCTET_STREAM);
			if(!isStream) {
				String transferEncoding = response.header(TRANSFER_ENCODING);
				boolean isTransferEncodingChunked = (transferEncoding != null) && 
						transferEncoding.equalsIgnoreCase(TRANSFER_ENCODING_CHUNKED);
				
				if(request.body() != null && isTransferEncodingChunked)
					return true;
			}
		}
		return false;
	}

	@Override
	public Response intercept(Chain chain) throws IOException {
		Request request = chain.request();
		Response response = chain.proceed(request);
		
		// Use should retry pass along with this request
		RetryOptions retryOption = request.tag(RetryOptions.class);
		retryOption = retryOption != null ? retryOption : mRetryOption;
		
		int executionCount = 1;
		while(retryRequest(response, executionCount, request, retryOption)) {
			request = request.newBuilder().addHeader(RETRY_ATTEMPT_HEADER, String.valueOf(executionCount)).build();
			executionCount++;
			response = chain.proceed(request);
		}
		return response;
	}

}
