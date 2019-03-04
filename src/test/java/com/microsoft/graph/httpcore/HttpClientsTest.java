package com.microsoft.graph.httpcore;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class HttpClientsTest {

	@Test
	public void testHttpClientCreation() {
		IAuthenticationProvider authprovider = new IAuthenticationProvider() {
			public Request authenticateRequest(Request request) {
				Request newRequest = request.newBuilder().addHeader("Authorization", "Bearer " + "TOKEN").build();
				return newRequest;
			}
		};
		
		OkHttpClient httpclient = HttpClients.createDefault(authprovider);
		assertTrue(httpclient != null);
	}

}
