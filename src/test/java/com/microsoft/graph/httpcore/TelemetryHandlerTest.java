package com.microsoft.graph.httpcore;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import java.io.IOException;

import com.microsoft.graph.authentication.IAuthenticationProvider;
import org.junit.jupiter.api.Test;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TelemetryHandlerTest {
    @Test
    public void telemetryInitTest() {
        final TelemetryHandler telemetryHandler = new TelemetryHandler();
        assertNotNull(telemetryHandler);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void interceptTest() throws IOException {
        final String expectedHeader = TelemetryHandler.GRAPH_VERSION_PREFIX +"/"
                +TelemetryHandler.VERSION;
        final OkHttpClient client = HttpClients.createDefault(mock(IAuthenticationProvider.class));
        final Request request = new Request.Builder().url("https://graph.microsoft.com/v1.0/users/").build();
        final Response response = client.newCall(request).execute();
        assertNotNull(response);
        assertTrue(response.request().header(TelemetryHandler.SDK_VERSION).contains(expectedHeader));
        assertTrue(!response.request().header(TelemetryHandler.SDK_VERSION).contains(TelemetryHandler.ANDROID_VERSION_PREFIX)); // Android version is not going to be present on unit tests runnning on java platform
        assertTrue(response.request().header(TelemetryHandler.SDK_VERSION).contains(TelemetryHandler.JAVA_VERSION_PREFIX));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void arrayInterceptorsTest() throws IOException {

        final AuthenticationHandler authenticationHandler = new AuthenticationHandler(mock(IAuthenticationProvider.class));
        final Interceptor[] interceptors = {new RetryHandler(), new RedirectHandler(), authenticationHandler};
        final OkHttpClient client = HttpClients.createFromInterceptors(interceptors);
        final String expectedHeader = TelemetryHandler.GRAPH_VERSION_PREFIX +"/"
                +TelemetryHandler.VERSION;
        final Request request = new Request.Builder().url("https://graph.microsoft.com/v1.0/users/").build();
        final Response response = client.newCall(request).execute();
        assertNotNull(response);
        assertTrue(response.request().header(TelemetryHandler.SDK_VERSION).contains(expectedHeader));
    }

    @Test
    public void arrayInterceptorEmptyTest() throws IOException {
        final Interceptor[] interceptors = null;
        final OkHttpClient client = HttpClients.createFromInterceptors(interceptors);
        final String expectedHeader = TelemetryHandler.GRAPH_VERSION_PREFIX +"/"
                +TelemetryHandler.VERSION;
        final Request request = new Request.Builder().url("https://graph.microsoft.com/v1.0/users/").build();
        final Response response = client.newCall(request).execute();
        assertNotNull(response);
        assertTrue(response.request().header(TelemetryHandler.SDK_VERSION).contains(expectedHeader));
    }

}
