package com.microsoft.graph.httpcore;

import static org.junit.Assert.assertTrue;

import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Test;

public class HttpClientsTest {

	@Test
	public void testHttpClientCreation() {
		IAuthenticationProvider authprovider = new IAuthenticationProvider() {
			@Override
			public String getAccessToken() {
				return "TOKEN";
			}
		};
		CloseableHttpClient httpclient = HttpClients.createDefault(authprovider);
		assertTrue(httpclient != null);
	}

}
