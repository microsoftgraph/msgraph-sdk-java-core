package com.microsoft.graph.core.content;

import com.microsoft.graph.core.CoreConstants;
import com.microsoft.graph.core.ErrorConstants;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.microsoft.graph.core.BaseClient;
import com.microsoft.graph.core.models.BatchRequestStep;
import com.microsoft.graph.core.requests.IBaseClient;
import com.microsoft.kiota.HttpMethod;
import com.microsoft.kiota.RequestInformation;
import com.microsoft.kiota.authentication.AnonymousAuthenticationProvider;

import com.microsoft.kiota.authentication.AuthenticationProvider;
import com.microsoft.kiota.http.OkHttpRequestAdapter;
import com.microsoft.kiota.http.middleware.UrlReplaceHandler;
import okhttp3.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.microsoft.graph.core.CoreConstants.ReplacementConstants.USERS_ENDPOINT_WITH_REPLACE_TOKEN;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class BatchRequestContentTest {
    static final String requestUrl = "https://graph.microsoft.com/v1.0"+USERS_ENDPOINT_WITH_REPLACE_TOKEN;
    private final IBaseClient client = new BaseClient(new AnonymousAuthenticationProvider(), requestUrl);
    private final Request defaultTestRequest = new Request.Builder().url(requestUrl).build();

    @Test
    void BatchRequestContent_DefaultInitialization() {
        BatchRequestContent batchRequestContent = new BatchRequestContent(client);

        assertNotNull(batchRequestContent.getBatchRequestSteps());
        assertEquals(0, batchRequestContent.getBatchRequestSteps().size());
    }
    @Test
    void BatchRequestContent_InitializeWithBatchRequestSteps() {
        ArrayList<BatchRequestStep> requestStepList = new ArrayList<>();
        for(int i = 0; i < 5; i++) {
            requestStepList.add(new BatchRequestStep(String.valueOf(i), defaultTestRequest));
        }
        BatchRequestContent batchRequestContent = new BatchRequestContent(client, requestStepList);

        assertNotNull(batchRequestContent.getBatchRequestSteps());
        assertEquals(5, batchRequestContent.getBatchRequestSteps().size());
    }
    @Test
    void BatchRequestContent_InitializeWithInvalidDependsOnIds() {
        BatchRequestStep requestStep = new BatchRequestStep("1", defaultTestRequest);
        BatchRequestStep requestStep2 = new BatchRequestStep("2", defaultTestRequest, Arrays.asList("3"));
        try {
            new BatchRequestContent(client, Arrays.asList(requestStep, requestStep2));
        } catch (IllegalArgumentException ex) {
            assertEquals(ErrorConstants.Messages.INVALID_DEPENDS_ON_REQUEST_ID, ex.getMessage());
        }
    }

    @Test
    void BatchRequestContent_RetainsOrderOfBatchRequestSteps() throws Exception {
        BatchRequestStep requestStep = new BatchRequestStep("1", defaultTestRequest);
        BatchRequestStep requestStep2 = new BatchRequestStep("ab23", defaultTestRequest);
        BatchRequestStep requestStep3 = new BatchRequestStep("b", defaultTestRequest, Arrays.asList("1"));

        BatchRequestContent batchRequestContent = new BatchRequestContent(client, Arrays.asList(requestStep, requestStep2, requestStep3));
        InputStream batchRequestPayload = batchRequestContent.getBatchRequestContent();
        String requestContentString = readInputStream(batchRequestPayload);
        JsonElement jsonElement = JsonParser.parseString(requestContentString);
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        if (jsonObject.has("requests")) {
            JsonElement requests = jsonObject.get("requests");
            assertTrue(requests.isJsonArray());
            JsonArray jsonArray = requests.getAsJsonArray();
            assertEquals(3, jsonArray.size());
            assertEquals("1", jsonArray.get(0).getAsJsonObject().get("id").getAsString());
            assertEquals("ab23", jsonArray.get(1).getAsJsonObject().get("id").getAsString());
            assertEquals("b", jsonArray.get(2).getAsJsonObject().get("id").getAsString());
        }
    }

    @Test
    void BatchRequestContent_AddBatchRequestStepWithNewRequestStep() {
        BatchRequestStep batchRequestStep = new BatchRequestStep("1", defaultTestRequest);
        BatchRequestContent batchRequestContent = new BatchRequestContent(client);

        assertTrue(batchRequestContent.getBatchRequestSteps().isEmpty());
        Assertions.assertTrue(batchRequestContent.addBatchRequestStep(batchRequestStep));
        assertFalse(batchRequestContent.getBatchRequestSteps().isEmpty());
        assertEquals(1, batchRequestContent.getBatchRequestSteps().size());
    }
    @Test
    void BatchRequestContent_AddBatchRequestStepToBatchRequestContentWithMaxSteps() {
        BatchRequestContent batchRequestContent = new BatchRequestContent(client);
        for(int i = 0; i < CoreConstants.BatchRequest.MAX_REQUESTS; i++) {
            Assertions.assertTrue(batchRequestContent.addBatchRequestStep(new BatchRequestStep(String.valueOf(i), defaultTestRequest)));
        }
        BatchRequestStep batchRequestStep21 = new BatchRequestStep("failing_id", defaultTestRequest);

        Assertions.assertFalse(batchRequestContent.addBatchRequestStep(batchRequestStep21));
        assertEquals(CoreConstants.BatchRequest.MAX_REQUESTS, batchRequestContent.getBatchRequestSteps().size());
    }
    @Test
    void BatchRequestContent_AddBatchRequestStepWithExistingRequestStep() {
        BatchRequestStep batchRequestStep = new BatchRequestStep("1", defaultTestRequest);
        BatchRequestContent batchRequestContent = new BatchRequestContent(client, Arrays.asList(batchRequestStep));

        Assertions.assertFalse(batchRequestContent.addBatchRequestStep(batchRequestStep));
        assertNotNull(batchRequestContent.getBatchRequestSteps());
        assertEquals(1, batchRequestContent.getBatchRequestSteps().size());
    }
    @Test
    void BatchRequestContent_AddBatchRequestStepWithNullRequestStep() {
        BatchRequestStep batchRequestStep = new BatchRequestStep("1", defaultTestRequest);
        BatchRequestContent batchRequestContent = new BatchRequestContent(client, Arrays.asList(batchRequestStep));

        Assertions.assertFalse(batchRequestContent.addBatchRequestStep((BatchRequestStep) null));
        assertNotNull(batchRequestContent.getBatchRequestSteps());
        assertEquals(1, batchRequestContent.getBatchRequestSteps().size());
    }
    @Test
    void BatchRequestContent_AddBatchRequestStep_WithTokenToReplaceUrl() {
        Request requestWithReplaceableToken = new Request.Builder().url("https://test-url.com"+USERS_ENDPOINT_WITH_REPLACE_TOKEN).build();
        BatchRequestStep batchRequestStep = new BatchRequestStep("1", requestWithReplaceableToken);
        BatchRequestContent batchRequestContent = new BatchRequestContent(client, Arrays.asList(batchRequestStep));

        assertNotNull(batchRequestContent.getBatchRequestSteps());
        assertEquals(1, batchRequestContent.getBatchRequestSteps().size());
        Assertions.assertEquals("https://test-url.com/me", batchRequestContent.getBatchRequestSteps().get("1").getRequest().url().toString());
    }
    @Test
    void BatchRequestContent_RemoveBatchRequestStepWithIdForExistingId() {
        BatchRequestStep batchRequestStep = new BatchRequestStep("1", defaultTestRequest);
        BatchRequestStep batchRequestStep2 = new BatchRequestStep("2", defaultTestRequest, Arrays.asList("1", "1", "1"));
        BatchRequestContent batchRequestContent = new BatchRequestContent(client, Arrays.asList(batchRequestStep, batchRequestStep2));

        assertTrue(batchRequestContent.removeBatchRequestStepWithId("1"));
        assertEquals(1, batchRequestContent.getBatchRequestSteps().size());
        Assertions.assertEquals(0, Objects.requireNonNull(batchRequestContent.getBatchRequestSteps().get("2").getDependsOn()).size());
    }
    @Test
    void BatchRequestContent_RemoveBatchRequestStepWithIdForNonExistingId() {
        BatchRequestStep batchRequestStep = new BatchRequestStep("1", defaultTestRequest);
        BatchRequestStep batchRequestStep2 = new BatchRequestStep("2", defaultTestRequest, Arrays.asList("1"));
        BatchRequestContent batchRequestContent = new BatchRequestContent(client, Arrays.asList(batchRequestStep, batchRequestStep2));

        assertFalse(batchRequestContent.removeBatchRequestStepWithId("3"));
        assertEquals(2, batchRequestContent.getBatchRequestSteps().size());
        Assertions.assertEquals(Objects.requireNonNull(batchRequestStep2.getDependsOn()).get(0), Objects.requireNonNull(batchRequestContent.getBatchRequestSteps().get("2").getDependsOn()).get(0));
    }
    @Test
    void BatchRequestContent_GetBatchRequestContentFromStep() throws Exception {
        Request request = new Request.Builder().url(requestUrl).build();
        BatchRequestStep batchRequestStep = new BatchRequestStep("1", defaultTestRequest);
        BatchRequestStep batchRequestStep2 = new BatchRequestStep("2", request, Arrays.asList("1"));
        BatchRequestContent batchRequestContent = new BatchRequestContent(client, Arrays.asList(batchRequestStep, batchRequestStep2));

        batchRequestContent.removeBatchRequestStepWithId("1");
        InputStream requestContent = batchRequestContent.getBatchRequestContent();
        String requestContentString = readInputStream(requestContent);
        requestContentString = requestContentString.replace("\n", "").replaceAll("\\s", "");
        String expectedContent = "{\"requests\":[{\"id\":\"2\",\"url\":\"/me\",\"method\":\"GET\"}]}";

        assertNotNull(requestContentString);
        assertEquals(1, batchRequestContent.getBatchRequestSteps().size());
        assertEquals(expectedContent, requestContentString);
    }
    @Test
    void BatchRequestContent_GetBatchRequestContentFromStepDoesNotModifyDateTimes() throws Exception {
        String bodyString = "{\n" +
            "  \"subject\": \"Lets go for lunch\",\n" +
            "  \"body\": {\n    \"contentType\": \"HTML\",\n" +
            "    \"content\": \"Does mid month work for you?\"\n" +
            "  },\n" +
            "  \"start\": {\n" +
            "      \"dateTime\": \"2019-03-15T12:00:00.0000\",\n" +
            "      \"timeZone\": \"Pacific Standard Time\"\n" +
            "  },\n" +
            "  \"end\": {\n" +
            "      \"dateTime\": \"2019-03-15T14:00:00.0000\",\n" +
            "      \"timeZone\": \"Pacific Standard Time\"\n" +
            "  },\n  \"location\":{\n" +
            "      \"displayName\":\"Harrys Bar\"\n" +
            "  },\n" +
            "  \"attendees\": [\n" +
            "    {\n" +
            "      \"emailAddress\": {\n" +
            "        \"address\":\"adelev@contoso.onmicrosoft.com\",\n" +
            "        \"name\": \"Adele Vance\"\n" +
            "      },\n" +
            "      \"type\": \"required\"\n" +
            "    }\n" +
            "  ]\n" +
            "}";
        Request eventRequest = new Request.Builder().url(requestUrl).method("POST", RequestBody.create(bodyString, MediaType.parse("application/json"))).build();
        BatchRequestStep batchRequestStep = new BatchRequestStep("1", new Request.Builder().url(requestUrl).build());
        BatchRequestStep batchRequestSte2 = new BatchRequestStep("2", eventRequest, Arrays.asList("1"));
        BatchRequestContent batchRequestContent = new BatchRequestContent(client, Arrays.asList(batchRequestStep, batchRequestSte2));

        InputStream stream = batchRequestContent.getBatchRequestContent();
        String requestContentString = readInputStream(stream);
        String expectedJson = "{\n" +
            "  \"requests\": [\n" +
            "    {\n" +
            "      \"id\": \"1\",\n" +
            "      \"url\": \"/me\",\n" +
            "      \"method\": \"GET\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": \"2\",\n" +
            "      \"url\": \"/me\",\n" +
            "      \"method\": \"POST\",\n" +
            "      \"dependsOn\": [\n" +
            "        \"1\"\n" +
            "      ],\n" +
            "      \"body\": {\n" +
            "        \"subject\": \"Lets go for lunch\",\n" +
            "        \"body\": {\n" +
            "          \"contentType\": \"HTML\",\n" +
            "          \"content\": \"Does mid month work for you?\"\n" +
            "        },\n" +
            "        \"start\": {\n" +
            "          \"dateTime\": \"2019-03-15T12:00:00.0000\",\n" +
            "          \"timeZone\": \"Pacific Standard Time\"\n" +
            "        },\n" +
            "        \"end\": {\n" +
            "          \"dateTime\": \"2019-03-15T14:00:00.0000\",\n" +
            "          \"timeZone\": \"Pacific Standard Time\"\n" +
            "        },\n" +
            "        \"location\": {\n" +
            "          \"displayName\": \"Harrys Bar\"\n" +
            "        },\n" +
            "        \"attendees\": [\n" +
            "          {\n" +
            "            \"emailAddress\": {\n" +
            "              \"address\": \"adelev@contoso.onmicrosoft.com\",\n" +
            "              \"name\": \"Adele Vance\"\n" +
            "            },\n" +
            "            \"type\": \"required\"\n" +
            "          }\n" +
            "        ]\n" +
            "      },\n" +
            "      \"headers\": {\n" +
            "        \"Content-Type\": \"application/json; charset=utf-8\"\n" +
            "      }\n" +
            "    }\n" +
            "  ]\n" +
            "}";
        // Ignore indentation and whitespace
        requestContentString = requestContentString.replace("\n", "").replaceAll("\\s", "");
        expectedJson = expectedJson.replace("\n", "").replaceAll("\\s", "");
        assertNotNull(requestContentString);
        assertEquals(2, batchRequestContent.getBatchRequestSteps().size());
        assertEquals(expectedJson, requestContentString);
    }
    @Test
    void BatchRequestContent_DoNotAddAuthorizationHeader() throws Exception {
        OkHttpRequestAdapter adapter = new OkHttpRequestAdapter(mock(AuthenticationProvider.class));

        String expectedJson = "{\n" +
            "    \"requests\": [\n" +
            "        {\n" +
            "            \"id\": \"1\",\n" +
            "            \"url\": \"/me\",\n" +
            "            \"method\": \"GET\",\n" +
            "            \"headers\": {\n" +
            "                \"accept\": \"application/json\"\n" +
            "            }\n" +
            "        },\n" +
            "        {\n" +
            "            \"id\": \"2\",\n" +
            "            \"url\": \"/me\",\n" +
            "            \"method\": \"GET\"\n" +
            "        }\n" +
            "    ]\n" +
            "}";
        //The following string is the same size as a token
        String longBearerString = "bbcbbbcbccbbabbacbccccbccabbcacacaaabccbccbbbbaabbabcccccbcbcacbbccbcbcaaacaacccacccbabacccabbccbccacccabcbbbbbacaacccabaaacaabcbacbaabcacabcbaaaccaccbbaaaabbbabbcaabbacccccaabbcabbbbbbbaaababaaabbbbcbbbcacbaaccaccabbcbabbabacbcccacbaccacaacaaacbacbaaaacbcbbacbcaaaaabcababbbcaabaaaabaaccbaccaababcbccbbacbaaabcbcbcbaaabcccabcacbbcbbabcccaccbacaaccaaaabcaacaccababbcbcabbccbaaaaaacccbcbccbaaccabbacbaaaacaccabcbbbcaabccccbbabbccaaaccbbbabbabcbcabcbccabbaabaacaaabbacaaccbcabaaaabcaabbabccabbcabcabbbaaaacccbcbcbbaacbbbbbcbbabcbabcbbcbbbaacccaababaccbaabcccccabbcabcababacbcaacbbaabaaacaabbacabcbcabcaabcccccacbaaacccbcabacbcbbbcccaaabacccaabcbcaababaabbacacabcbccacbbcacbbcaaccbbbcccbaaaacbcacabbcaaaacbcaacaccccbbaaabcccaacbabbbcbbccbacabccabaabacbbbbbcbaaaaaccabcbccabcccbcccabababbbbcbbcbbcbcabaabaabccbabcbbbabaaacaaaabcbcabaccaaaaacbaaabcbaccbaccbabacbcabbcbcbbaabbbbccaacccaabacacbabbcacabcbaccbcacbccaabcbbacbacbacbbaaccaaaaccacbcababccccccbbcbacacaaabaaaccbaabaacccbaaabcbcaabaaaaabcabacbabcbbccccbacbaabccaaabcccbbacbbacacaccabbcaacbbbbcbcbbcaabaacbbbcbbcbaaacccbacbaabacacbbabcaaaacaabacbaacaaaabbcacbacbcccacbcabcccacacaaccbbbcaacabcccaaacbabaaccbcbaacacbacaababcabcbccabcabcccaacabacabccaacbbcabbcaacbccaababacccaccabacbbbaabaccbcabcaabbcccacccbcbcabbccabbabaaaccacccbcbacabcaabcaccbbcbaaacbaabbbbcbccbbcccaababababaabacccbbbcabbaaacbcaaabccbbbccabbbcccbcacacaaabbabcacbacaacbbbcbbbbbccabbbabcabbcbacccaaabaaacbaabbacabbabcbcbcacbbaabbabcbcaacbabbcccbabaaccabbacbcaaacabbbbcaacbccbbbbacbcabbbaabcacaaabaabbaaccabbcabcabbacaaaacacabbabccacbbabbbbcabbaaccabcccaabbaaaacaabcbacabbaacaccbbbbaaaaacbcbacbbaaaabbabcaacaaacbbaabcccbbcbaacabbbbcaccaaaabcacbcbaaabbbcabcabcbbbbacbaccaacbccaacbbcaccaaaaacbabbbcbcbacbacbaccaacbcbcbbcaaaabaaabaabccaaaabbcabaaabcbcccbbcbaacacbbacacbabbcbaccabacbabcbcaabbbaabccccccaaccbcbccccbbbbcabaaacbbbaacbbaccaabcbcaacaacaacacaababcccbacbbccccbcacbcbcaacaaaacccccccaccaababaacbaabbcbbbccaacbabbcbcaaabbccacbbaabbbbcbbccbcccbbcacabaaacbacacbcaaabcbccacacccbbaacbacbbcbabbcbbbbcaccbaaccbcbcaabcababcbbbcccbcbaababcacbacbbbacacacabbccabbbaaaaacaccbbccbccbabaababcbbccabcaaacaccacabbaabacacabaccabacbacabbccbabaccbabcccbbcbbbaaabbccabbcbbbacacbbbabbcbbacbcabacaccabbbcbabbcbcacbcbbabbbbcabcbbabbbcaaccbaaaaccbababbbaabcbbbaacabbbbcabcabbcabbacabbccccaabaaaaabbcbabacbacbabcabcccabbbccbbcccaacacaabbcbabcbabaaaababbbacabaacbabbabcbbbcbccbacbcbccbbbccccbacaccbaccaaabbaacbbaaabbbcaccbabbcccbbbbccacbbaaacabbbbaabbabcccabcbcbbccccbacccabbbaaabcacccaabbabaccccbbbcccccaacbbbccbcabbbcccababbbcacccccccabccbbcaabccbbbaaccabbcaabcacabbcbbabcccaccccaaacbbbccaaabcbacabbbacbaccaabcbabababbcbcacaabcaabcbcbbcaaacaacabaaababbbacaccababaccbacacacacacbcccbabcbabcabccbaabcccababcbacbccccccacacbbacccccbaccbacaacbacacbcccccaaaacbaaaaccbacbbcacccbbbaabaaaccaccbcabcccccacaaaabcbabbacbbbcaaababcbacccbabcbaaabbcbaaacaabbcaaccaaccbacbaaaaaaabbaacaaabacbbcaacaacabbcabaccaaacbaccccbcccbcbcaaacbacaacccaccaacabacaaaabbbbbbbcacacbabccacacabbbababbbbcbabaaacaaacbacbcabbccacaacccbbbcbbacaccbbbaaabababbcbaacbcabcabaaccbcaaacbbbaacacccbbcaabcbacabbccbcbbbabbbaabacacaccaabbcbbaccbaaabcabbababaccca";
        RequestInformation requestInfo = new RequestInformation();
        requestInfo.urlTemplate = "{+baseurl}/users/{user%2Did}{?%24expand,%24select}";
        HashMap<String, Object> pathParameters = new HashMap<>();
        pathParameters.put("baseurl", "https://graph.microsoft.com/v1.0");
        pathParameters.put("user%2Did", "TokenToReplace");
        requestInfo.pathParameters = pathParameters;
        requestInfo.httpMethod = HttpMethod.GET;
        // Only one header should be present in the headers object of the Json Body
        requestInfo.headers.add("accept", "application/json");
        requestInfo.headers.add("authorization", longBearerString);
        RequestInformation requestInfo2 = new RequestInformation();
        requestInfo2.urlTemplate = "{+baseurl}/users/{user%2Did}{?%24expand,%24select}";
        HashMap<String, Object> pathParameters2 = new HashMap<>();
        pathParameters2.put("baseurl", "https://graph.microsoft.com/v1.0");
        pathParameters2.put("user%2Did", "TokenToReplace");
        requestInfo2.pathParameters = pathParameters2;
        requestInfo2.httpMethod = HttpMethod.GET;
        // No headers object should be present in the Json body
        requestInfo2.headers.add("AuthoriZation", longBearerString); // Test with strange casing

        BatchRequestContent batchRequestContent = new BatchRequestContent(client);
        batchRequestContent.addBatchRequestStep(new BatchRequestStep("1",adapter.convertToNativeRequest(requestInfo)));
        batchRequestContent.addBatchRequestStep(new BatchRequestStep("2",adapter.convertToNativeRequest(requestInfo2)));

        InputStream stream = batchRequestContent.getBatchRequestContent();
        String requestContentString = readInputStream(stream);
        requestContentString = requestContentString.replace("\n", "").replaceAll("\\s", "");
        expectedJson = expectedJson.replace("\n", "").replaceAll("\\s", "");

        assertNotNull(requestContentString);
        assertEquals(expectedJson, requestContentString);
    }
    @Test
    void BatchRequestContent_AddBatchRequestStepWithHttpRequestMessage() {
        BatchRequestContent batchRequestContent = new BatchRequestContent(client);
        assertTrue(batchRequestContent.getBatchRequestSteps().isEmpty());

        Request request = new Request.Builder().url(requestUrl).build();
        String requestId = batchRequestContent.addBatchRequestStep(request);
        request = UrlReplaceHandler.replaceRequestUrl(request, CoreConstants.ReplacementConstants.getDefaultReplacementPairs());

        assertNotNull(requestId);
        assertNotNull(batchRequestContent.getBatchRequestSteps());
        assertEquals(1, batchRequestContent.getBatchRequestSteps().size());
        Assertions.assertEquals(batchRequestContent.getBatchRequestSteps().get(requestId).getRequest().url().uri().toString(), request.url().uri().toString());
        Assertions.assertEquals(batchRequestContent.getBatchRequestSteps().get(requestId).getRequest().method(), request.method());
    }

    @Test
    void BatchRequestContent_AddBatchRequestStepWithHttpRequestMessageToBatchRequestContentWithMaxSteps() {
        BatchRequestContent batchRequestContent = new BatchRequestContent(client);
        for(int i = 0; i < CoreConstants.BatchRequest.MAX_REQUESTS; i++) {
            Request request = new Request.Builder().url(requestUrl).build();
            String requestId = batchRequestContent.addBatchRequestStep(request);
            assertNotNull(requestId);
        }
        Request extraRequest = new Request.Builder().url(requestUrl).build();
        try {
            batchRequestContent.addBatchRequestStep(extraRequest);
        } catch (RuntimeException e) {
            assertEquals(String.format(Locale.US,ErrorConstants.Messages.MAXIMUM_VALUE_EXCEEDED, "Number of request steps", CoreConstants.BatchRequest.MAX_REQUESTS), e.getMessage());
            assertNotNull(batchRequestContent.getBatchRequestSteps());
            assertEquals(CoreConstants.BatchRequest.MAX_REQUESTS, batchRequestContent.getBatchRequestSteps().size());
        }
    }
    @Test
    void BatchRequestContent_AddBatchRequestStepWithBaseRequest() throws IOException {
        BatchRequestContent batchRequestContent = new BatchRequestContent(client);
        RequestInformation requestInfo = new RequestInformation();
        requestInfo.urlTemplate = requestUrl; //URL here is https://graph.microsoft.com/v1.0/users/TokenToReplace
        requestInfo.httpMethod = HttpMethod.GET;

        assertTrue(batchRequestContent.getBatchRequestSteps().isEmpty());
        String requestId = batchRequestContent.addBatchRequestStep(requestInfo);
        String expectedUrl = "https://graph.microsoft.com/v1.0/me"; //We expect the url to be changed because it contains the default replacement pairs


        assertNotNull(requestId);
        assertNotNull(batchRequestContent.getBatchRequestSteps());
        assertEquals(1, batchRequestContent.getBatchRequestSteps().size());
        Assertions.assertEquals(expectedUrl, batchRequestContent.getBatchRequestSteps().get(requestId).getRequest().url().uri().toString());
        Assertions.assertEquals(batchRequestContent.getBatchRequestSteps().get(requestId).getRequest().method(), requestInfo.httpMethod.toString());
    }
    @Test
    void BatchRequestContent_AddBatchRequestStepWithBaseRequestWithHeaderOptions() throws Exception {
        BatchRequestContent batchRequestContent = new BatchRequestContent(client);
        Request request = new Request.Builder()
            .url(requestUrl)
            .post(RequestBody.create("{}", MediaType.get(CoreConstants.MimeTypeNames.APPLICATION_JSON)))//MediaType.parse(CoreConstants.MimeTypeNames.APPLICATION_JSON) ))
            .addHeader("ConsistencyLevel", "eventual")
            .build();
        String requestId = batchRequestContent.addBatchRequestStep(request);
        assertNotNull(batchRequestContent.getBatchRequestSteps());
        assertEquals(1, batchRequestContent.getBatchRequestSteps().size());
        assertTrue(batchRequestContent.getBatchRequestSteps().get(requestId).getRequest().headers().size() > 0);
        assertNotNull(Objects.requireNonNull(batchRequestContent.getBatchRequestSteps().get(requestId).getRequest().body()).contentType());

        InputStream stream = batchRequestContent.getBatchRequestContent();
        String requestContentString = readInputStream(stream);
        String expectedJsonSection = "      \"url\": \"/me\"," +
            "      \"method\": \"POST\"," +
            "      \"body\": {}," +
            "      \"headers\": {" +
            "        \"ConsistencyLevel\": \"eventual\"," + // Ensure the requestMessage headers are present
            "        \"Content-Type\": \"application/json; charset=utf-8\"" + // Ensure the content headers are present
            "      }";
        //Ignore indentation and whitespace
        requestContentString = requestContentString.replaceAll("\\s", "").replace("\n", "");
        expectedJsonSection = expectedJsonSection.replace("\n", "").replaceAll("\\s", "");
        assertTrue(requestContentString.contains(expectedJsonSection));
    }
    @Test
    void BatchRequestContent_AddBatchRequestStepWithBaseRequestToBatchRequestContentWithMaxSteps() {
        BatchRequestContent batchRequestContent = new BatchRequestContent(client);
        for(int i = 0; i < CoreConstants.BatchRequest.MAX_REQUESTS; i++) {
            RequestInformation requestInfo = new RequestInformation();
            requestInfo.urlTemplate = requestUrl;
            requestInfo.httpMethod = HttpMethod.GET;
            String requestId = batchRequestContent.addBatchRequestStep(requestInfo);
            assertNotNull(requestId);
        }
        RequestInformation extraRequestInfo = new RequestInformation();
        extraRequestInfo.urlTemplate = requestUrl;
        extraRequestInfo.httpMethod = HttpMethod.GET;
        try {
            batchRequestContent.addBatchRequestStep(extraRequestInfo);
        } catch (Exception e) {
            assertEquals(String.format(Locale.US,ErrorConstants.Messages.MAXIMUM_VALUE_EXCEEDED, "Number of request steps", CoreConstants.BatchRequest.MAX_REQUESTS), e.getMessage());
            assertNotNull(batchRequestContent.getBatchRequestSteps());
            assertEquals(CoreConstants.BatchRequest.MAX_REQUESTS, batchRequestContent.getBatchRequestSteps().size());
        }
    }
    @ParameterizedTest
    @CsvSource({
        "https://graph.microsoft.com/v1.0/me , /me" ,
        "https://graph.microsoft.com/beta/me , /me" ,
        "https://graph.microsoft.com/v1.0/users/abcbeta123@wonderemail.com/events , /users/abcbeta123@wonderemail.com/events" ,
        "https://graph.microsoft.com/beta/users/abcbeta123@wonderemail.com/events , /users/abcbeta123@wonderemail.com/events" ,
        "https://graph.microsoft.com/v1.0/users?$filter=identities/any(id:id/issuer%20eq%20'$74707853-18b3-411f-ad57-2ef65f6fdeb0'%20and%20id/issuerAssignedId%20eq%20'**bobbetancourt@fakeemail.com**') , /users?$filter=identities/any(id:id/issuer%20eq%20%27$74707853-18b3-411f-ad57-2ef65f6fdeb0%27%20and%20id/issuerAssignedId%20eq%20%27**bobbetancourt@fakeemail.com**%27)" ,
        "https://graph.microsoft.com/beta/users?$filter=identities/any(id:id/issuer%20eq%20'$74707853-18b3-411f-ad57-2ef65f6fdeb0'%20and%20id/issuerAssignedId%20eq%20'**bobbetancourt@fakeemail.com**')&$top=1 , /users?$filter=identities/any(id:id/issuer%20eq%20%27$74707853-18b3-411f-ad57-2ef65f6fdeb0%27%20and%20id/issuerAssignedId%20eq%20%27**bobbetancourt@fakeemail.com**%27)&$top=1" ,
    })
    void BatchRequestContent_AddBatchRequestStepWithBaseRequestProperlySetsVersion(ArgumentsAccessor argumentsAccessor) throws Exception {
        Request request = new Request.Builder().url(argumentsAccessor.getString(0)).build();
        BatchRequestStep batchRequestStep = new BatchRequestStep("1", request);
        BatchRequestContent batchRequestContent = new BatchRequestContent(client);
        assertTrue(batchRequestContent.getBatchRequestSteps().isEmpty());

        batchRequestContent.addBatchRequestStep(batchRequestStep);
        InputStream stream = batchRequestContent.getBatchRequestContent();
        String requestContentString = readInputStream(stream);
        String expectedJson = "{" +
            "  \"requests\": [" +
            "    {" +
            "      \"id\": \"1\"," +
            "      \"url\": \""+ argumentsAccessor.getString(1) +"\"," +
            "      \"method\": \"GET\"" +
            "    }" +
            "  ]" +
            "}";
        //Ignore indentation and whitespace
        expectedJson = expectedJson.replaceAll("\\s", "").replace("\n", "");
        requestContentString = requestContentString.replaceAll("\\s", "").replace("\n", "");
        assertEquals(expectedJson, requestContentString);
    }
    private static String readInputStream(InputStream stream) throws Exception {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = stream.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        return new String(result.toByteArray(), StandardCharsets.UTF_8);
    }
}
