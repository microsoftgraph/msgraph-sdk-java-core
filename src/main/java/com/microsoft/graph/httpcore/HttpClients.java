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
     */
    public static Builder custom() {
        return new OkHttpClient.Builder();
    }

    /**
     * Creates {@link OkHttpClient} instance with default
     * configuration and provided authProvider
     */
    public static OkHttpClient createDefault(IAuthenticationProvider auth) {
    	
    	return new OkHttpClient.Builder().addInterceptor(new AuthenticationHandler(auth))
    			.addInterceptor(new RetryHandler())
    			.addInterceptor(new RedirectHandler())
    			.build();
    }
}
