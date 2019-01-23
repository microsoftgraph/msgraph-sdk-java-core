package com.microsoft.graph.httpcore.middlewareoption;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.protocol.HttpClientContext;

public class HttpContextBuilder {
	
	private RetryOptions retryoptions;
	private int maxRedirect = -1;
	
	public static HttpContextBuilder create() {
		return new HttpContextBuilder();
	}
	
	public void setRetryOption(IShouldRetry shouldRetry) {
		retryoptions = new RetryOptions(shouldRetry);
	}
	
	public void setRedirectOption(int maxRedirect) {
		this.maxRedirect = maxRedirect;
	}
	
	public HttpClientContext build() {
		HttpClientContext context = HttpClientContext.create();
		if(retryoptions != null)
			context.setAttribute(MiddlewareType.RETRY.toString(), retryoptions);
		if(maxRedirect != -1) {
			RequestConfig config = RequestConfig.custom().setMaxRedirects(maxRedirect).build();
			context.setRequestConfig(config);
		}
		return context;
	}
}
