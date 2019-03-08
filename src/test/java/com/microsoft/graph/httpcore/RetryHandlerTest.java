package com.microsoft.graph.httpcore;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.HttpURLConnection;

import org.junit.Test;

import com.microsoft.graph.httpcore.middlewareoption.IShouldRetry;
import com.microsoft.graph.httpcore.middlewareoption.RetryOptions;

import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RetryHandlerTest {

	int maxRetries = 2;
	int retryInterval = 1000;
	String testmeurl = "https://graph.microsoft.com/v1.0/me";
	private final int HTTP_SERVER_ERROR = 500;

	@Test
	public void testRetryHandlerCreation() {
		RetryHandler retryhandler = new RetryHandler();
		assertNotNull(retryhandler);
	}

	@Test
	public void testRetryHandlerWithRetryOptions() {
		RetryOptions option = new RetryOptions();
		RetryHandler retryhandler = new RetryHandler(option);
		Request httpget = new Request.Builder().url(testmeurl).build();
		Response response = new Response.Builder()
				.protocol(Protocol.HTTP_1_1)
				.code(HttpURLConnection.HTTP_GATEWAY_TIMEOUT)
				.message("Gateway Timeout")
				.request(httpget)
				.build();
		assertTrue(retryhandler.retryRequest(response, 1, httpget, option));
	}

	@Test
	public void testRetryHandlerWithCustomRetryOptions() {
		IShouldRetry shouldRetry = new IShouldRetry() {
			public boolean shouldRetry(Response response, int executionCount, Request request, long delay){
				return false;
			}
		};
		RetryOptions option = new RetryOptions(shouldRetry, 5, 0);
		RetryHandler retryhandler = new RetryHandler(option);
		Request httpget = new Request.Builder().url(testmeurl).build();
		Response response = new Response.Builder()
				.protocol(Protocol.HTTP_1_1)
				.code(HttpURLConnection.HTTP_GATEWAY_TIMEOUT)
				.message("Gateway Timeout")
				.request(httpget)
				.build();
		assertTrue(!retryhandler.retryRequest(response, 0, httpget, option));
	}

	@Test
	public void testRetryRequestWithMaxRetryAttempts() {
		RetryHandler retryhandler = new RetryHandler();
		Request httpget = new Request.Builder().url(testmeurl).build();
		Response response = new Response.Builder()
				.protocol(Protocol.HTTP_1_1)
				.code(HttpURLConnection.HTTP_GATEWAY_TIMEOUT)
				.message("Gateway Timeout")
				.request(httpget)
				.build();
		// Default retry options with Number of maxretries default to 3
		RetryOptions retryOptions = new RetryOptions();
		// Try to execute one more than allowed default max retries
		int executionCount = RetryOptions.DEFAULT_MAX_RETRIES + 1; 
		assertFalse(retryhandler.retryRequest(response, executionCount, httpget, retryOptions));
	}

	@Test
	public void testRetryRequestForStatusCode() {
		RetryHandler retryhandler = new RetryHandler();
		Request httpget = new Request.Builder().url(testmeurl).build();
		Response response = new Response.Builder()
				.protocol(Protocol.HTTP_1_1)
				// For status code 500 which is not in (429 503 504), So NO retry
				.code(HTTP_SERVER_ERROR)  
				.message( "Internal Server Error")
				.request(httpget)
				.build();
		assertFalse(retryhandler.retryRequest(response, 1, httpget, new RetryOptions()));
	}

	@Test
	public void testRetryRequestWithTransferEncoding() {
		RetryHandler retryhandler = new RetryHandler();
		Request httppost = new Request.Builder().url(testmeurl).post(RequestBody.create(MediaType.parse("application/json"), "TEST")).build();
		Response response = new Response.Builder()
				.protocol(Protocol.HTTP_1_1)
				.code(HttpURLConnection.HTTP_GATEWAY_TIMEOUT)  
				.message( "gateway timeout")
				.request(httppost)
				.addHeader("Transfer-Encoding", "chunked")
				.build();
		assertTrue(retryhandler.retryRequest(response, 1, httppost, new RetryOptions()));
	}

	@Test
	public void testRetryRequestWithExponentialBackOff() {
		RetryHandler retryhandler = new RetryHandler();
		Request httppost = new Request.Builder().url(testmeurl).post(RequestBody.create(MediaType.parse("application/json"), "TEST")).build();
		Response response = new Response.Builder()
				.protocol(Protocol.HTTP_1_1)
				.code(HttpURLConnection.HTTP_GATEWAY_TIMEOUT)  
				.message( "gateway timeout")
				.request(httppost)
				.addHeader("Transfer-Encoding", "chunked")
				.build();
		
		assertTrue(retryhandler.retryRequest(response, 1, httppost, new RetryOptions()));
	}
}
