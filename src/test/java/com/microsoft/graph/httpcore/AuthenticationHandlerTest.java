package com.microsoft.graph.httpcore;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Ignore;
import org.junit.Test;

import okhttp3.Request;
import okhttp3.internal.http.RealInterceptorChain;

@Ignore
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
		
		RealInterceptorChain chain = new RealInterceptorChain(null, null, null, null, 0, request, null, null, 0, 0, 0);
		
		try {
			authHandler.intercept(chain);
			
			String value = request.header("Authorization");
			assertTrue(value.equals("Bearer " + token));
			
			
		} catch (Exception e) {
			e.printStackTrace();
			fail("Authentication handler failure");
		}
	}

}
