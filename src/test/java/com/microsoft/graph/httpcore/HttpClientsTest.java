package com.microsoft.graph.httpcore;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class HttpClientsTest {

	@Test
	public void testHttpClientCreation() {
		ICoreAuthenticationProvider authprovider = new ICoreAuthenticationProvider() {
			public Request authenticateRequest(Request request) {
				Request newRequest = request.newBuilder().addHeader("Authorization", "Bearer " + "TOKEN").build();
				return newRequest;
			}
		};
		
		OkHttpClient httpclient = HttpClients.createDefault(authprovider);
		assertTrue(httpclient != null);
	}
	
	@Test
	public void arrayInterceptorsTest() {
		AuthenticationHandler authenticationHandler = new AuthenticationHandler(new ICoreAuthenticationProvider() {
			@Override
			public Request authenticateRequest(Request request) {
				return request;
			}
		});
		Interceptor[] interceptors = {new RetryHandler(), new RedirectHandler(), authenticationHandler};
		OkHttpClient client = HttpClients.createFromInterceptors(interceptors);
		assertTrue(client.interceptors().size()==4);
	}

}
