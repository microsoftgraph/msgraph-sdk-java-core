package com.microsoft.graph.httpcore;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;

public class RedirectHandler extends DefaultRedirectStrategy{
	
	public static final RedirectHandler INSTANCE = new RedirectHandler();
	
    @Override
    public boolean isRedirected(
            final HttpRequest request,
            final HttpResponse response,
            final HttpContext context) throws ProtocolException {
        Args.notNull(request, "HTTP request");
        Args.notNull(response, "HTTP response");

        final int statusCode = response.getStatusLine().getStatusCode();
        final Header locationHeader = response.getFirstHeader("location");
        if(locationHeader == null)
        	return false;
        
        if(statusCode == HttpStatus.SC_MOVED_TEMPORARILY ||
        		statusCode == HttpStatus.SC_MOVED_PERMANENTLY ||
        		statusCode == HttpStatus.SC_TEMPORARY_REDIRECT ||
        		statusCode == HttpStatus.SC_SEE_OTHER ||
        		statusCode == 308)
        	return true;
        
        return false;
    }
    
    @Override
    public HttpUriRequest getRedirect(
    		final HttpRequest request,
    		final HttpResponse response,
    		final HttpContext context) throws ProtocolException {
    	final URI uri = getLocationURI(request, response, context);
    	try {
    		final URI requestURI = new URI(request.getRequestLine().getUri());
    		if(!uri.getHost().equalsIgnoreCase(requestURI.getHost()) || 
    				!uri.getScheme().equalsIgnoreCase(requestURI.getScheme()))
    			request.removeHeaders("Authorization");	
    	}
    	catch (final URISyntaxException ex) {
    		throw new ProtocolException(ex.getMessage(), ex);
    	}
    	
    	final int status = response.getStatusLine().getStatusCode();
    	if(status == HttpStatus.SC_SEE_OTHER)
    		return new HttpGet(uri);
    	return RequestBuilder.copy(request).setUri(uri).build();
    }
}
