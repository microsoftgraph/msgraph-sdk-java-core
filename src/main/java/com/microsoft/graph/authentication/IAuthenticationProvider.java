package com.microsoft.graph.authentication;

import javax.annotation.Nonnull;

import com.microsoft.graph.exceptions.AuthenticationException;
import com.microsoft.graph.httpcore.IHttpRequest;

public interface IAuthenticationProvider {

    /**
     * Authenticates the request
     *
     * @param request the request to authenticate
     */
    void authenticateRequest(@Nonnull final IHttpRequest request) throws AuthenticationException;

}
