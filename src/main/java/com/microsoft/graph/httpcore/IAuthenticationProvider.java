package com.microsoft.graph.httpcore;

import okhttp3.Request;

public interface IAuthenticationProvider {
    /**
     * Authenticates the request
     * 
     * @param request the request to authenticate
     */
    Request authenticateRequest(Request request);
}
