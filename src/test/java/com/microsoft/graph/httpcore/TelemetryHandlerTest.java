package com.microsoft.graph.httpcore;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

import com.microsoft.graph.CoreConstants;
import com.microsoft.graph.authentication.IAuthenticationProvider;
import org.junit.jupiter.api.Test;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TelemetryHandlerTest {
    @Test
    public void telemetryInitTest() {
        final GraphTelemetryHandler graphTelemetryHandler = new GraphTelemetryHandler();
        assertNotNull(graphTelemetryHandler);
    }

    @Test
    public void interceptTest() throws IOException {
        final String expectedHeader = CoreConstants.Headers.GraphVersionPrefix +"/"
                + CoreConstants.Headers.Version;
        final IAuthenticationProvider authProvider = mock(IAuthenticationProvider.class);
        when(authProvider.getAuthorizationTokenAsync(any(URL.class))).thenReturn(CompletableFuture.completedFuture(""));
        final OkHttpClient client = HttpClients.createDefault(authProvider);
        final Request request = new Request.Builder().url("https://graph.microsoft.com/v1.0/users/").build();
        final Response response = client.newCall(request).execute();
        assertNotNull(response);
        assertTrue(response.request().header(CoreConstants.Headers.SdkVersionHeaderName).contains(expectedHeader));
        assertTrue(!response.request().header(CoreConstants.Headers.SdkVersionHeaderName).contains(CoreConstants.Headers.AndroidVersionPrefix)); // Android version is not going to be present on unit tests runnning on java platform
        assertTrue(response.request().header(CoreConstants.Headers.SdkVersionHeaderName).contains(CoreConstants.Headers.JavaVersionPrefix));
    }

    @Test
    public void arrayInterceptorsTest() throws IOException {
        final IAuthenticationProvider authProvider = mock(IAuthenticationProvider.class);
        when(authProvider.getAuthorizationTokenAsync(any(URL.class))).thenReturn(CompletableFuture.completedFuture(""));
        final AuthenticationHandler authenticationHandler = new AuthenticationHandler(authProvider);
        final Interceptor[] interceptors = {new RetryHandler(), new RedirectHandler(), authenticationHandler};
        final OkHttpClient client = HttpClients.createFromInterceptors(interceptors);
        final String expectedHeader = CoreConstants.Headers.GraphVersionPrefix +"/"
                + CoreConstants.Headers.Version;
        final Request request = new Request.Builder().url("https://graph.microsoft.com/v1.0/users/").build();
        final Response response = client.newCall(request).execute();
        assertNotNull(response);
        assertTrue(response.request().header(CoreConstants.Headers.SdkVersionHeaderName).contains(expectedHeader));
    }

    @Test
    public void arrayInterceptorEmptyTest() throws IOException {
        final Interceptor[] interceptors = null;
        final OkHttpClient client = HttpClients.createFromInterceptors(interceptors);
        final String expectedHeader = CoreConstants.Headers.GraphVersionPrefix +"/"
                + CoreConstants.Headers.Version;
        final Request request = new Request.Builder().url("https://graph.microsoft.com/v1.0/users/").build();
        final Response response = client.newCall(request).execute();
        assertNotNull(response);
        assertTrue(response.request().header(CoreConstants.Headers.SdkVersionHeaderName).contains(expectedHeader));
    }

}
