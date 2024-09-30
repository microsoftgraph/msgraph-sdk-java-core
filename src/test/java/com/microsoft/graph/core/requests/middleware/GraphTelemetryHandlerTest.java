package com.microsoft.graph.core.requests.middleware;

import com.microsoft.graph.core.CoreConstants;
import com.microsoft.graph.core.requests.GraphClientFactory;
import com.microsoft.graph.core.requests.options.GraphClientOption;
import com.microsoft.kiota.http.middleware.RedirectHandler;
import com.microsoft.kiota.http.middleware.RetryHandler;

import com.microsoft.kiota.http.middleware.options.RetryHandlerOption;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

class GraphTelemetryHandlerTest {

    private static final String defaultSDKVersion = "graph-java";

    @Test
    void telemetryHandlerDefaultTests() throws IOException {
        final String expectedCore = CoreConstants.Headers.GRAPH_VERSION_PREFIX + "/" + CoreConstants.Headers.VERSION;

        final OkHttpClient client = GraphClientFactory.create().build();
        final Request request = new Request.Builder().url("https://graph.microsoft.com/v1.0/users/").build();
        final Response response = client.newCall(request).execute();

        assertNotNull(response);
        assertTrue(response.request().header(CoreConstants.Headers.SDK_VERSION_HEADER_NAME).contains(expectedCore));
        assertTrue(!response.request().header(CoreConstants.Headers.SDK_VERSION_HEADER_NAME).contains(
            CoreConstants.Headers.ANDROID_VERSION_PREFIX)); // Android version is not going to be present on unit tests running on java platform
        assertTrue(
            response.request().header(CoreConstants.Headers.SDK_VERSION_HEADER_NAME).contains(defaultSDKVersion));
    }

    @Test
    void arrayInterceptorsTest() throws IOException {
        final String expectedCore = CoreConstants.Headers.GRAPH_VERSION_PREFIX + "/" + CoreConstants.Headers.VERSION;

        final Interceptor[] interceptors = {new GraphTelemetryHandler(), new RetryHandler(),
            new RedirectHandler()};
        final OkHttpClient client = GraphClientFactory.create(interceptors).build();
        final Request request = new Request.Builder().url("https://graph.microsoft.com/v1.0/users/").build();
        final Response response = client.newCall(request).execute();

        assertNotNull(response);
        assertTrue(response.request().header(CoreConstants.Headers.SDK_VERSION_HEADER_NAME).contains(expectedCore));
        assertTrue(
            response.request().header(CoreConstants.Headers.SDK_VERSION_HEADER_NAME).contains(defaultSDKVersion));
    }

    @Test
    void arrayInterceptorEmptyTest() throws IOException {
        final String expectedCore = CoreConstants.Headers.GRAPH_VERSION_PREFIX + "/" + CoreConstants.Headers.VERSION;

        final Interceptor[] interceptors = new Interceptor[]{};
        final OkHttpClient client = GraphClientFactory.create(interceptors).build();
        final Request request = new Request.Builder().url("https://graph.microsoft.com/v1.0/users/").build();
        final Response response = client.newCall(request).execute();

        assertNotNull(response);
        assertTrue(response.request().header(CoreConstants.Headers.SDK_VERSION_HEADER_NAME).contains(expectedCore));
        assertTrue(
            response.request().header(CoreConstants.Headers.SDK_VERSION_HEADER_NAME).contains(defaultSDKVersion));
    }

    @Test
    void testClientOptions() throws IOException {
        String requestId = "1234567890";
        String coreLibVer = "3.1.1";
        String clientLibVer = "6.1.1";
        String serviceLibVer = "beta";

        final GraphClientOption graphClientOption = new GraphClientOption();
        graphClientOption.setClientRequestId(requestId);
        graphClientOption.setCoreLibraryVersion(coreLibVer);
        graphClientOption.setClientLibraryVersion(clientLibVer);
        graphClientOption.setGraphServiceTargetVersion(serviceLibVer);

        final String expectedCoreVer =
            CoreConstants.Headers.GRAPH_VERSION_PREFIX + "/" + coreLibVer;
        final String expectedClientEndpoint =
            CoreConstants.Headers.JAVA_VERSION_PREFIX + "-" + serviceLibVer + "/" + clientLibVer;

        final OkHttpClient client = GraphClientFactory.create(graphClientOption).build();
        final Request request = new Request.Builder().url("https://graph.microsoft.com/v1.0/users/").build();
        final Response response = client.newCall(request).execute();

        assertTrue(response.request().header(CoreConstants.Headers.SDK_VERSION_HEADER_NAME).contains(expectedCoreVer));
        assertTrue(
            response.request().header(CoreConstants.Headers.SDK_VERSION_HEADER_NAME).contains(expectedClientEndpoint));
        assertTrue(response.request().header(CoreConstants.Headers.CLIENT_REQUEST_ID).contains(requestId));
    }
}


