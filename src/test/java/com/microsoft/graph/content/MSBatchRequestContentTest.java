package com.microsoft.graph.content;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.concurrent.ThreadLocalRandom;

import com.google.gson.JsonParser;
import com.microsoft.graph.authentication.IAuthenticationProvider;
import com.microsoft.graph.core.BaseClient;
import com.microsoft.graph.core.IBaseClient;
import com.microsoft.graph.http.CoreHttpProvider;
import com.microsoft.graph.logger.ILogger;
import com.microsoft.graph.serializer.DefaultSerializer;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MSBatchRequestContentTest {

    String testurl = "http://graph.microsoft.com/me";

    @Test
    public void testMSBatchRequestContentCreation() {
        MSBatchRequestContent requestContent = new MSBatchRequestContent();
        for (int i = 0; i < 5; i++) {
            Request request = new Request.Builder().url(testurl).build();
            MSBatchRequestStep requestStep = new MSBatchRequestStep("" + i, request);
            requestContent.addBatchRequestStep(requestStep);
        }
        assertTrue(requestContent.getBatchRequestContent() != null);
    }

    @Test
    public void testGetBatchRequestContent() {
        Request request = new Request.Builder().url(testurl).build();
        MSBatchRequestStep requestStep = new MSBatchRequestStep("1", request);
        MSBatchRequestContent requestContent = new MSBatchRequestContent();
        requestContent.addBatchRequestStep(requestStep);
        String content = requestContent.getBatchRequestContent();
        String expectedContent = "{\"requests\":[{\"id\":\"1\",\"url\":\"http://graph.microsoft.com/me\",\"method\":\"GET\",\"dependsOn\":[]}]}";
        assertTrue(content.compareTo(expectedContent) == 0);
    }

    @Test
    public void testGetBatchRequestContentWithHeader() {
        Request request = new Request.Builder().url(testurl).header("testkey", "testvalue").build();
        MSBatchRequestStep requestStep = new MSBatchRequestStep("1", request);
        MSBatchRequestContent requestContent = new MSBatchRequestContent();
        requestContent.addBatchRequestStep(requestStep);
        String content = requestContent.getBatchRequestContent();
        System.out.println(content);
        String expectedContent = "{\"requests\":[{\"id\":\"1\",\"url\":\"http://graph.microsoft.com/me\",\"method\":\"GET\",\"headers\":{\"testkey\":\"testvalue\"},\"dependsOn\":[]}]}";
        assertTrue(content.compareTo(expectedContent) == 0);
    }

    @Test
    public void testRemoveBatchRequesStepWithId() {
        Request request = new Request.Builder().url(testurl).build();
        MSBatchRequestStep requestStep = new MSBatchRequestStep("1", request);
        MSBatchRequestContent requestContent = new MSBatchRequestContent();
        requestContent.addBatchRequestStep(requestStep);
        requestContent.removeBatchRequestStepWithId("1");
        String content = requestContent.getBatchRequestContent();
        String expectedContent = "{\"requests\":[]}";
        assertTrue(content.compareTo(expectedContent) == 0);
    }

    @Test
    public void testRemoveBatchRequesStepWithIdByAddingMultipleBatchSteps() {
        Request request = new Request.Builder().url(testurl).build();
        MSBatchRequestStep requestStep = new MSBatchRequestStep("1", request);

        Request request1 = new Request.Builder().url(testurl).build();
        MSBatchRequestStep requestStep1 = new MSBatchRequestStep("2", request1, "1");

        MSBatchRequestContent requestContent = new MSBatchRequestContent();
        requestContent.addBatchRequestStep(requestStep);
        requestContent.addBatchRequestStep(requestStep1);

        requestContent.removeBatchRequestStepWithId("1");
        String content = requestContent.getBatchRequestContent();
        String expectedContent = "{\"requests\":[{\"id\":\"2\",\"url\":\"http://graph.microsoft.com/me\",\"method\":\"GET\",\"dependsOn\":[]}]}";
        assertTrue(content.compareTo(expectedContent) == 0);
    }

    @Test
    public void defensiveProgrammingTests() {
        final MSBatchRequestStep mockStep = mock(MSBatchRequestStep.class);
        final HashSet<String> reservedIds = new HashSet<>();
        when(mockStep.getRequestId()).thenAnswer(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                return getNewId(reservedIds);
            }
        });

        assertThrows("the number of steps cannot exceed 20", IllegalArgumentException.class, () -> {
            new MSBatchRequestContent(mock(ILogger.class), null, null, null, null, null, null, null, null, null, null, null, null, null,
                    null, null, null, null, null, null, null, null, null);
        });
        assertThrows("the logger cannot be null", NullPointerException.class, () -> {
            new MSBatchRequestContent((ILogger) null, (MSBatchRequestStep)null);
        });

        new MSBatchRequestContent(mockStep, null); // addind a null step doesn't throw
        assertThrows("should throw argument exception", NullPointerException.class, () -> {
            new MSBatchRequestContent().addBatchRequestStep(null);
        });
        assertThrows("should throw argument exception", NullPointerException.class, () -> {
            new MSBatchRequestContent().addBatchRequestStep((Request) null);
        });

        assertThrows("the number of steps cannot exceed 20", IllegalArgumentException.class, () -> {
            final MSBatchRequestContent batchContent = new MSBatchRequestContent();
            for (int i = 0; i < MSBatchRequestContent.MAX_NUMBER_OF_REQUESTS; i++) {
                assertNotNull(batchContent.addBatchRequestStep(mock(Request.class)));
            }
            batchContent.addBatchRequestStep(mock(Request.class));
        });
        {
            final MSBatchRequestContent batchContent = new MSBatchRequestContent();
            reservedIds.clear();
            for (int i = 0; i < MSBatchRequestContent.MAX_NUMBER_OF_REQUESTS; i++) {
                assertTrue("item number " + i + " should be added successfully",
                        batchContent.addBatchRequestStep(mockStep));
            }
            assertFalse(batchContent.addBatchRequestStep(mockStep));
        }
    }

    private String getNewId(final HashSet<String> reserved) {
        String requestId;
        do {
            requestId = Integer.toString(ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE));
        } while (reserved.contains(requestId));
        reserved.add(requestId);
        return requestId;
    }

    @Test
    @SuppressWarnings("unchecked")
    public void executeBatchTest() throws Throwable {
        final MSBatchRequestStep step = new MSBatchRequestStep("1", new Request
                                                                    .Builder()
                                                                    .url(testurl)
                                                                    .method("GET", null)
                                                                    .build());
        final MSBatchRequestContent content = new MSBatchRequestContent(step);
        final OkHttpClient mHttpClient = mock(OkHttpClient.class);
        final Call mCall = mock(Call.class);
        when(mHttpClient.newCall(any(Request.class))).thenReturn(mCall);

        final CoreHttpProvider mHttpProvider = new CoreHttpProvider(new DefaultSerializer(mock(ILogger.class)), mock(ILogger.class), mHttpClient);
        final IBaseClient mClient = BaseClient.builder()
                                            .authenticationProvider(mock(IAuthenticationProvider.class))
                                            .httpProvider(mHttpProvider)
                                            .buildClient();
        final Response mResponse = new Response
                                            .Builder()
                                            .request(new Request
                                                        .Builder()
                                                        .url("https://graph.microsoft.com/v1.0/$batch")
                                                        .build())
                                            .code(200)
                                            .protocol(Protocol.HTTP_1_1)
                                            .message("OK")
                                            .addHeader("Content-type", "application/json")
                                            .body(ResponseBody.create("{\"responses\": [{\"id\": \"1\",\"status\": 200,\"body\": null}]}",
                                                    MediaType.parse("application/json")))
                                            .build();
        when(mCall.execute()).thenReturn(mResponse);
        final MSBatchResponseContent batchResponse = content.execute(mClient);
        final Response response = batchResponse.getResponseById("1");
        assertNotNull(response);
    }
    @Test
    public void responseParsing() {
        final MSBatchResponseContent batchResponse = new MSBatchResponseContent(mock(ILogger.class), "https://graph.microsoft.com/v1.0",
                                                        JsonParser.parseString("{\"requests\":[{\"id\":\"1\",\"method\":\"GET\",\"url\":\"/me\"}]}")
                                                                    .getAsJsonObject(),
                                                        JsonParser.parseString("{\"responses\": [{\"id\": \"1\",\"status\": 200,\"body\": null}]}")
                                                                    .getAsJsonObject());
        final Response response = batchResponse.getResponseById("1");
        assertNotNull(response);
    }
}
