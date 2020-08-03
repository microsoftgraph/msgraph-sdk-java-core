package com.microsoft.graph.authentication;

import com.microsoft.graph.httpcore.IHttpRequest;

public interface IAuthenticationProvider {

    /**
     * Authenticates the request
     *
     * @param request the request to authenticate
     */
    void authenticateRequest(final IHttpRequest request);

}
