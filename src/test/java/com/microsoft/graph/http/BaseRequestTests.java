package com.microsoft.graph.http;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import static org.mockito.Mockito.mock;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.microsoft.graph.core.IBaseClient;
import com.microsoft.graph.options.FunctionOption;
import com.microsoft.graph.options.Option;
import com.microsoft.graph.options.QueryOption;
import com.microsoft.graph.core.BaseClient;

/**
 * Test cases for {@see BaseRequest}
 */
public class BaseRequestTests {
    private IBaseClient<Request> mBaseClient;
    private BaseRequest<JsonObject> mRequest;

    @BeforeEach
    @SuppressFBWarnings
    public void setUp() throws Exception {
        final Response response = new Response.Builder()
                .request(new Request.Builder().url("https://a.b.c").build())
                .protocol(Protocol.HTTP_1_1)
                .code(200).message("OK").body(
                   ResponseBody.create("{ \"id\": \"zzz\" }", MediaType.parse("application/json")
                ))
                .addHeader("Content-Type", "application/json")
                .build();
        final OkHttpClient mockClient = BaseStreamRequestTests.getMockClient(response);
        final JsonObject result = new JsonObject();
        result.add("id", new JsonPrimitive("zzz"));
        mBaseClient = BaseClient.builder()
                .httpClient(mockClient)
                .buildClient();
        mRequest = new BaseRequest<JsonObject>("https://a.b.c/", mBaseClient, null,JsonObject.class){};
    }

    @Test
    public void testSend() {
        final JsonObject result = mRequest.send(HttpMethod.GET, null);
        assertNotNull(result);
        assertEquals("zzz", result.get("id").getAsString());
    }

    @Test
    public void testSendWithCallback() throws InterruptedException, ExecutionException {
        final java.util.concurrent.CompletableFuture<JsonObject> result = mRequest.sendAsync(HttpMethod.GET, null);
        assertNotNull(result.get());
        assertTrue(result.isDone());
        assertFalse(result.isCancelled());
        assertEquals("zzz", result.get().get("id").getAsString());
    }

    @Test
    public void testFunctionParameters() {
        final Option fo1 = new FunctionOption("1", "one");
        final Option fo2 = new FunctionOption("2", null);
        final BaseRequest<Void> request = new BaseRequest<Void>("https://a.b.c/", mock(IBaseClient.class), Arrays.asList(fo1, fo2), Void.class){};
        assertEquals("https://a.b.c/(1='one',2=null)", request.getRequestUrl().toString());
        request.addFunctionOption(new FunctionOption("3","two"));;
        assertEquals("https://a.b.c/(1='one',2=null,3='two')", request.getRequestUrl().toString());
        assertEquals(3, request.getOptions().size());
    }

    @Test
    public void testQueryParameters() {
        final Option q1 = new QueryOption("q1","option1 ");
        final Option q2 = new QueryOption("q2","option2");
        final BaseRequest<Void> request = new BaseRequest<Void>("https://a.b.c/", mock(IBaseClient.class), Arrays.asList(q1, q2), Void.class){};
        assertEquals("https://a.b.c/?q1=option1%20&q2=option2", request.getRequestUrl().toString());
        request.addQueryOption(new QueryOption("q3","option3"));
        assertEquals("https://a.b.c/?q1=option1%20&q2=option2&q3=option3", request.getRequestUrl().toString());
        assertEquals(3,request.getOptions().size());
    }

    @Test
    public void testFunctionAndQueryParameters() {
        final Option f1 = new FunctionOption("f1", "fun1");
        final Option f2 = new FunctionOption("f2", null);
        final Option q1 = new QueryOption("q1","option1 ");
        final Option q2 = new QueryOption("q2","option2");
        final BaseRequest<Void> request = new BaseRequest<Void>("https://a.b.c/", mock(IBaseClient.class), Arrays.asList(f1, f2, q1, q2), Void.class){};
        assertEquals("https://a.b.c/(f1='fun1',f2=null)?q1=option1%20&q2=option2", request.getRequestUrl().toString());
        assertEquals(4, request.getOptions().size());
    }

    @Test
    public void testHttpMethod() {
        final BaseRequest<Void> request = new BaseRequest<Void>("https://a.b.c/", mock(IBaseClient.class), null, Void.class){};
        assertNull(request.getHttpMethod());
        request.setHttpMethod(HttpMethod.GET);
        assertEquals(HttpMethod.GET, request.getHttpMethod());
    }

    @Test
    public void testHeader() {
        final String expectedHeader = "header key";
        final String expectedValue = "header value";
        final BaseRequest<Void> request = new BaseRequest<Void>("https://a.b.c/", mock(IBaseClient.class), null, Void.class){};
        assertEquals(0, request.getHeaders().size());
        request.addHeader(expectedHeader,expectedValue);
        assertEquals(1,request.getHeaders().size());
    }

    @Test
    public void testProtectedProperties() {
        assertEquals(0, mRequest.functionOptions.size());
        assertEquals(0, mRequest.queryOptions.size());
        final Option q1 = new QueryOption("q1","option1 ");
        final Option f1 = new FunctionOption("f1","option2");
        final BaseRequest<Void> request = new BaseRequest<Void>("https://a.b.c/", mock(IBaseClient.class), Arrays.asList(q1,f1), Void.class){};
        assertEquals(1, request.functionOptions.size());
        assertEquals(1, request.queryOptions.size());
        assertEquals("q1", request.queryOptions.get(0).getName());
        assertEquals("option1 ", request.queryOptions.get(0).getValue());
        assertEquals("f1", request.functionOptions.get(0).getName());
        assertEquals("option2", request.functionOptions.get(0).getValue());
    }
}
