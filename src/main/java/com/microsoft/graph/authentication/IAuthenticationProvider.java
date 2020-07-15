package com.microsoft.graph.authentication;

//This should be deleted later once IHttpRequest is moved to the core library
import com.microsoft.graph.httpcore.IHttpRequest;

public interface IAuthenticationProvider {

    /**
     * Authenticates the request
     *
     * @param request the request to authenticate
     */
    void authenticateRequest(final IHttpRequest request);

}
