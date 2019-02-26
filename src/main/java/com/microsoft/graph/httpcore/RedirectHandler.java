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
        if(statusCode == HTTP_PERM_REDIRECT ||
        		statusCode == HTTP_MOVED_PERM ||
        		statusCode == HTTP_TEMP_REDIRECT ||
        		statusCode == HTTP_SEE_OTHER ||
        		statusCode == HTTP_MOVED_TEMP)
        	return true;
        
        return false;
    }
    
    public Request getRedirect(
    		final Request request,
    		final Response userResponse) throws ProtocolException {    	
        String location = userResponse.header("Location");
        if (location == null) return null;
        HttpUrl url = userResponse.request().url().resolve(location);
        // Don't follow redirects to unsupported protocols.
        if (url == null) return null;

        // Most redirects don't include a request body.
        Request.Builder requestBuilder = userResponse.request().newBuilder();
        final String method = userResponse.request().method();
        
        if (HttpMethod.permitsRequestBody(method)) {
          final boolean maintainBody = HttpMethod.redirectsWithBody(method);
          if (HttpMethod.redirectsToGet(method)) {
            requestBuilder.method("GET", null);
          } else {
            RequestBody requestBody = maintainBody ? userResponse.request().body() : null;
            requestBuilder.method(method, requestBody);
          }
          if (!maintainBody) {
            requestBuilder.removeHeader("Transfer-Encoding");
            requestBuilder.removeHeader("Content-Length");
            requestBuilder.removeHeader("Content-Type");
          }
        }

        // When redirecting across hosts, drop all authentication headers. This
        // is potentially annoying to the application layer since they have no
        // way to retain them.
        boolean sameScheme = url.scheme().equals(userResponse.request().url().scheme());
        boolean sameHost = url.host().equals(userResponse.request().url().host());
        if (!sameScheme || !sameHost) {
          requestBuilder.removeHeader("Authorization");
        }

        return requestBuilder.url(url).build();
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
