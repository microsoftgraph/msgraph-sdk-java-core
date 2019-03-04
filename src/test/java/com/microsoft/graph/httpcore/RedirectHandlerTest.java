package com.microsoft.graph.httpcore;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.ProtocolException;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.message.BasicHttpResponse;
import org.junit.Test;

import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.http2.Header;

public class RedirectHandlerTest {
	
	String testmeurl = "https://graph.microsoft.com/v1.0/me/";
	String testurl = "https://graph.microsoft.com/v1.0/";
	String differenthosturl = "https://graph.abc.com/v1.0/";

	@Test
	public void testIsRedirectedFailureByNoLocationHeader() {
		RedirectHandler redirectHandler = RedirectHandler.INSTANCE;
		Request httpget = new Request.Builder().url(testmeurl).build();
		Response response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_MOVED_TEMPORARILY, "Moved Temporarily");
		HttpClientContext localContext = HttpClientContext.create();
		try {
			boolean isRedirected = redirectHandler.isRedirected(httpget, response, localContext);
			assertTrue(!isRedirected);
		} catch (ProtocolException e) {
			e.printStackTrace();
			fail("Redirect handler testIsRedirectedFailure failure");
		}
	}
	
	@Test
	public void testIsRedirectedFailureByStatusCodeBadRequest() {
		RedirectHandler redirectHandler = RedirectHandler.INSTANCE;
		Request httpget = new Request.Builder().url(testmeurl).build();
		Response response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_BAD_REQUEST, "Bad Request");
		response.setHeader("location", testmeurl);
		HttpClientContext localContext = HttpClientContext.create();
		try {
			boolean isRedirected = redirectHandler.isRedirected(httpget, response, localContext);
			assertTrue(!isRedirected);
		} catch (ProtocolException e) {
			e.printStackTrace();
			fail("Redirect handler testIsRedirectedFailure1 failure");
		}
	}
	
	@Test
	public void testIsRedirectedSuccessWithStatusCodeMovedTemporarily() {
		RedirectHandler redirectHandler = RedirectHandler.INSTANCE;
		Request httpget = new Request.Builder().url(testmeurl).build();
		Response response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_MOVED_TEMPORARILY, "Moved Temporarily");
		response.setHeader("location", testmeurl);
		HttpClientContext localContext = HttpClientContext.create();
		try {
			boolean isRedirected = redirectHandler.isRedirected(httpget, response, localContext);
			assertTrue(isRedirected);
		} catch (ProtocolException e) {
			e.printStackTrace();
			fail("Redirect handler testIsRedirectedSuccess failure");
		}
	}
	
	@Test
	public void testIsRedirectedSuccessWithStatusCodeMovedPermanently() {
		RedirectHandler redirectHandler = RedirectHandler.INSTANCE;
		Request httpget = new Request.Builder().url(testmeurl).build();
		Response response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_MOVED_PERMANENTLY, "Moved Permanently");
		response.setHeader("location", testmeurl);
		HttpClientContext localContext = HttpClientContext.create();
		try {
			boolean isRedirected = redirectHandler.isRedirected(httpget, response, localContext);
			assertTrue(isRedirected);
		} catch (ProtocolException e) {
			e.printStackTrace();
			fail("Redirect handler testIsRedirectedSuccess1 failure");
		}
	}
	
	@Test
	public void testIsRedirectedSuccessWithStatusCodeTemporaryRedirect() {
		RedirectHandler redirectHandler = RedirectHandler.INSTANCE;
		Request httpget = new HttpGet(testmeurl);
		Response response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_TEMPORARY_REDIRECT, "Temporary Redirect");
		response.setHeader("location", testmeurl);
		HttpClientContext localContext = HttpClientContext.create();
		try {
			boolean isRedirected = redirectHandler.isRedirected(httpget, response, localContext);
			assertTrue(isRedirected);
		} catch (ProtocolException e) {
			e.printStackTrace();
			fail("Redirect handler testIsRedirectedSuccess2 failure");
		}
	}
	
	@Test
	public void testIsRedirectedSuccessWithStatusCodeSeeOther() {
		RedirectHandler redirectHandler = RedirectHandler.INSTANCE;
		Request httpget = new Request.Builder().url(testmeurl).build();
		Response response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_SEE_OTHER, "See Other");
		response.setHeader("location", testmeurl);
		HttpClientContext localContext = HttpClientContext.create();
		try {
			boolean isRedirected = redirectHandler.isRedirected(httpget, response, localContext);
			assertTrue(isRedirected);
		} catch (ProtocolException e) {
			e.printStackTrace();
			fail("Redirect handler testIsRedirectedSuccess3 failure");
		}
	}
	
	@Test
	public void testGetRedirectForGetMethod() {
		RedirectHandler redirectHandler = RedirectHandler.INSTANCE;
		Request httpget = new Request.Builder().url(testurl).build();
		Response response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_MOVED_TEMPORARILY, "Moved Temporarily");
		response.setHeader("location", testmeurl);
		HttpClientContext localContext = HttpClientContext.create();
		try {
			Request request = redirectHandler.getRedirect(httpget, response, localContext);
			assertTrue(request != null);
			final String method = request.getRequestLine().getMethod();
			assertTrue(method.equalsIgnoreCase(HttpGet.METHOD_NAME));
		} catch (ProtocolException e) {
			e.printStackTrace();
			fail("Redirect handler testGetRedirectForGetMethod failure");
		}
	}
		
	@Test
	public void testGetRedirectForGetMethodForAuthHeader() {
		RedirectHandler redirectHandler = RedirectHandler.INSTANCE;
		Request httpget = new Request.Builder().url(testurl).build();
		httpget.addHeader("Authorization", "TOKEN");
		Response response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_MOVED_TEMPORARILY, "Moved Temporarily");
		response.setHeader("location", differenthosturl);
		HttpClientContext localContext = HttpClientContext.create();
		try {
			Request request = redirectHandler.getRedirect(httpget, response, localContext);
			assertTrue(request != null);
			final String method = request.getRequestLine().getMethod();
			assertTrue(method.equalsIgnoreCase(HttpGet.METHOD_NAME));
			Header header = request.getFirstHeader("Authorization");
			assertTrue(header == null);
		} catch (ProtocolException e) {
			e.printStackTrace();
			fail("Redirect handler testGetRedirectForGetMethodForAuthHeader failure");
		}
	}
	
	@Test
	public void testGetRedirectForHeadMethod() {
		RedirectHandler redirectHandler = RedirectHandler.INSTANCE;
		HttpHead httphead = new HttpHead(testurl);
		Response response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_MOVED_TEMPORARILY, "Moved Temporarily");
		response.setHeader("location", testmeurl);
		HttpClientContext localContext = HttpClientContext.create();
		try {
			Request request = redirectHandler.getRedirect(httphead, response, localContext);
			assertTrue(request != null);
			final String method = request.getRequestLine().getMethod();
			assertTrue(method.equalsIgnoreCase(HttpHead.METHOD_NAME));
		} catch (ProtocolException e) {
			e.printStackTrace();
			fail("Redirect handler testGetRedirectForHeadMethod failure");
		}
	}
	
	@Test
	public void testGetRedirectForPostMethod() {
		RedirectHandler redirectHandler = RedirectHandler.INSTANCE;
		Request httppost = new Request.Builder().url(testurl).build();
		Response response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_MOVED_TEMPORARILY, "Moved Temporarily");
		response.setHeader("location", testmeurl);
		HttpClientContext localContext = HttpClientContext.create();
		try {
			Request request = redirectHandler.getRedirect(httppost, response, localContext);
			assertTrue(request != null);
			final String method = request.getRequestLine().getMethod();
			assertTrue(method.equalsIgnoreCase(HttpPost.METHOD_NAME));
		} catch (ProtocolException e) {
			e.printStackTrace();
			fail("Redirect handler testGetRedirectForPostMethod failure");
		}
	}
	
	@Test
	public void testGetRedirectForPostMethodWithStatusCodeSeeOther() {
		RedirectHandler redirectHandler = RedirectHandler.INSTANCE;
		Request httppost = new Request.Builder().url(testurl).build();
		Response response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_SEE_OTHER, "See Other");
		response.setHeader("location", testmeurl);
		HttpClientContext localContext = HttpClientContext.create();
		try {
			Request request = redirectHandler.getRedirect(httppost, response, localContext);
			assertTrue(request != null);
			final String method = request.getRequestLine().getMethod();
			assertTrue(method.equalsIgnoreCase(HttpGet.METHOD_NAME));
		} catch (ProtocolException e) {
			e.printStackTrace();
			fail("Redirect handler testGetRedirectForPostMethod1 failure");
		}
	}

}
