package com.microsoft.graph.core.requests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URI;

import org.junit.jupiter.api.Test;

import com.microsoft.graph.core.authentication.AzureIdentityAccessTokenProvider;
import com.microsoft.graph.core.authentication.AzureIdentityAuthenticationProvider;
import com.microsoft.kiota.authentication.AccessTokenProvider;
import com.microsoft.kiota.authentication.AllowedHostsValidator;
import com.microsoft.kiota.authentication.BaseBearerTokenAuthenticationProvider;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

class GraphClientFactoryTest {

    private static final String ACCESS_TOKEN_STRING = "token";

    @Test
    void testCreateWithAuthenticationProvider() throws IOException {
        final BaseBearerTokenAuthenticationProvider mockAuthenticationProvider =
                getMockAuthenticationProvider();
        OkHttpClient graphClient = GraphClientFactory.create(mockAuthenticationProvider).addInterceptor(new MockResponseHandler()).build();

        Request request = new Request.Builder().url("https://graph.microsoft.com/v1.0/me").build();
        Response response = graphClient.newCall(request).execute();

        assertEquals(200, response.code());
        assertNotNull(response.request());
        assertTrue(response.request().headers().names().contains("Authorization"));
        assertEquals("Bearer " + ACCESS_TOKEN_STRING, response.request().header("Authorization"));
    }

    private static BaseBearerTokenAuthenticationProvider getMockAuthenticationProvider() {
        final AccessTokenProvider mockAccessTokenProvider = mock(AzureIdentityAccessTokenProvider.class);
        when(mockAccessTokenProvider.getAuthorizationToken(any(URI.class), anyMap()))
                .thenReturn(ACCESS_TOKEN_STRING);
        when(mockAccessTokenProvider.getAllowedHostsValidator()).thenReturn(new AllowedHostsValidator("graph.microsoft.com"));
        final AzureIdentityAuthenticationProvider mockAuthenticationProvider =
                mock(AzureIdentityAuthenticationProvider.class);
        when(mockAuthenticationProvider.getAccessTokenProvider())
                .thenReturn(mockAccessTokenProvider);
        return mockAuthenticationProvider;
    }
}
