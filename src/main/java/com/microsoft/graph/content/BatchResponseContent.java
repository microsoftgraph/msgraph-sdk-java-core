package com.microsoft.graph.content;

import com.google.common.io.ByteStreams;
import com.google.gson.*;
import com.microsoft.graph.CoreConstants;
import com.microsoft.graph.exceptions.ErrorConstants;
import com.microsoft.graph.requests.ResponseBodyHandler;
import com.microsoft.kiota.ResponseHandler;
import com.microsoft.kiota.serialization.Parsable;
import com.microsoft.kiota.serialization.ParsableFactory;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import okhttp3.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * A class representing the content of a batch request response.
 */
public class BatchResponseContent {
    private Response batchResponse;
    private JsonObject jsonBatchResponseObject;
    private HashMap<String, ParsableFactory<? extends Parsable>> apiErrorMappings;
    private Protocol batchResponseProtocol;
    private Request batchResponseRequest;
    /**
     * Creates a new BatchResponseContent instance.
     * @param batchResponse The response of the batch request.
     */
    public BatchResponseContent(@Nonnull Response batchResponse) {
        this(batchResponse, null);
    }
    /**
     * Creates a new BatchResponseContent instance.
     * @param batchResponse The response of the batch request.
     * @param apiErrorMappings The error mappings to use when deserializing failed responses bodies. Where an error code like 401 applies specifically to that status code, a class code like 4XX applies to all status codes within the range if the specific error code is not present.
     */
    public BatchResponseContent(@Nonnull Response batchResponse, @Nullable HashMap<String, ParsableFactory<? extends Parsable>> apiErrorMappings) {
        this.batchResponse = Objects.requireNonNull(batchResponse, String.format(Locale.US, ErrorConstants.Messages.NULL_PARAMETER, "batchResponse"));
        this.batchResponseProtocol = batchResponse.protocol();
        this.batchResponseRequest = batchResponse.request();
        this.apiErrorMappings = apiErrorMappings == null ? new HashMap<>() : apiErrorMappings;
    }
    /**
     * Gets the responses of the batch request.
     * @return The responses of the batch request.
     */
    @Nonnull
    public CompletableFuture<HashMap<String, Response>> getResponsesAsync() {
        HashMap<String, Response> responses = new HashMap<>();
        jsonBatchResponseObject = jsonBatchResponseObject != null ? jsonBatchResponseObject : getBatchResponseContentAsync().join();
        if (jsonBatchResponseObject != null) {
            JsonElement responsesElement = jsonBatchResponseObject.get(CoreConstants.BatchRequest.RESPONSES);
            if (responsesElement != null && responsesElement.isJsonArray()) { //ensure "responses" is not null and is an array.
                JsonArray responsesArray = responsesElement.getAsJsonArray();
                for (JsonElement responseElement : responsesArray) {
                    responses.put(responseElement.getAsJsonObject().get("id").getAsString(), getResponseFromJsonObject(responseElement));
                }
            }
        }
        return CompletableFuture.completedFuture(responses);
    }
    /**
     * Gets the status codes of the responses of the batch request.
     * @return The status codes of the responses of the batch request.
     */
    @Nonnull
    public CompletableFuture<HashMap<String, Integer>> getResponsesStatusCodeAsync() {
        HashMap<String, Integer> statusCodes = new HashMap<>();
        jsonBatchResponseObject = jsonBatchResponseObject != null ? jsonBatchResponseObject : getBatchResponseContentAsync().join();
        if (jsonBatchResponseObject != null) {
            JsonElement responsesElement = jsonBatchResponseObject.get(CoreConstants.BatchRequest.RESPONSES);
            if (responsesElement != null && responsesElement.isJsonArray()) { //ensure "responses" is not null and is an array.
                JsonArray responsesArray = responsesElement.getAsJsonArray();
                for (JsonElement responseElement : responsesArray) {
                    statusCodes.put(responseElement.getAsJsonObject().get("id").getAsString(), getStatusCodeFromJsonObject(responseElement));
                }
            }
        }
        return CompletableFuture.completedFuture(statusCodes);
    }
    /**
     * Gets the response within the batch response via specified id.
     * @param requestId The id of the request.
     * @return The response within the batch response via specified id, null if not found.
     */
    @Nullable
    public CompletableFuture<Response> getResponseByIdAsync(String requestId) {
        jsonBatchResponseObject = jsonBatchResponseObject != null ? jsonBatchResponseObject : getBatchResponseContentAsync().join();
        if (jsonBatchResponseObject != null) {
            JsonElement responsesElement = jsonBatchResponseObject.get(CoreConstants.BatchRequest.RESPONSES);
            if (responsesElement != null && responsesElement.isJsonArray()) { //ensure "responses" is not null and is an array.
                JsonArray responsesArray = responsesElement.getAsJsonArray();
                for (JsonElement responseElement : responsesArray) {
                    if (responseElement.getAsJsonObject().get("id").getAsString().equals(requestId)) {
                        return CompletableFuture.completedFuture(getResponseFromJsonObject(responseElement));
                    }
                }
            }
        }
        return CompletableFuture.completedFuture(null);
    }
    /**
     * Gets the response within the batch response via specified id.
     * @param requestId The id of the request.
     * @param responseHandler The response handler to use when deserializing the response body.
     * @return The response within the batch response via specified id, null if not found.
     * @param <T> The type of the response body.
     */
    @Nullable
    public <T extends Parsable> CompletableFuture<T> getResponseByIdAsync(String requestId, @Nonnull ResponseHandler responseHandler) {
        Response response = getResponseByIdAsync(requestId).join();
        if(response == null) {
            return CompletableFuture.completedFuture(null);
        }
        return responseHandler.handleResponseAsync(response, apiErrorMappings);
    }
    /**
     * Gets the response within the batch response via specified id.
     * @param requestId The id of the request.
     * @param factory The factory to use when deserializing the response body.
     * @return The response within the batch response via specified id, null if not found.
     * @param <T> The type of the response body.
     */
    @Nullable
    public <T extends Parsable> CompletableFuture<T> getResponseByIdAsync(String requestId, @Nonnull ParsableFactory<T> factory) {
        return this.getResponseByIdAsync(requestId, new ResponseBodyHandler<>(factory));
    }
    /**
     * Gets the response within the batch response via specified id.
     * @param requestId The id of the request.
     * @return The response within the batch response via specified id as an InputStream, null if not found.
     */
    @Nullable
    @SuppressFBWarnings
    public CompletableFuture<InputStream> getResponseStreamByIdAsync(String requestId) {
        Response response = getResponseByIdAsync(requestId).join();
        if(response != null && response.body() != null) {
            try{
                InputStream in = response.body().byteStream();
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(ByteStreams.toByteArray(in));
                return CompletableFuture.completedFuture(byteArrayInputStream);
            } catch (IOException e) {
                CompletableFuture<InputStream> exception = new CompletableFuture<>();
                exception.completeExceptionally(e);
                return exception;
            }
        }
        return CompletableFuture.completedFuture(null);
    }
    /**
     * Get the next link of the batch response.
     * @return The next link of the batch response.
     */
    @Nullable
    public CompletableFuture<String> getNextLinkAsync() {
        jsonBatchResponseObject = jsonBatchResponseObject != null ? jsonBatchResponseObject : getBatchResponseContentAsync().join();
        if(jsonBatchResponseObject != null) {
            JsonElement nextLinkElement = jsonBatchResponseObject.get(CoreConstants.Serialization.ODAta_NEXT_LINK);
            if (nextLinkElement != null && nextLinkElement.isJsonPrimitive()) {
                return CompletableFuture.completedFuture(nextLinkElement.getAsString());
            }
        }
        return CompletableFuture.completedFuture(null);
    }
    @SuppressFBWarnings
    private CompletableFuture<JsonObject> getBatchResponseContentAsync() {
        if (this.batchResponse.body() != null && this.batchResponse.body().contentType() != null) {
            InputStream in = this.batchResponse.body().byteStream();
            return CompletableFuture.completedFuture(JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject());
        }
        return CompletableFuture.completedFuture(null);
    }
    private Response getResponseFromJsonObject(JsonElement responseElement) {
        Response.Builder response = new Response.Builder();
        JsonObject responseObject = responseElement.getAsJsonObject();
        String message = null;
        if(responseObject.has(CoreConstants.BatchRequest.STATUS)) {
            response.code(responseObject.get("status").getAsInt());
        }
        String contentType = null;
        if(responseObject.has(CoreConstants.BatchRequest.HEADERS)) {
            JsonObject headers = responseObject.get("headers").getAsJsonObject();
            for(Map.Entry<String, JsonElement> header : headers.entrySet()) {
                if(header.getKey().equalsIgnoreCase("Content-Type")) {
                    contentType = header.getValue().getAsString();
                }
                response.addHeader(header.getKey(), header.getValue().getAsString());
            }
        }
        if(responseObject.has(CoreConstants.BatchRequest.BODY)) {
            String body = responseObject.get("body").toString();
            if(responseObject.get("body").isJsonObject()) {
                JsonObject bodyObject = responseObject.get("body").getAsJsonObject();
                if(bodyObject.has(CoreConstants.BatchRequest.ERROR)) {
                    JsonObject errorObject = bodyObject.get(CoreConstants.BatchRequest.ERROR).getAsJsonObject();
                    message = errorObject.get("message").getAsString();
                }
            }
            ResponseBody responseBody = ResponseBody.create(body, MediaType.parse(contentType != null ? contentType : CoreConstants.MimeTypeNames.APPLICATION_JSON));
            response.body(responseBody);
        }
        response.protocol(this.batchResponseProtocol);
        response.message(message == null ? "See status code for details" : message);
        response.request(this.batchResponseRequest);
        return response.build();
    }
    private int getStatusCodeFromJsonObject(JsonElement responseElement) {
        JsonObject responseObject = responseElement.getAsJsonObject();
        if(responseObject.has(CoreConstants.BatchRequest.STATUS)) {
            return responseObject.get("status").getAsInt();
        } else {
            throw new IllegalArgumentException("Response object does not contain status code");
        }
    }
    /**
     * Checks if the status code is a success status code.
     * @param statusCode The status code to check.
     * @return True if the status code is a success status code, false otherwise.
     */
    public static boolean isSuccessStatusCode(int statusCode) {
        return (statusCode >= 200 && statusCode < 300);
    }
}
