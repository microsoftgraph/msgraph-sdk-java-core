package com.microsoft.graph.httpcore;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import com.microsoft.graph.authentication.IAuthenticationProvider;
import org.junit.Test;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

public class HttpClientsTest {

    @Test
    @SuppressWarnings("unchecked")
    public void testHttpClientCreation() {
        OkHttpClient httpclient = HttpClients.createDefault(mock(IAuthenticationProvider.class));
        assertTrue(httpclient != null);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void arrayInterceptorsTest() {
        AuthenticationHandler authenticationHandler = new AuthenticationHandler(mock(IAuthenticationProvider.class));
        Interceptor[] interceptors = {new RetryHandler(), new RedirectHandler(), authenticationHandler};
        OkHttpClient client = HttpClients.createFromInterceptors(interceptors);
        assertTrue(client.interceptors().size()==4);
    }

}
