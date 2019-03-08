package com.microsoft.graph.httpcore;

import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;

public class HttpClients {
    private HttpClients() {
        super();
    }

    /**
     * Creates builder object for construction of custom
     * {@link OkHttpClient} instances.
     * 
     * @return OkHttpClient.Builder() custom builder for developer to add its own interceptors to it
     */
    public static Builder custom() {
        return new OkHttpClient.Builder();
    }

    /**
     * Creates {@link OkHttpClient} instance with default
     * configuration and provided authProvider
     * 
     * @param auth Use IAuthenticationProvider instance provided while constructing http client
     * @return OkHttpClient build with authentication provider given, default redirect and default retry handlers 
     */
    public static OkHttpClient createDefault(IAuthenticationProvider auth) {
    	return new OkHttpClient.Builder().addInterceptor(new AuthenticationHandler(auth))
    			.followRedirects(false)
    			.addInterceptor(new RetryHandler())
    			.addInterceptor(new RedirectHandler())
    			.build();
    }
}
