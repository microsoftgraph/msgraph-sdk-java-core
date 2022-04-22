package com.microsoft.graph.httpcore;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import com.microsoft.graph.authentication.IAuthenticationProvider;

import javax.annotation.Nonnull;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Interceptor responsible for injecting the token in the request headers
 */
public class AuthenticationHandler implements Interceptor {
    /** The bearer value for the authorization request header, contains a space */
    protected static final String BEARER = "Bearer ";
    /** The authorization request header name */
    protected static final String AUTHORIZATION_HEADER = "Authorization";

    private IAuthenticationProvider authProvider;

    /**
     * Initialize a the handler with a authentication provider
     * @param authProvider the authentication provider to use
     */
    public AuthenticationHandler(@Nonnull final IAuthenticationProvider authProvider) {
        this.authProvider = authProvider;
    }

    @Override
    @Nonnull
    public Response intercept(@Nonnull final Chain chain) throws IOException {
        Request originalRequest = chain.request();

        FeatureTracker featureTracker = originalRequest.tag(FeatureTracker.class);
        if(featureTracker == null) {
            featureTracker = new FeatureTracker();
            originalRequest = originalRequest.newBuilder().tag(FeatureTracker.class, featureTracker).build();
        }
        featureTracker.setFeatureUsage(FeatureFlag.AUTH_HANDLER_FLAG);

        try {
            final CompletableFuture<String> future = authProvider.getAuthorizationTokenAsync(originalRequest.url().url());
            final String accessToken = future.get();
            if(accessToken == null)
                return chain.proceed(originalRequest);
            else {
                return chain.proceed(originalRequest
                                        .newBuilder()
                                        .addHeader(AUTHORIZATION_HEADER, BEARER + accessToken)
                                        .build());
            }
        } catch (InterruptedException | ExecutionException ex) {
            if (ex instanceof InterruptedException)
                Thread.currentThread().interrupt();
            throw new IOException(ex);
        }
    }

}
