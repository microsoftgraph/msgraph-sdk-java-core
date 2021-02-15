package com.microsoft.graph.http;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Dispatcher;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static org.mockito.Mockito.*;

import com.microsoft.graph.core.BaseClient;
import com.microsoft.graph.logger.ILogger;
import com.microsoft.graph.serializer.ISerializer;

/**
 * Test cases for {@see BaseStreamRequest}
 */
public class BaseStreamRequestTests {

    private BaseClient<Request> mBaseClient;

    @BeforeEach
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        mBaseClient = mock(BaseClient.class);
    }

    @Test
    public void testSend() throws IOException {
        final Response response = new Response.Builder()
                .request(new Request.Builder().url("https://a.b.c").build())
                .protocol(Protocol.HTTP_1_1)
                .code(200).message("OK").body(
                   ResponseBody.create(
                        "{ \"id\": \"zzz\" }",
                        MediaType.parse("application/octet-stream")
                ))
                .build();

        final OkHttpClient mockClient = getMockClient(response);
        CoreHttpProvider mProvider = new CoreHttpProvider(mock(ISerializer.class),
                mock(ILogger.class),
                mockClient);
        when(mBaseClient.getHttpProvider()).thenReturn(mProvider);
        final BaseStreamRequest<String> request = new BaseStreamRequest<>("https://a.b.c/", mBaseClient,null, String.class){};
        request.send();
    }

    @Test
    public void testSendWithCallback() throws IOException, InterruptedException, ExecutionException {
        final Response response = new Response.Builder()
                .request(new Request.Builder().url("https://a.b.c").build())
                .protocol(Protocol.HTTP_1_1)
                .code(200).message("OK").body(
                   ResponseBody.create(
                        "{ \"id\": \"zzz\" }",
                        MediaType.parse("application/json")
                ))
                .build();

        final OkHttpClient mockClient = getMockClient(response);
        CoreHttpProvider mProvider = new CoreHttpProvider(mock(ISerializer.class),
                mock(ILogger.class),
                mockClient);
        when(mBaseClient.getHttpProvider()).thenReturn(mProvider);
        final BaseStreamRequest<InputStream> request = new BaseStreamRequest<>("https://a.b.c/", mBaseClient,null, InputStream.class){};
        final java.util.concurrent.CompletableFuture<InputStream> result = request.sendAsync();
        assertNotNull(result.get());
        assertTrue(result.isDone());
        assertFalse(result.isCancelled());
    }

    @Test
    public void testSendWithContentAndCallback() throws IOException, InterruptedException, ExecutionException {
        final Response response = new Response.Builder()
                .request(new Request.Builder().url("https://a.b.c").build())
                .protocol(Protocol.HTTP_1_1)
                .code(200).message("OK").body(
                   ResponseBody.create(
                        "{ \"id\": \"zzz\" }",
                        MediaType.parse("application/json")
                ))
                .build();

        final OkHttpClient mockClient = getMockClient(response);
        CoreHttpProvider mProvider = new CoreHttpProvider(mock(ISerializer.class),
                mock(ILogger.class),
                mockClient);
        when(mBaseClient.getHttpProvider()).thenReturn(mProvider);
        final BaseStreamRequest<InputStream> request = new BaseStreamRequest<>("https://a.b.c/", mBaseClient,null, InputStream.class){};
        final java.util.concurrent.CompletableFuture<InputStream> result = request.sendAsync(new byte[]{1, 2, 3, 4});
        assertNotNull(result.get());
        assertTrue(result.isDone());
        assertFalse(result.isCancelled());
    }

    @Test
    public void testSendWithContent() throws IOException {
        final Response response = new Response.Builder()
                .request(new Request.Builder().url("https://a.b.c").build())
                .protocol(Protocol.HTTP_1_1)
                .code(200).message("OK").body(
                   ResponseBody.create(
                        "{ \"id\": \"zzz\" }",
                        MediaType.parse("application/json")
                ))
                .build();

        final OkHttpClient mockClient = getMockClient(response);

        CoreHttpProvider mProvider = new CoreHttpProvider(mock(ISerializer.class),
                mock(ILogger.class),
                mockClient);
        when(mBaseClient.getHttpProvider()).thenReturn(mProvider);
        final BaseStreamRequest<InputStream> request = new BaseStreamRequest<>("https://a.b.c/", mBaseClient,null, InputStream.class){};
        request.send(new byte[]{1, 2, 3, 4});
    }

    @Test
    public void testBaseMethod() {
        final BaseStreamRequest<InputStream> request = new BaseStreamRequest<>("https://a.b.c/", mBaseClient,null, InputStream.class){};
        assertEquals("https://a.b.c/", request.getRequestUrl().toString());
        request.addHeader("header key", "header value");
        assertEquals(1,request.getHeaders().size());
        assertNull(request.getHttpMethod());
        assertEquals(1, request.getOptions().size());
    }
    public static OkHttpClient getMockClient(final Response response) throws IOException {
        final OkHttpClient mockClient = mock(OkHttpClient.class);
        final Call remoteCall = mock(Call.class);
        final Dispatcher dispatcher = new Dispatcher();
        when(remoteCall.execute()).thenReturn(response);
        doAnswer((Answer<Void>) invocation -> {
            Callback callback = invocation.getArgument(0);
            callback.onResponse(null, response);
            return null;
        }).when(remoteCall)
            .enqueue(any(Callback.class));
        when(mockClient.dispatcher()).thenReturn(dispatcher);
        when(mockClient.newCall(any())).thenReturn(remoteCall);
        return mockClient;
    }
}
