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
import java.util.ArrayList;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.microsoft.graph.core.authentication.AzureIdentityAccessTokenProvider;
import com.microsoft.graph.core.authentication.AzureIdentityAuthenticationProvider;
import com.microsoft.graph.core.requests.middleware.GraphTelemetryHandler;
import com.microsoft.kiota.RequestOption;
import com.microsoft.kiota.authentication.AccessTokenProvider;
import com.microsoft.kiota.authentication.AllowedHostsValidator;
import com.microsoft.kiota.authentication.BaseBearerTokenAuthenticationProvider;
import com.microsoft.kiota.http.middleware.RedirectHandler;
import com.microsoft.kiota.http.middleware.RetryHandler;
import com.microsoft.kiota.http.middleware.options.RetryHandlerOption;

import okhttp3.Interceptor;
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

    @Test
    void testCreateWithAuthenticationProviderAndCustomRequestOptions() throws IOException {
        final BaseBearerTokenAuthenticationProvider mockAuthenticationProvider =
                getMockAuthenticationProvider();
        var requestOptions = new ArrayList<RequestOption>();
        requestOptions.add(new RetryHandlerOption(null, 0, 0));
        OkHttpClient graphClient = GraphClientFactory.create(mockAuthenticationProvider, requestOptions.toArray(new RequestOption[0])).addInterceptor(new MockResponseHandler()).build();

        var interceptors = graphClient.interceptors();
        for (Interceptor interceptor : interceptors) {
            if (interceptor instanceof RetryHandler) {
                RetryHandlerOption retryOptions = ((RetryHandler) interceptor).getRetryOptions();
                Assertions.assertEquals(0, retryOptions.maxRetries());
                Assertions.assertEquals(0, retryOptions.delay());
            }
        }

        Request request = new Request.Builder().url("https://graph.microsoft.com/v1.0/me")
                .addHeader("CustomHeader", "CustomValue").build();
        Response response = graphClient.newCall(request).execute();

        assertEquals(200, response.code());
        assertNotNull(response.request());
        assertTrue(response.request().headers().names().contains("Authorization"));
        assertTrue(response.request().headers().names().contains("CustomHeader"));
        assertEquals("Bearer " + ACCESS_TOKEN_STRING, response.request().header("Authorization"));
        assertEquals("CustomValue", response.request().header("CustomHeader"));
    }

    @Test
    void testCreateWithCustomInterceptorsOverwritesDefaults() throws IOException {

        final Interceptor[] interceptors = {new GraphTelemetryHandler(), getDisabledRetryHandler(),
            new RedirectHandler()};
        final OkHttpClient client = GraphClientFactory.create(interceptors).build();
        final Request request = new Request.Builder().url("https://graph.microsoft.com/v1.0/users/").build();
        client.newCall(request).execute();

        for (Interceptor clientInterceptor : client.interceptors()) {
            if (clientInterceptor instanceof RetryHandler) {
                RetryHandlerOption retryOptions = ((RetryHandler) clientInterceptor).getRetryOptions();
                Assertions.assertEquals(0, retryOptions.maxRetries());
                Assertions.assertEquals(0, retryOptions.delay());

            }

            assertTrue(clientInterceptor instanceof GraphTelemetryHandler
                || clientInterceptor instanceof RedirectHandler
                || clientInterceptor instanceof RetryHandler);
        }
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

    private static @NotNull RetryHandler getDisabledRetryHandler() {
        RetryHandlerOption retryHandlerOption = new RetryHandlerOption(
            (delay, executionCount, request, response) -> false, 0, 0);
        RetryHandler retryHandler = new RetryHandler(retryHandlerOption);
        return retryHandler;
    }
}
