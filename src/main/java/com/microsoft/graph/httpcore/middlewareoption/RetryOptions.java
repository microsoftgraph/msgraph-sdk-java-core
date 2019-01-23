package com.microsoft.graph.httpcore.middlewareoption;

import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;

public class RetryOptions implements IMiddlewareControl {
	private IShouldRetry shouldretry;
	
	RetryOptions(){
		this(new IShouldRetry() {
			public boolean shouldRetry(HttpResponse response, int executionCount, HttpContext context) {
				return true;
			}
		});
	}
	
	RetryOptions(IShouldRetry shouldretry){
		this.shouldretry = shouldretry;
	}
	
	public IShouldRetry shouldRetry() {
		return shouldretry;
	}
}
