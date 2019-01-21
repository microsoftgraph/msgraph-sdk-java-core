package com.microsoft.graph.httpcore;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.ProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.message.BasicHttpResponse;
import org.junit.Test;

public class RedirectHandlerTest {
	
	String testmeurl = "https://graph.microsoft.com/v1.0/me/";
	String testurl = "https://graph.microsoft.com/v1.0/";
	String differenthosturl = "https://graph.abc.com/v1.0/";

	@Test
	public void testIsRedirectedFailure() {
		RedirectHandler redirectHandler = RedirectHandler.INSTANCE;
		HttpGet httpget = new HttpGet(testmeurl);
		HttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_MOVED_TEMPORARILY, "Moved Temporarily");
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
	public void testIsRedirectedFailure1() {
		RedirectHandler redirectHandler = RedirectHandler.INSTANCE;
		HttpGet httpget = new HttpGet(testmeurl);
		HttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_BAD_REQUEST, "Bad Request");
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
	public void testIsRedirectedSuccess() {
		RedirectHandler redirectHandler = RedirectHandler.INSTANCE;
		HttpGet httpget = new HttpGet(testmeurl);
		HttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_MOVED_TEMPORARILY, "Moved Temporarily");
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
	public void testIsRedirectedSuccess1() {
		RedirectHandler redirectHandler = RedirectHandler.INSTANCE;
		HttpGet httpget = new HttpGet(testmeurl);
		HttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_MOVED_PERMANENTLY, "Moved Permanently");
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
	public void testIsRedirectedSuccess2() {
		RedirectHandler redirectHandler = RedirectHandler.INSTANCE;
		HttpGet httpget = new HttpGet(testmeurl);
		HttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_TEMPORARY_REDIRECT, "Temporary Redirect");
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
	public void testIsRedirectedSuccess3() {
		RedirectHandler redirectHandler = RedirectHandler.INSTANCE;
		HttpGet httpget = new HttpGet(testmeurl);
		HttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_SEE_OTHER, "See Other");
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
		HttpGet httpget = new HttpGet(testurl);
		HttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_MOVED_TEMPORARILY, "Moved Temporarily");
		response.setHeader("location", testmeurl);
		HttpClientContext localContext = HttpClientContext.create();
		try {
			HttpRequest request = redirectHandler.getRedirect(httpget, response, localContext);
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
		HttpGet httpget = new HttpGet(testurl);
		httpget.addHeader("Authorization", "TOKEN");
		HttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_MOVED_TEMPORARILY, "Moved Temporarily");
		response.setHeader("location", differenthosturl);
		HttpClientContext localContext = HttpClientContext.create();
		try {
			HttpRequest request = redirectHandler.getRedirect(httpget, response, localContext);
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
		HttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_MOVED_TEMPORARILY, "Moved Temporarily");
		response.setHeader("location", testmeurl);
		HttpClientContext localContext = HttpClientContext.create();
		try {
			HttpRequest request = redirectHandler.getRedirect(httphead, response, localContext);
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
		HttpPost httppost = new HttpPost(testurl);
		HttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_MOVED_TEMPORARILY, "Moved Temporarily");
		response.setHeader("location", testmeurl);
		HttpClientContext localContext = HttpClientContext.create();
		try {
			HttpRequest request = redirectHandler.getRedirect(httppost, response, localContext);
			assertTrue(request != null);
			final String method = request.getRequestLine().getMethod();
			assertTrue(method.equalsIgnoreCase(HttpPost.METHOD_NAME));
		} catch (ProtocolException e) {
			e.printStackTrace();
			fail("Redirect handler testGetRedirectForPostMethod failure");
		}
	}
	
	@Test
	public void testGetRedirectForPostMethod1() {
		RedirectHandler redirectHandler = RedirectHandler.INSTANCE;
		HttpPost httppost = new HttpPost(testurl);
		HttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_SEE_OTHER, "See Other");
		response.setHeader("location", testmeurl);
		HttpClientContext localContext = HttpClientContext.create();
		try {
			HttpRequest request = redirectHandler.getRedirect(httppost, response, localContext);
			assertTrue(request != null);
			final String method = request.getRequestLine().getMethod();
			assertTrue(method.equalsIgnoreCase(HttpGet.METHOD_NAME));
		} catch (ProtocolException e) {
			e.printStackTrace();
			fail("Redirect handler testGetRedirectForPostMethod1 failure");
		}
	}

}
