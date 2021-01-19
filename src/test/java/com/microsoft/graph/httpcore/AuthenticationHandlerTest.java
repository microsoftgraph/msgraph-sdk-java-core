package com.microsoft.graph.httpcore;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.microsoft.graph.authentication.ICoreAuthenticationProvider;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.anyObject;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Test;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AuthenticationHandlerTest {
    @Test
    public void testAuthenticationHandler() throws Exception {
        ICoreAuthenticationProvider authProvider = mock(ICoreAuthenticationProvider.class);
        AuthenticationHandler authHandler = new AuthenticationHandler(authProvider);
        Request request = new Request.Builder().url("https://graph.microsoft.com/v1.0/me/").build();
        OkHttpClient client = new OkHttpClient().newBuilder().addInterceptor(authHandler).build();
        Response response = client.newCall(request).execute();
        verify(authProvider, times(1)).authenticateRequest(anyObject());
    }

}
