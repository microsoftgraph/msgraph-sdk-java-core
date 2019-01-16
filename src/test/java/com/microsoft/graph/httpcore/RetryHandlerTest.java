package com.microsoft.graph.httpcore;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.protocol.HttpCoreContext;
import org.junit.Test;

public class RetryHandlerTest {

	@Test
	public void testRetryHandlerCreation() {
		RetryHandler retryhandler = new RetryHandler(2, 2000);
		assertTrue(retryhandler.getRetryInterval() == 2000);
	}
	
	@Test
	public void testRetryRequestWithMaxRetryAttempts() {
		RetryHandler retryhandler = new RetryHandler(2, 2000);
		HttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_GATEWAY_TIMEOUT, "Gateway Timeout");
		HttpClientContext localContext = HttpClientContext.create();
		assertFalse(retryhandler.retryRequest(response, 3, localContext));
	}
	
	@Test
	public void testRetryRequestForStatusCode() {
		RetryHandler retryhandler = new RetryHandler(2, 2000);
		HttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_INTERNAL_SERVER_ERROR, "Internal Server Error");
		HttpClientContext localContext = HttpClientContext.create();
		assertFalse(retryhandler.retryRequest(response, 1, localContext));
	}
	
	@Test
	public void testRetryRequestWithTransferEncoding() {
		RetryHandler retryhandler = new RetryHandler(2, 2000);
		HttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_GATEWAY_TIMEOUT, "Internal Server Error");
		response.setHeader("Transfer-Encoding", "chunked");
		HttpPost httppost = new HttpPost("https://graph.microsoft.com/v1.0/");
		
		try {
			HttpEntity entity = new StringEntity("TEST");
			httppost.setEntity(entity);
			HttpClientContext localContext = HttpClientContext.create();
			localContext.setAttribute(HttpCoreContext.HTTP_REQUEST, httppost);
			assertFalse(retryhandler.retryRequest(response, 1, localContext));
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			fail("Retry handler testRetryHandlerRetryRequest3 test failure");
		} 
	}
	
	@Test
	public void testRetryRequestWithExponentialBackOff() {
		RetryHandler retryhandler = new RetryHandler(2, 2000);
		HttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_GATEWAY_TIMEOUT, "Internal Server Error");
		HttpPost httppost = new HttpPost("https://graph.microsoft.com/v1.0/");
		
		try {
			HttpEntity entity = new StringEntity("TEST");
			httppost.setEntity(entity);
			HttpClientContext localContext = HttpClientContext.create();
			localContext.setAttribute(HttpCoreContext.HTTP_REQUEST, httppost);
			assertTrue(retryhandler.retryRequest(response, 1, localContext));
			assertTrue(retryhandler.getRetryInterval() == 20);
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			fail("Retry handler testRetryHandlerRetryRequest3 test failure");
		} 
	}
	
	@Test
	public void testRetryHandlerRetryRequestWithRetryAfterHeader() {
		RetryHandler retryhandler = new RetryHandler(2, 2000);
		HttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_GATEWAY_TIMEOUT, "Internal Server Error");
		response.setHeader("Retry-After", "100");
		HttpPost httppost = new HttpPost("https://graph.microsoft.com/v1.0/");
		
		try {
			HttpEntity entity = new StringEntity("TEST");
			httppost.setEntity(entity);
			HttpClientContext localContext = HttpClientContext.create();
			localContext.setAttribute(HttpCoreContext.HTTP_REQUEST, httppost);
			assertTrue(retryhandler.retryRequest(response, 1, localContext));
			assertTrue(retryhandler.getRetryInterval() == 100);
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			fail("Retry handler testRetryHandlerRetryRequestWithRetryAfterHeader test failure");
		} 
	}

}
