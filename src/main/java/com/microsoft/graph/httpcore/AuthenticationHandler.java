package com.microsoft.graph.httpcore;

import java.io.IOException;

import com.microsoft.graph.authentication.ICoreAuthenticationProvider;
import com.microsoft.graph.exceptions.AuthenticationException;

import javax.annotation.Nullable;
import javax.annotation.Nonnull;

import com.microsoft.graph.httpcore.middlewareoption.MiddlewareType;
import com.microsoft.graph.httpcore.middlewareoption.TelemetryOptions;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthenticationHandler implements Interceptor {
	
	public final MiddlewareType MIDDLEWARE_TYPE = MiddlewareType.AUTHENTICATION;

	private ICoreAuthenticationProvider authProvider;
	
	public AuthenticationHandler(@Nonnull final ICoreAuthenticationProvider authProvider) {
		this.authProvider = authProvider;
	}

	@Override
	@Nullable
	public Response intercept(@Nonnull final Chain chain) throws IOException {
		Request originalRequest = chain.request();
		
		if(originalRequest.tag(TelemetryOptions.class) == null)
			originalRequest = originalRequest.newBuilder().tag(TelemetryOptions.class, new TelemetryOptions()).build();
		originalRequest.tag(TelemetryOptions.class).setFeatureUsage(TelemetryOptions.AUTH_HANDLER_ENABLED_FLAG);
		
		try {
			final Request authenticatedRequest = authProvider.authenticateRequest(originalRequest);
			return chain.proceed(authenticatedRequest);
		} catch (AuthenticationException ex) {
			throw new IOException(ex);
		}
	}

}
