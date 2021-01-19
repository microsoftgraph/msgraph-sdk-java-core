package com.microsoft.graph.authentication;

import javax.annotation.Nonnull;

import com.microsoft.graph.exceptions.AuthenticationException;
import com.microsoft.graph.httpcore.IHttpRequest;


/**
 * Authenticates requests to be sent to the API
 * @param <NativeRequestType> the type of the http native http request from the http client library
 */
public interface IAuthenticationProvider<NativeRequestType> {
    /**
     * Authenticates the request
     *
     * @param request the request to authenticate
     */
    void authenticateRequest(@Nonnull final IHttpRequest request) throws AuthenticationException;
    /**
     * Authenticates the request
     *
     * @param request the request to authenticate
     * @return Request with Authorization header added to it
     */
    @Nonnull
    NativeRequestType authenticateRequest(@Nonnull final NativeRequestType request) throws AuthenticationException;
}
