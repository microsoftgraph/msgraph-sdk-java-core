package com.microsoft.graph.authentication;

import okhttp3.Request;

import javax.annotation.Nonnull;

import com.microsoft.graph.exceptions.AuthenticationException;

public interface ICoreAuthenticationProvider {
    /**
     * Authenticates the request
     * 
     * @param request the request to authenticate
     * @return Request with Authorization header added to it
     */
    @Nonnull
    Request authenticateRequest(@Nonnull final Request request) throws AuthenticationException;
}
