package com.microsoft.graph.httpcore;

import org.apache.http.HttpRequest;

public interface IAuthenticationProvider {
	/**
     * Authenticates the request
     * 
     * @param request the request to authenticate
     */
    void authenticateRequest(final HttpRequest request);
}
