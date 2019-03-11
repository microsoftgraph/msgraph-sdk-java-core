package com.microsoft.graph.httpcore;

import okhttp3.Request;

public interface ICoreAuthenticationProvider {
    /**
     * Authenticates the request
     * 
     * @param request the request to authenticate
     * @return Request with Authorization header added to it
     */
    Request authenticateRequest(Request request);
}
