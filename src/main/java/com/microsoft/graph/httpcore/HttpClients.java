package com.microsoft.graph.httpcore;

import java.util.Arrays;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
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
        return new OkHttpClient.Builder()
                    .addInterceptor(new TelemetryHandler())
                    .followRedirects(false)
                    .protocols(Arrays.asList(Protocol.HTTP_1_1)); //https://stackoverflow.com/questions/62031298/sockettimeout-on-java-11-but-not-on-java-8
    }

    /**
     * Creates {@link OkHttpClient} instance with default
     * configuration and provided authProvider
     * 
     * @param auth Use IAuthenticationProvider instance provided while constructing http client
     * @return OkHttpClient build with authentication provider given, default redirect and default retry handlers 
     */
    public static OkHttpClient createDefault(ICoreAuthenticationProvider auth) {
        return custom()
                .addInterceptor(new AuthenticationHandler(auth))
    			.addInterceptor(new RetryHandler())
    			.addInterceptor(new RedirectHandler())
    			.build();
    }
    
    /**
     * Creates {@link OkHttpClient} instance with interceptors
     * 
     * @param interceptors Use interceptors provided while constructing http client
     * @return OkHttpClient build with interceptors provided 
     */
    public static OkHttpClient createFromInterceptors(Interceptor[] interceptors) {
    	OkHttpClient.Builder builder = custom();
    	if(interceptors != null)
    		for(Interceptor interceptor : interceptors) {
    			if(interceptor != null)
    				builder.addInterceptor(interceptor);
    		}
    	return builder.build();
    }
}
