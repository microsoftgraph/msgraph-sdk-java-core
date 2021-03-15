package com.microsoft.graph.http;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import com.microsoft.graph.core.GraphErrorCodes;
import com.microsoft.graph.logger.DefaultLogger;
import com.microsoft.graph.serializer.ISerializer;

public class GraphServiceExceptionTests {

	@Test
	public void testError() {
        GraphErrorResponse errorResponse = new GraphErrorResponse();
        GraphError error = new GraphError();
        error.code = GraphErrorCodes.UNAUTHENTICATED.toString();
        errorResponse.error = error;
        GraphServiceException exception = new GraphServiceException("GET","https://graph.microsoft.com/v1.0/me",new ArrayList<String>(),null,401,"Unauthorized",new ArrayList<String>(),errorResponse, false);
        String message = exception.getMessage();
        assertTrue(message.indexOf("Error code: UNAUTHENTICATED") == 0);
        assertTrue(message.indexOf("401 : Unauthorized") > 0);
        assertTrue(message.indexOf("truncated") > 0);
        assertEquals(error,exception.getServiceError());
    }

	@Test
	public void testVerboseError() {
		GraphErrorResponse errorResponse = new GraphErrorResponse();
        GraphError error = new GraphError();
        error.code = GraphErrorCodes.UNAUTHENTICATED.toString();
        errorResponse.error = error;
        GraphServiceException exception = new GraphServiceException("GET","https://graph.microsoft.com/v1.0/me",new ArrayList<String>(),null,401,"Unauthorized",new ArrayList<String>(),errorResponse, true);
        String message = exception.getMessage();
        assertTrue(message.indexOf("Error code: UNAUTHENTICATED") == 0);
        assertTrue(message.indexOf("401 : Unauthorized") > 0);
        assertFalse(message.indexOf("truncated") > 0);
        assertEquals(error,exception.getServiceError());
	}

	@Test
    public void testcreateFromResponse() throws IOException {
		DefaultLogger logger = new DefaultLogger();
        GraphServiceException exception = null;
        final Response response = new Response.Builder()
                .request(new Request.Builder().url("https://a.b.c").build())
                .protocol(Protocol.HTTP_1_1)
                .code(401).message("Unauthorized").body(
                   ResponseBody.create(
                        "{}",
                        MediaType.parse("application/json")
                ))
                .build();

        IHttpRequest mRequest = mock(IHttpRequest.class);
        when(mRequest.getRequestUrl()).thenReturn(new URL("http://localhost"));
        exception = GraphServiceException.createFromResponse(mRequest ,null, mock(ISerializer.class),response,logger);

        String message = exception.getMessage();
        assertTrue(message.indexOf("http://localhost") > 0);
    }
    @Test
    public void requestPayloadShouldNotBePartOfMessageWhenNotVerbose(){
        final GraphErrorResponse errorResponse = new GraphErrorResponse();
        final GraphError error = new GraphError();
        error.code = GraphErrorCodes.UNAUTHENTICATED.toString();
        errorResponse.error = error;
        final GraphServiceException exception = new GraphServiceException("GET","https://graph.microsoft.com/v1.0/me",new ArrayList<String>(),"requestPayload",401,"Unauthorized",new ArrayList<String>(),errorResponse, false);
        final String message = exception.getMessage();
        assertFalse(message.indexOf("requestPayload") > 0);
    }
    @Test
    public void requestPayloadShouldBePartOfMessageWhenVerbose(){
        final GraphErrorResponse errorResponse = new GraphErrorResponse();
        final GraphError error = new GraphError();
        error.code = GraphErrorCodes.UNAUTHENTICATED.toString();
        errorResponse.error = error;
        final GraphServiceException exception = new GraphServiceException("GET","https://graph.microsoft.com/v1.0/me",new ArrayList<String>(),"requestPayload",401,"Unauthorized",new ArrayList<String>(),errorResponse, true);
        final String message = exception.getMessage();
        assertTrue(message.indexOf("requestPayload") > 0);
    }
}
