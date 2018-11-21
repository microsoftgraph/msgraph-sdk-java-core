package com.microsoft.graph.httpcore;

import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.protocol.HttpContext;

public class AuthenticationHandler implements HttpRequestInterceptor {

	private IAuthenticationProvider authProvider;
	
	public AuthenticationHandler(IAuthenticationProvider authProvider) {
		this.authProvider = authProvider;
	}
	
	@Override
	public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
		// TODO Auto-generated method stub
		authProvider.authenticateRequest(request);
	}

}
