package com.microsoft.graph.httpcore;

import java.io.IOException;

import com.microsoft.graph.httpcore.middlewareoption.RetryOptions;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class RetryHandler implements Interceptor{

    /**
     * Maximum number of allowed retries if the server responds with a HTTP code
     * in our retry code list. Default value is 1.
     */
    private final int maxRetries = 2;

    /**
     * Retry interval between subsequent requests, in milliseconds. Default
     * value is 1 second.
     */
    private long retryInterval = 1000;
    private final int DELAY_MILLISECONDS = 1000;
    private final String RETRY_AFTER = "Retry-After";
    private final String TRANSFER_ENCODING = "Transfer-Encoding";
    
    private final int MSClientErrorCodeTooManyRequests = 429;
    private final int MSClientErrorCodeServiceUnavailable  = 503;
    private final int MSClientErrorCodeGatewayTimeout = 504;
    private final RetryOptions mRetryOption;
    
    public RetryHandler(RetryOptions option) {
        super();
        this.mRetryOption = option;
    }

    public RetryHandler() {
        this(null);
    }
    
	public boolean retryRequest(Response response, int executionCount, Request request) {
		
		RetryOptions retryOption = request.tag(RetryOptions.class);
		if(retryOption != null) {
			return retryOption.shouldRetry().shouldRetry(response, executionCount, request);
		}
		if(mRetryOption != null) {
			return mRetryOption.shouldRetry().shouldRetry(response, executionCount, request);
		}
		
		boolean shouldRetry = false;
		int statusCode = response.code();
		shouldRetry = (executionCount < maxRetries) && checkStatus(statusCode) && isBuffered(response, request);
		
		if(shouldRetry) {
			String retryAfterHeader = response.header(RETRY_AFTER);
			if(retryAfterHeader != null) 
				retryInterval = Long.parseLong(retryAfterHeader);
			else
				retryInterval = (long)Math.pow(2.0, (double)executionCount) * DELAY_MILLISECONDS;
		}
		return shouldRetry;
	}

	public long getRetryInterval() {
		return retryInterval;
	}
	
	private boolean checkStatus(int statusCode) {
	    if (statusCode == MSClientErrorCodeTooManyRequests || statusCode == MSClientErrorCodeServiceUnavailable 
	    		|| statusCode == MSClientErrorCodeGatewayTimeout)
	    	return true;
	    return false;
	}
	
	private boolean isBuffered(Response response, Request request) {
		String methodName = request.method();
		
		boolean isHTTPMethodPutPatchOrPost = methodName.equalsIgnoreCase("POST") ||
				methodName.equalsIgnoreCase("PUT") ||
				methodName.equalsIgnoreCase("PATCH");
		
		//Header transferEncoding = response.getFirstHeader(TRANSFER_ENCODING);
		String transferEncoding = response.header(TRANSFER_ENCODING);
		boolean isTransferEncodingChunked = (transferEncoding != null) && 
				transferEncoding.equalsIgnoreCase("chunked"); 
		
		if(request.body() != null && isHTTPMethodPutPatchOrPost && isTransferEncodingChunked)
			return false;
		return true;
	}

	@Override
	public Response intercept(Chain chain) throws IOException {
		Request request = chain.request();
		
		Response response = chain.proceed(request);
		int executionCount = 0;
		while(retryRequest(response, executionCount, request)) {
			executionCount++;
			response = chain.proceed(request);
		}
		return response;
	}

}
