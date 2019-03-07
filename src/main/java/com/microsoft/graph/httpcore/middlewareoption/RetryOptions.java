package com.microsoft.graph.httpcore.middlewareoption;

import okhttp3.Request;
import okhttp3.Response;

public class RetryOptions implements IMiddlewareControl {
	private IShouldRetry shouldretry;
	public static final IShouldRetry DEFAULT_SHOULD_RETRY = new IShouldRetry() {
		@Override
		public boolean shouldRetry(Response response, int executionCount, Request request, long delay) {
			return true;
		}
	};
	
    private int maxRetries;
    public static final int MAX_RETRIES = 10;
    public static final int DEFAULT_MAX_RETRIES = 3;
    
    /*
     * Delay in seconds
     */
    private long delay;
    public static final long DEFAULT_DELAY = 3; // 3 seconds default delay
    public static final long MAX_DELAY = 180; // 180 second max delay
    
    /*
     * Create default instance of retry options, with default values of delay, max retries and shouldRetry callback.
     */
	public RetryOptions(){
		this(DEFAULT_SHOULD_RETRY, DEFAULT_MAX_RETRIES, DEFAULT_DELAY);
	}
	
	/*
	 * @param shouldRetry Retry callback to be called before making a retry
	 * @param maxRetries Number of max retires for a request
	 * @param delay Delay in seconds between retries
	 */
	public RetryOptions(IShouldRetry shouldRetry, int maxRetries, long delay) {
		if(delay > MAX_DELAY)
			throw new IllegalArgumentException("Delay cannot exceed " + MAX_DELAY);
		if(delay < 0)
			throw new IllegalArgumentException("Delay cannot be negative");
		if(maxRetries > MAX_RETRIES)
			throw new IllegalArgumentException("Max retires cannot exceed " + MAX_RETRIES);
		if(maxRetries < 0)
			throw new IllegalArgumentException("Max retires cannot be negative");
		
		this.shouldretry = shouldRetry != null ? shouldRetry : DEFAULT_SHOULD_RETRY;
		this.maxRetries = maxRetries;
		this.delay = delay;
	}
	
	/*
	 * @return should retry callback
	 */
	public IShouldRetry shouldRetry() {
		return shouldretry;
	}
	
	/*
	 * @return Number of max retries
	 */
	public int maxRetries() {
		return maxRetries;
	}
	
	/*
	 * @return Delay in seconds between retries
	 */
	public long delay() {
		return delay;
	}
}
