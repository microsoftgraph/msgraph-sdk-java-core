package com.microsoft.graph.httpcore.middlewareoption;

import okhttp3.Request;
import okhttp3.Response;

public class RetryOptions implements IMiddlewareControl {
	private IShouldRetry shouldretry;
	
	public RetryOptions(){
		this(new IShouldRetry() {
			public boolean shouldRetry(Response response, int executionCount, Request request) {
				return true;
			}
		});
	}
	
	public RetryOptions(IShouldRetry shouldretry){
		this.shouldretry = shouldretry;
	}
	
	public IShouldRetry shouldRetry() {
		return shouldretry;
	}
}
