package com.microsoft.graph.httpcore;

import java.io.IOException;

import com.microsoft.graph.httpcore.middlewareoption.MiddlewareType;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthenticationHandler implements Interceptor {
	
	public final MiddlewareType MIDDLEWARE_TYPE = MiddlewareType.AUTHENTICATION;

	private IAuthenticationProvider authProvider;
	
	public AuthenticationHandler(IAuthenticationProvider authProvider) {
		this.authProvider = authProvider;
	}

	@Override
	public Response intercept(Chain chain) throws IOException {
		Request originalRequest = chain.request();
		Request authenticatedRequest = authProvider.authenticateRequest(originalRequest);
		return chain.proceed(authenticatedRequest);
	}

}
