package com.microsoft.graph.httpcore;

import okhttp3.Request;

import javax.annotation.Nonnull;

/**
 * Authenticates requests to be sent to the API
 */
public interface ICoreAuthenticationProvider {
    /**
     * Authenticates the request
     *
     * @param request the request to authenticate
     * @return Request with Authorization header added to it
     */
    @Nonnull
    Request authenticateRequest(@Nonnull final Request request);
}
