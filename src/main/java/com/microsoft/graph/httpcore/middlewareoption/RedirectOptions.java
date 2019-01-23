package com.microsoft.graph.httpcore.middlewareoption;

public class RedirectOptions implements IMiddlewareControl {
	private int maxRedirects;
	
	public RedirectOptions() {
		this(0);
	}
	
	public RedirectOptions(int maxRedirects) {
		this.maxRedirects = maxRedirects;
	}
	
	public int getMaxRedirects() {
		return maxRedirects;
	}
}
