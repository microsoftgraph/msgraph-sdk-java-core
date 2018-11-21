package com.microsoft.graph.httpcore;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

public class HttpClients {
    private HttpClients() {
        super();
    }

    /**
     * Creates builder object for construction of custom
     * {@link CloseableHttpClient} instances.
     */
    public static HttpClientBuilder custom() {
        return HttpClientBuilder.create();
    }

    /**
     * Creates {@link CloseableHttpClient} instance with default
     * configuration.
     */
    public static CloseableHttpClient createDefault() {
        //return HttpClientBuilder.create().build();
    	
    	RequestConfig config = RequestConfig.custom().setMaxRedirects(5).build();
    	
    	IAuthenticationProvider auth = null;
    	return HttpClientBuilder.create().addInterceptorFirst(new AuthenticationHandler(null))
    			.setRedirectStrategy(new RedirectHandler())
    			.setServiceUnavailableRetryStrategy(new RetryHandler())
    			.setDefaultRequestConfig(config)
    			.build();
    }

    /**
     * Creates {@link CloseableHttpClient} instance with default
     * configuration based on system properties.
     */
    public static CloseableHttpClient createSystem() {
        return HttpClientBuilder.create().useSystemProperties().build();
    }
}
