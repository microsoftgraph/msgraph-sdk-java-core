package com.microsoft.graph.httpcore;

import org.apache.http.HttpRequest;

public interface IAuthenticationProvider {
	/**
     * Get Access Token
     * 
     */
    
    String getAccessToken();
}
