package com.microsoft.graph.httpcore;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.junit.Test;

public class AuthenticationHandlerTest {
	
	static String token = "TEST-TOKEN";
	
	public static class AuthProvider implements IAuthenticationProvider{
		 public void authenticateRequest(HttpRequest request) {
			 request.addHeader("Authorization", "Bearer " + token);
		 }
	}

	@Test
	public void testAuthenticationHandler() {
		AuthProvider authProvider = new AuthProvider();
		AuthenticationHandler authHandler = new AuthenticationHandler(authProvider);
		HttpGet httpget = new HttpGet("https://graph.microsoft.com/v1.0/me/");
		HttpClientContext localContext = HttpClientContext.create();
		
		try {
			authHandler.process(httpget, localContext);
			Header header = httpget.getFirstHeader("Authorization");
			assertTrue(header.getValue().equals("Bearer " + token));
		} catch (HttpException | IOException e) {
			e.printStackTrace();
			fail("Authentication handler failure");
		}
	}

}
