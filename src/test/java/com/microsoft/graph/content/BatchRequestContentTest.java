package com.microsoft.graph.content;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.ArrayList;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.microsoft.graph.authentication.IAuthenticationProvider;
import com.microsoft.graph.core.BaseClient;
import com.microsoft.graph.core.IBaseClient;
import com.microsoft.graph.http.CoreHttpProvider;
import com.microsoft.graph.http.HttpMethod;
import com.microsoft.graph.http.IHttpRequest;
import com.microsoft.graph.logger.ILogger;
import com.microsoft.graph.options.HeaderOption;
import com.microsoft.graph.serializer.AdditionalDataManager;
import com.microsoft.graph.serializer.DefaultSerializer;
import com.microsoft.graph.serializer.IJsonBackedObject;
import com.microsoft.graph.serializer.ISerializer;

import org.junit.jupiter.api.Test;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class BatchRequestContentTest {

    String testurl = "http://graph.microsoft.com/me";

    @Test
    public void testBatchRequestContentCreation() throws MalformedURLException {
        BatchRequestContent requestContent = new BatchRequestContent();
        for (int i = 0; i < 5; i++) {
            IHttpRequest requestStep = mock(IHttpRequest.class);
            when(requestStep.getRequestUrl()).thenReturn(new URL(testurl));
            requestContent.addBatchRequestStep(requestStep);
        }
        assertNotNull(requestContent.requests);
    }

    @Test
    public void testGetBatchRequestContent() throws MalformedURLException {
        IHttpRequest requestStep = mock(IHttpRequest.class);
        when(requestStep.getRequestUrl()).thenReturn(new URL(testurl));
        BatchRequestContent requestContent = new BatchRequestContent();
        String stepId = requestContent.addBatchRequestStep(requestStep);
        String content = new DefaultSerializer(mock(ILogger.class)).serializeObject(requestContent);
        String expectedContent = "{\"requests\":[{\"url\":\"http://graph.microsoft.com/me\",\"method\":\"GET\",\"id\":\""
                + stepId + "\"}]}";
        assertEquals(expectedContent, content);
    }

    @Test
    public void testGetBatchRequestContentWithHeader() throws MalformedURLException {
        IHttpRequest requestStep = mock(IHttpRequest.class);
        when(requestStep.getRequestUrl()).thenReturn(new URL(testurl));
        when(requestStep.getHeaders()).thenReturn(Arrays.asList(new HeaderOption("testkey", "testvalue")));
        BatchRequestContent requestContent = new BatchRequestContent();
        String stepId = requestContent.addBatchRequestStep(requestStep);
        String content = new DefaultSerializer(mock(ILogger.class)).serializeObject(requestContent);
        String expectedContent = "{\"requests\":[{\"url\":\"http://graph.microsoft.com/me\",\"method\":\"GET\",\"id\":\""
                + stepId + "\",\"headers\":{\"testkey\":\"testvalue\"}}]}";
        assertEquals(expectedContent, content);
    }

    @Test
    public void testRemoveBatchRequesStepWithId() throws MalformedURLException {
        IHttpRequest requestStep = mock(IHttpRequest.class);
        when(requestStep.getRequestUrl()).thenReturn(new URL(testurl));
        BatchRequestContent requestContent = new BatchRequestContent();
        String stepId = requestContent.addBatchRequestStep(requestStep);
        requestContent.removeBatchRequestStepWithId(stepId);
        String content = new DefaultSerializer(mock(ILogger.class)).serializeObject(requestContent);
        String expectedContent = "{\"requests\":[]}";
        assertEquals(expectedContent, content);
    }

    @Test
    public void testRemoveBatchRequesStepWithIdByAddingMultipleBatchSteps() throws MalformedURLException {
        IHttpRequest requestStep = mock(IHttpRequest.class);
        when(requestStep.getRequestUrl()).thenReturn(new URL(testurl));
        BatchRequestContent requestContent = new BatchRequestContent();
        String stepId = requestContent.addBatchRequestStep(requestStep);

        IHttpRequest requestStep1 = mock(IHttpRequest.class);
        when(requestStep1.getRequestUrl()).thenReturn(new URL(testurl));

        String step1Id = requestContent.addBatchRequestStep(requestStep1, HttpMethod.GET, null, stepId);

        requestContent.removeBatchRequestStepWithId(stepId);
        String content = new DefaultSerializer(mock(ILogger.class)).serializeObject(requestContent);
        String expectedContent = "{\"requests\":[{\"url\":\"http://graph.microsoft.com/me\",\"method\":\"GET\",\"id\":\""
                + step1Id + "\"}]}";
        assertEquals(expectedContent, content);
    }

    @Test
    public void defensiveProgrammingTests() {
        assertThrows(NullPointerException.class, () -> {
            new BatchRequestContent().addBatchRequestStep(null);
        }, "should throw argument exception");
        assertThrows(NullPointerException.class, () -> {
            new BatchRequestContent().addBatchRequestStep(mock(IHttpRequest.class), null);
        }, "null http method throws");

        assertThrows(NullPointerException.class, () -> {
            new BatchRequestContent().getStepById(null);
        }, "get step by id with null id throws");

        assertThrows(NullPointerException.class, () -> {
            new BatchRequestContent() {
                {
                    requests = new ArrayList<>();
                }
            }.removeBatchRequestStepWithId((String) null);
        }, "remove step by id with null id throws");

        assertThrows(IllegalArgumentException.class, () -> {
            new BatchRequestContent().addBatchRequestStep(mock(IHttpRequest.class), HttpMethod.GET, null, "1");
        }, "dependency on inexisting step throws");
    }

    @Test
    public void executeBatchTest() throws Throwable {
        final BatchRequestContent content = new BatchRequestContent();
        IHttpRequest requestStep = mock(IHttpRequest.class);
        when(requestStep.getRequestUrl()).thenReturn(new URL(testurl));
        final String stepId = content.addBatchRequestStep(requestStep);

        final OkHttpClient mHttpClient = mock(OkHttpClient.class);
        final Call mCall = mock(Call.class);
        when(mHttpClient.newCall(any(Request.class))).thenReturn(mCall);

        final CoreHttpProvider mHttpProvider = new CoreHttpProvider(new DefaultSerializer(mock(ILogger.class)),
                mock(ILogger.class), mHttpClient);
        final IBaseClient<?> mClient = BaseClient.builder().authenticationProvider(mock(IAuthenticationProvider.class))
                .httpProvider(mHttpProvider).buildClient();
        final Response mResponse = new Response.Builder()
                .request(new Request.Builder().url("https://graph.microsoft.com/v1.0/$batch").build()).code(200)
                .protocol(Protocol.HTTP_1_1).message("OK").addHeader("Content-type", "application/json")
                .body(ResponseBody.create(
                        "{\"responses\": [{\"id\": \"" + stepId + "\",\"status\": 200,\"body\": null}]}",
                        MediaType.parse("application/json")))
                .build();
        when(mCall.execute()).thenReturn(mResponse);
        final BatchResponseContent batchResponse = mClient.batch().buildRequest().post(content);
        assertNotNull(mClient.batch().buildRequest().postAsync(content));
        final BatchResponseStep<?> response = batchResponse.getResponseById(stepId);
        assertNotNull(response);
    }

    @Test
    public void usesHttpMethodFromRequestIfAlreadySet() throws MalformedURLException {
        IHttpRequest requestStep = mock(IHttpRequest.class);
        when(requestStep.getRequestUrl()).thenReturn(new URL(testurl));
        when(requestStep.getHttpMethod()).thenReturn(HttpMethod.DELETE);
        final BatchRequestContent batchRequest = new BatchRequestContent();
        final String stepId = batchRequest.addBatchRequestStep(requestStep);
        assertEquals(HttpMethod.DELETE.toString(), batchRequest.getStepById(stepId).method);
    }

    @Test
    public void doesNotThrowWhenTryingToRemoveRequestFromNull() {
        final BatchRequestContent batchRequest = new BatchRequestContent();
        batchRequest.removeBatchRequestStepWithId("id");
    }

    @Test
    public void doesNotRemoveDependsOnWhenNotEmpty() throws MalformedURLException {
        IHttpRequest requestStep = mock(IHttpRequest.class);
        when(requestStep.getRequestUrl()).thenReturn(new URL(testurl));
        final BatchRequestContent batchRequest = new BatchRequestContent();
        final String stepId = batchRequest.addBatchRequestStep(requestStep);
        final String stepId2 = batchRequest.addBatchRequestStep(requestStep);
        final String stepId3 = batchRequest.addBatchRequestStep(requestStep, HttpMethod.GET, null, stepId, stepId2);

        batchRequest.removeBatchRequestStepWithId(stepId);

        assertNotNull(batchRequest.getStepById(stepId3).dependsOn);
    }

    @Test
    public void addsContentTypeForBodies() throws MalformedURLException {
        IHttpRequest requestStep = mock(IHttpRequest.class);
        when(requestStep.getRequestUrl()).thenReturn(new URL(testurl));
        final BatchRequestContent batchRequest = new BatchRequestContent();
        final IJsonBackedObject body = new IJsonBackedObject() {

            @Override
            public void setRawObject(ISerializer serializer, JsonObject json) {
                // TODO Auto-generated method stub

            }

            @Override
            public AdditionalDataManager additionalDataManager() {
                // TODO Auto-generated method stub
                return null;
            }

        };
        final String stepId = batchRequest.addBatchRequestStep(requestStep, HttpMethod.POST, body);
        final BatchRequestStep<?> step = batchRequest.getStepById(stepId);
        assertNotNull(step.headers);
        assertEquals("application/json", step.headers.get("content-type"));

        when(requestStep.getHeaders()).thenReturn(Arrays.asList(new HeaderOption("dummy", "dummy")));
        final String stepId2 = batchRequest.addBatchRequestStep(requestStep, HttpMethod.POST, body);
        final BatchRequestStep<?> step2 = batchRequest.getStepById(stepId2);
        assertNotNull(step2.headers);
        assertEquals("application/json", step2.headers.get("content-type"));

        final String stepId3 = batchRequest.addBatchRequestStep(requestStep, HttpMethod.POST, Integer.valueOf(10));
        final BatchRequestStep<?> step3 = batchRequest.getStepById(stepId3);
        assertNotNull(step3.headers);
        assertNull(step3.headers.get("content-type"));

        when(requestStep.getHeaders()).thenReturn(Arrays.asList(new HeaderOption("Content-type", "application/octet-stream")));
        final String stepId4 = batchRequest.addBatchRequestStep(requestStep, HttpMethod.POST, Integer.valueOf(10));
        final BatchRequestStep<?> step4 = batchRequest.getStepById(stepId4);
        assertNotNull(step4.headers);
        assertEquals("application/octet-stream", step4.headers.get("content-type"));
    }
    @Test
    public void serializesAdditionalData() throws MalformedURLException {
        IHttpRequest requestStep = mock(IHttpRequest.class);
        when(requestStep.getRequestUrl()).thenReturn(new URL(testurl));
        final BatchRequestContent batchRequest = new BatchRequestContent();
        final String bindValue = "https://somebindvalue";
        final BatchRequestTestBody body = new BatchRequestTestBody(); // using a dynamic implementation doesn't work as "this" maps to the current test class
        body.additionalDataManager().put("teamsApp@odata.bind", new JsonPrimitive(bindValue));
        batchRequest.addBatchRequestStep(requestStep, HttpMethod.POST, body);
        final ISerializer serializer = new DefaultSerializer(mock(ILogger.class));
        final String result = serializer.serializeObject(batchRequest);
        assertNotNull(result);
        assertTrue(result.contains(bindValue));
    }
}
