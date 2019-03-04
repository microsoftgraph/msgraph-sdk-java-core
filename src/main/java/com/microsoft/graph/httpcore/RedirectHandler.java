package com.microsoft.graph.httpcore;

import static java.net.HttpURLConnection.HTTP_MOVED_PERM;
import static java.net.HttpURLConnection.HTTP_MOVED_TEMP;
import static java.net.HttpURLConnection.HTTP_SEE_OTHER;
import static okhttp3.internal.http.StatusLine.HTTP_PERM_REDIRECT;
import static okhttp3.internal.http.StatusLine.HTTP_TEMP_REDIRECT;

import java.io.IOException;
import java.net.ProtocolException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.internal.http.HttpMethod;

public class RedirectHandler implements Interceptor{
	
	public static final RedirectHandler INSTANCE = new RedirectHandler();
	final int maxRedirect = 5;
	
    public boolean isRedirected(Request request, Response response, int redirectCount) throws IOException {
        
    	if(redirectCount > maxRedirect) return false;
    	
        final String locationHeader = response.header("location");
        if(locationHeader == null)
        	return false;
        
        final int statusCode = response.code();
        if(statusCode == HTTP_PERM_REDIRECT || //308
        		statusCode == HTTP_MOVED_PERM || //301
        		statusCode == HTTP_TEMP_REDIRECT || //307
        		statusCode == HTTP_SEE_OTHER || //303
        		statusCode == HTTP_MOVED_TEMP) //302
        	return true;
        
        return false;
    }
    
    public Request getRedirect(
    		final Request request,
    		final Response userResponse) throws ProtocolException {    	
        String location = userResponse.header("Location");
        if (location == null) return null;
        
        HttpUrl requestUrl = userResponse.request().url();
        
        HttpUrl locationUrl = userResponse.request().url().resolve(location);
        // Don't follow redirects to unsupported protocols.
        if (locationUrl == null) return null;

        // Most redirects don't include a request body.
        Request.Builder requestBuilder = userResponse.request().newBuilder();

        // When redirecting across hosts, drop all authentication headers. This
        // is potentially annoying to the application layer since they have no
        // way to retain them.
        boolean sameScheme = locationUrl.scheme().equalsIgnoreCase(requestUrl.scheme());
        boolean sameHost = locationUrl.host().toString().equalsIgnoreCase(requestUrl.host().toString());
        if (!sameScheme || !sameHost) {
          requestBuilder.removeHeader("Authorization");
        }

        return requestBuilder.url(locationUrl).build();
    }

	@Override
	public Response intercept(Chain chain) throws IOException {
		Request request = chain.request();
		Response response = null;
		int redirectCount = 1;
		while(true) {
			response = chain.proceed(request);
			boolean shouldRedirect = isRedirected(request, response, redirectCount);
			if(!shouldRedirect) break;
			
			Request followup = getRedirect(request, response);
			if(followup == null) break;
			request = followup;
		
			redirectCount++;
		}
		return response;
	}
}
