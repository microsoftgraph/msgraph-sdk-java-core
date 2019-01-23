package com.microsoft.graph.httpcore;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.ServiceUnavailableRetryStrategy;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpCoreContext;
import org.apache.http.util.Args;

import com.microsoft.graph.httpcore.middlewareoption.MiddlewareType;
import com.microsoft.graph.httpcore.middlewareoption.RetryOptions;

public class RetryHandler implements ServiceUnavailableRetryStrategy{

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
    
	@Override
	public boolean retryRequest(HttpResponse response, int executionCount, HttpContext context) {
		
		RetryOptions retryOption = (RetryOptions)context.getAttribute(MiddlewareType.RETRY.toString());
		if(retryOption != null) {
			return retryOption.shouldRetry().shouldRetry(response, executionCount, context);
		}
		if(mRetryOption != null) {
			return mRetryOption.shouldRetry().shouldRetry(response, executionCount, context);
		}
		
		boolean shouldRetry = false;
		int statusCode = response.getStatusLine().getStatusCode();
		shouldRetry = (executionCount < maxRetries) && checkStatus(statusCode) && isBuffered(response, context);
		
		if(shouldRetry) {
			Header header = response.getFirstHeader(RETRY_AFTER);
			if(header != null) 
				retryInterval = Long.parseLong(header.getValue());
			else
				retryInterval = (long)Math.pow(2.0, (double)executionCount) * DELAY_MILLISECONDS;
		}
		return shouldRetry;
	}

	@Override
	public long getRetryInterval() {
		return retryInterval;
	}
	
	private boolean checkStatus(int statusCode) {
	    if (statusCode == MSClientErrorCodeTooManyRequests || statusCode == MSClientErrorCodeServiceUnavailable 
	    		|| statusCode == MSClientErrorCodeGatewayTimeout)
	    	return true;
	    return false;
	}
	
	private boolean isBuffered(HttpResponse response, HttpContext context) {
		HttpRequest request = (HttpRequest)context.getAttribute( HttpCoreContext.HTTP_REQUEST);
		String methodName = request.getRequestLine().getMethod();
		
		boolean isHTTPMethodPutPatchOrPost = methodName.equalsIgnoreCase(HttpPost.METHOD_NAME) ||
				methodName.equalsIgnoreCase(HttpPut.METHOD_NAME) ||
				methodName.equalsIgnoreCase(HttpPatch.METHOD_NAME);
		
		Header transferEncoding = response.getFirstHeader(TRANSFER_ENCODING);
		boolean isTransferEncodingChunked = (transferEncoding != null) && 
				transferEncoding.getValue().equalsIgnoreCase("chunked"); 
		
		HttpEntity entity = null;
		if(request instanceof HttpEntityEnclosingRequestBase) {
			HttpEntityEnclosingRequestBase httprequest = (HttpEntityEnclosingRequestBase)request;
			entity = httprequest.getEntity();
		}
		
		if(entity != null && isHTTPMethodPutPatchOrPost && isTransferEncodingChunked)
			return false;
		return true;
	}

}
