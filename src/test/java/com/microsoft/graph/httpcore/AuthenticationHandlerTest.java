package com.microsoft.graph.httpcore;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.junit.Test;

import okhttp3.Request;
import okhttp3.internal.http2.Header;

public class AuthenticationHandlerTest {
	
	static String token = "TEST-TOKEN";
	
	public static class AuthProvider implements IAuthenticationProvider{
		 public Request authenticateRequest(Request request) {
			 Request newRequest = request.newBuilder().addHeader("Authorization", "Bearer " + token).build();
			 return newRequest;
		 }
	}

	@Test
	public void testAuthenticationHandler() {
		AuthProvider authProvider = new AuthProvider();
		AuthenticationHandler authHandler = new AuthenticationHandler(authProvider);
		Request request = new Request.Builder().url("https://graph.microsoft.com/v1.0/me/").build();
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
