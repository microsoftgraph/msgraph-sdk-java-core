package com.microsoft.graph.httpcore;

import okhttp3.Interceptor;
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
        return new OkHttpClient.Builder().addInterceptor(new TelemetryHandler());
    }

    /**
     * Creates {@link OkHttpClient} instance with default
     * configuration and provided authProvider
     * 
     * @param auth Use IAuthenticationProvider instance provided while constructing http client
     * @return OkHttpClient build with authentication provider given, default redirect and default retry handlers 
     */
    public static OkHttpClient createDefault(ICoreAuthenticationProvider auth) {
    	return new OkHttpClient.Builder().addInterceptor(new AuthenticationHandler(auth))
    			.followRedirects(false)
    			.addInterceptor(new RetryHandler())
    			.addInterceptor(new RedirectHandler())
    			.addInterceptor(new TelemetryHandler())
    			.build();
    }
    
    /**
     * Creates {@link OkHttpClient} instance with interceptors
     * 
     * @param interceptors Use interceptors provided while constructing http client
     * @return OkHttpClient build with interceptors provided 
     */
    public static OkHttpClient createFromInterceptors(Interceptor[] interceptors) {
    	OkHttpClient.Builder builder = new OkHttpClient.Builder();
    	if(interceptors != null)
    		for(Interceptor interceptor : interceptors) {
    			if(interceptor != null)
    				builder.addInterceptor(interceptor);
    		}
    	builder.addInterceptor(new TelemetryHandler());
    	return builder.build();
    }
}
