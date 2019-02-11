package com.microsoft.graph.httpcore;

import static org.junit.Assert.assertTrue;

import org.apache.http.HttpRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Test;

public class HttpClientsTest {

	@Test
	public void testHttpClientCreation() {
		IAuthenticationProvider authprovider = new IAuthenticationProvider() {
			public void authenticateRequest(HttpRequest request) {
				 request.addHeader("Authorization", "Bearer " + "TOKEN");
			 }
		};
		CloseableHttpClient httpclient = HttpClients.createDefault(authprovider);
		assertTrue(httpclient != null);
	}

}
