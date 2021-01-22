package com.microsoft.graph.httpcore;

import com.microsoft.graph.authentication.IAuthenticationProvider;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URL;
import java.util.concurrent.CompletableFuture;

import org.junit.Test;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class AuthenticationHandlerTest {
    @Test
    public void testAuthenticationHandler() throws Exception {
        IAuthenticationProvider authProvider = mock(IAuthenticationProvider.class);
        when(authProvider.getAuthorizationTokenAsync(any(URL.class))).thenReturn(CompletableFuture.completedFuture("a token"));
        AuthenticationHandler authHandler = new AuthenticationHandler(authProvider);
        Request request = new Request.Builder().url("https://graph.microsoft.com/v1.0/me/").build();
        OkHttpClient client = new OkHttpClient().newBuilder().addInterceptor(authHandler).build();
        client.newCall(request).execute();
        verify(authProvider, times(1)).getAuthorizationTokenAsync(any(URL.class));
    }

}
