package com.microsoft.graph.httpcore;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

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
	public void interceptTest() throws IOException {
		final String expectedHeader = TelemetryHandler.GRAPH_VERSION_PREFIX +"/"
				+TelemetryHandler.VERSION;
		final OkHttpClient client = HttpClients.createDefault(new ICoreAuthenticationProvider() {
			@Override
			public Request authenticateRequest(Request request) {
				return request;
			}
		});
		final Request request = new Request.Builder().url("https://graph.microsoft.com/v1.0/users/").build();
		final Response response = client.newCall(request).execute();
		assertNotNull(response);
		assertTrue(response.request().header(TelemetryHandler.SDK_VERSION).contains(expectedHeader));
		assertTrue(response.request().header(TelemetryHandler.SDK_VERSION).contains(TelemetryHandler.ANDROID_VERSION_PREFIX));
		assertTrue(response.request().header(TelemetryHandler.SDK_VERSION).contains(TelemetryHandler.JAVA_VERSION_PREFIX));
	}

	@Test
	public void arrayInterceptorsTest() throws IOException {

		final AuthenticationHandler authenticationHandler = new AuthenticationHandler(new ICoreAuthenticationProvider() {

			@Override
			public Request authenticateRequest(Request request) {
				return request;
			}
		});
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
