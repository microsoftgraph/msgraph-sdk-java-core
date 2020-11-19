package com.microsoft.graph.httpcore.middlewareoption;

import okhttp3.Response;

import javax.annotation.Nullable;
import javax.annotation.Nonnull;

public class RedirectOptions implements IMiddlewareControl{
	private int maxRedirects;
	public static final int DEFAULT_MAX_REDIRECTS = 5;
	public static final int MAX_REDIRECTS = 20;
	
	private IShouldRedirect shouldRedirect;
	public static final IShouldRedirect DEFAULT_SHOULD_REDIRECT = new IShouldRedirect() {
		@Override
		public boolean shouldRedirect(Response response) {
			return true;
		}
	}; 
	
	/*
	 * Create default instance of redirect options, with default values of max redirects and should redirect
	 */
	public RedirectOptions() {
		this(DEFAULT_MAX_REDIRECTS, DEFAULT_SHOULD_REDIRECT);
	}
	
	/*
	 * @param maxRedirects Max redirects to occur
	 * @param shouldRedirect Should redirect callback called before every redirect
	 */
	public RedirectOptions(int maxRedirects, @Nullable final IShouldRedirect shouldRedirect) {
		if(maxRedirects < 0)
			throw new IllegalArgumentException("Max redirects cannot be negative");
		if(maxRedirects > MAX_REDIRECTS)
			throw new IllegalArgumentException("Max redirect cannot exceed " + MAX_REDIRECTS);
		
		this.maxRedirects = maxRedirects;
		this.shouldRedirect = shouldRedirect != null ? shouldRedirect : DEFAULT_SHOULD_REDIRECT;
	}
	
	/*
	 * @return max redirects
	 */
	public int maxRedirects() {
		return this.maxRedirects;
	}
	
	/*
	 * @return should redirect
	 */
	@Nonnull
	public IShouldRedirect shouldRedirect() {
		return this.shouldRedirect;
	}
}
