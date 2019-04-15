package com.microsoft.graph.httpcore;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@Ignore
public class TelemetryHandlerTest {
	@Test
	public void telemetryInitTest() {
		TelemetryHandler telemetryHandler = new TelemetryHandler();
		assertNotNull(telemetryHandler);
	}
	
	@Test
	public void interceptTest() throws IOException {
		String expectedHeader = TelemetryHandler.SDK_VERSION + TelemetryHandler.GRAPH_VERSION_PREFIX +"/"
				+TelemetryHandler.VERSION;
		OkHttpClient client = HttpClients.createDefault(new ICoreAuthenticationProvider() {
			@Override
			public Request authenticateRequest(Request request) {
				return request;
			}
		});
		Request request = new Request.Builder().url("https://graph.microsoft.com/v1.0/users/").build();
		Response response = client.newCall(request).execute();
		assertNotNull(response);
		assertTrue(response.request().header("SdkVersion").contains(expectedHeader));
	}
	
	@Test
	public void arrayInterceptorsTest() throws IOException {
		
		AuthenticationHandler authenticationHandler = new AuthenticationHandler(new ICoreAuthenticationProvider() {
			
			@Override
			public Request authenticateRequest(Request request) {
				return request;
			}
		});
		Interceptor[] interceptors = {new RetryHandler(), new RedirectHandler(), authenticationHandler};
		OkHttpClient client = HttpClients.createFromInterceptors(interceptors);
		String expectedHeader = TelemetryHandler.SDK_VERSION + TelemetryHandler.GRAPH_VERSION_PREFIX +"/"
				+TelemetryHandler.VERSION;
		Request request = new Request.Builder().url("https://graph.microsoft.com/v1.0/users/").build();
		Response response = client.newCall(request).execute();
		assertNotNull(response);
		assertTrue(response.request().header("SdkVersion").contains(expectedHeader));
	}
}
