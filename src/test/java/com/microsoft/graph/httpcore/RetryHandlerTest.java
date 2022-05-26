package com.microsoft.graph.httpcore;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.net.HttpURLConnection;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.jupiter.api.Test;

import com.microsoft.graph.httpcore.middlewareoption.IShouldRetry;
import com.microsoft.graph.httpcore.middlewareoption.RetryOptions;

import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

@SuppressFBWarnings
public class RetryHandlerTest {

    private static final String testmeurl = "https://graph.microsoft.com/v1.0/me";
    private static final int HTTP_SERVER_ERROR = 500;

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
            public boolean shouldRetry(long delay, int executionCount, Request request,Response response){
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
        Request httppost = new Request.Builder().url(testmeurl).post(RequestBody.create("TEST", MediaType.parse("application/json"))).build();
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
        Request httppost = new Request.Builder().url(testmeurl).post(RequestBody.create("TEST", MediaType.parse("application/json"))).build();
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
    public void testGetRetryAfterWithHeader() {
        RetryHandler retryHandler = new RetryHandler();
        long delay = retryHandler.getRetryAfter(TestResponse().newBuilder().addHeader("Retry-After", "60").build(), 1, 1);
        assertTrue(delay == 60000);
        delay = retryHandler.getRetryAfter(TestResponse().newBuilder().addHeader("Retry-After", "1").build(), 2, 3);
        assertTrue(delay == 1000);
    }

    @Test
    public void testGetRetryAfterOnFirstExecution() {
        RetryHandler retryHandler = new RetryHandler();
        long delay = retryHandler.getRetryAfter(TestResponse(), 3, 1);
        assertTrue(delay > 3000);
        delay = retryHandler.getRetryAfter(TestResponse(), 3, 2);
        assertTrue(delay > 3100);
    }

    @Test
    public void testGetRetryAfterMaxExceed() {
        RetryHandler retryHandler = new RetryHandler();
        long delay = retryHandler.getRetryAfter(TestResponse(), 190, 1);
        assertTrue(delay == 180000);
    }
    @Test
    public void defensiveProgramming() {
        assertThrows(NullPointerException.class, () -> {
            new RetryHandler(null, mock(RetryOptions.class));
        }, "logger cannot be null");
    }

    @Test
    public void testIsBuffered() {
        final RetryHandler retryHandler = new RetryHandler();
        Request request = new Request.Builder().url("https://localhost").method("GET", null).build();
        assertTrue(retryHandler.isBuffered(request), "Get Request is buffered");

        request = new Request.Builder().url("https://localhost").method("DELETE", null).build();
        assertTrue(retryHandler.isBuffered(request), "Delete Request is buffered");

        request = new Request.Builder().url("https://localhost")
                                        .method("POST",
                                            RequestBody.create("{\"key\": 42 }", MediaType.parse("application/json")))
                                        .build();
        assertTrue(retryHandler.isBuffered(request), "Post Request is buffered");

        request = new Request.Builder().url("https://localhost")
                                        .method("POST",
                                            new RequestBody() {

                                                @Override
                                                public MediaType contentType() {
                                                    return MediaType.parse("application/octet-stream");
                                                }

                                                @Override
                                                public void writeTo(BufferedSink sink) throws IOException {
                                                    // TODO Auto-generated method stub

                                                }
                                            })
                                        .build();
        assertFalse(retryHandler.isBuffered(request), "Post Stream Request is not buffered");
    }

    Response TestResponse() {
        return new Response.Builder()
                .code(429)
                .message("message")
                .request(new Request.Builder().url("https://localhost").build())
                .protocol(Protocol.HTTP_1_0).build();
    }
}
