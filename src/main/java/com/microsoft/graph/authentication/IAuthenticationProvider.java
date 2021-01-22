package com.microsoft.graph.authentication;

import java.net.URL;
import java.util.concurrent.CompletableFuture;

import javax.annotation.Nonnull;

/**
 * Authenticates requests to be sent to the API
 */
public interface IAuthenticationProvider {
    /**
     * Authenticates the request
     *
     * @param requestUrl the outgoing request URL
     * @return a future with the token
     */
    @Nonnull
    CompletableFuture<String> getAuthorizationTokenAsync(@Nonnull final URL requestUrl);
}
