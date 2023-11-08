package com.microsoft.graph.content;

import com.google.gson.*;
import com.microsoft.graph.CoreConstants;
import com.microsoft.graph.ErrorConstants;
import com.microsoft.graph.requests.ResponseBodyHandler;
import com.microsoft.kiota.ResponseHandler;
import com.microsoft.kiota.serialization.Parsable;
import com.microsoft.kiota.serialization.ParsableFactory;
import okhttp3.*;


import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;


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
    public BatchResponseContent(@Nonnull Response batchResponse, @Nullable Map<String, ParsableFactory<? extends Parsable>> apiErrorMappings) {
        this.batchResponse = Objects.requireNonNull(batchResponse, ErrorConstants.Messages.NULL_PARAMETER + "batchResponse");
        this.batchResponseProtocol = batchResponse.protocol();
        this.batchResponseRequest = batchResponse.request();
        this.apiErrorMappings = apiErrorMappings == null ? new HashMap<>() : new HashMap<>(apiErrorMappings);
    }
    /**
     * Gets the responses of the batch request.
     * @return The responses of the batch request.
     */
    @Nonnull
    public HashMap<String, Response> getResponses() {
        HashMap<String, Response> responses = new HashMap<>();
        jsonBatchResponseObject = jsonBatchResponseObject != null ? jsonBatchResponseObject : getBatchResponseContent();
        if (jsonBatchResponseObject != null) {
            JsonElement responsesElement = jsonBatchResponseObject.get(CoreConstants.BatchRequest.RESPONSES);
            if (responsesElement != null && responsesElement.isJsonArray()) { //ensure "responses" is not null and is an array.
                JsonArray responsesArray = responsesElement.getAsJsonArray();
                for (JsonElement responseElement : responsesArray) {
                    responses.put(responseElement.getAsJsonObject().get("id").getAsString(), getResponseFromJsonObject(responseElement));
                }
            }
        }
        return responses;
    }
    /**
     * Gets the status codes of the responses of the batch request.
     * @return The status codes of the responses of the batch request.
     */
    @Nonnull
    public HashMap<String, Integer> getResponsesStatusCode() {
        HashMap<String, Integer> statusCodes = new HashMap<>();
        jsonBatchResponseObject = jsonBatchResponseObject != null ? jsonBatchResponseObject : getBatchResponseContent();
        if (jsonBatchResponseObject != null) {
            JsonElement responsesElement = jsonBatchResponseObject.get(CoreConstants.BatchRequest.RESPONSES);
            if (responsesElement != null && responsesElement.isJsonArray()) { //ensure "responses" is not null and is an array.
                JsonArray responsesArray = responsesElement.getAsJsonArray();
                for (JsonElement responseElement : responsesArray) {
                    statusCodes.put(responseElement.getAsJsonObject().get("id").getAsString(), getStatusCodeFromJsonObject(responseElement));
                }
            }
        }
        return statusCodes;
    }
    /**
     * Gets the response within the batch response via specified id.
     * @param requestId The id of the request.
     * @return The response within the batch response via specified id, null if not found.
     */
    @Nullable
    public Response getResponseById(@Nonnull String requestId) {
        Objects.requireNonNull(requestId);
        if(!requestId.isEmpty()) {
            jsonBatchResponseObject = jsonBatchResponseObject != null ? jsonBatchResponseObject : getBatchResponseContent();
            if (jsonBatchResponseObject != null) {
                JsonElement responsesElement = jsonBatchResponseObject.get(CoreConstants.BatchRequest.RESPONSES);
                if (responsesElement != null && responsesElement.isJsonArray()) { //ensure "responses" is not null and is an array.
                    JsonArray responsesArray = responsesElement.getAsJsonArray();
                    for (JsonElement responseElement : responsesArray) {
                        if (responseElement.getAsJsonObject().get("id").getAsString().equals(requestId)) {
                            return getResponseFromJsonObject(responseElement);
                        }
                    }
                }
            }
        }
        return null;
    }
    /**
     * Gets the response within the batch response via specified id.
     * @param requestId The id of the request.
     * @param responseHandler The response handler to use when deserializing the response body.
     * @return The response within the batch response via specified id, null if not found.
     * @param <T> The type of the response body.
     */
    @Nullable
    public <T extends Parsable> T getResponseById(@Nonnull String requestId, @Nonnull ResponseHandler responseHandler) {
        Response response = getResponseById(requestId);
        if(response == null) {
            return null;
        }
        return responseHandler.handleResponse(response, apiErrorMappings);
    }
    /**
     * Gets the response within the batch response via specified id.
     * @param requestId The id of the request.
     * @param factory The factory to use when deserializing the response body.
     * @return The response within the batch response via specified id, null if not found.
     * @param <T> The type of the response body.
     */
    @Nullable
    public <T extends Parsable> T getResponseById(@Nonnull String requestId, @Nonnull ParsableFactory<T> factory) {
        return this.getResponseById(requestId, new ResponseBodyHandler<>(factory));
    }
    /**
     * Gets the response within the batch response via specified id.
     * @param requestId The id of the request.
     * @return The response within the batch response via specified id as an InputStream, null if not found.
     */
    @Nullable
    public InputStream getResponseStreamById(@Nonnull String requestId) {
        Response response = getResponseById(requestId);
        if(response != null && response.body() != null) {
            InputStream in = response.body().byteStream();
            return in;
        }
        return null;
    }
    /**
     * Get the next link of the batch response.
     * @return The next link of the batch response.
     */
    @Nullable
    public String getNextLink() {
        jsonBatchResponseObject = jsonBatchResponseObject != null ? jsonBatchResponseObject : getBatchResponseContent();
        if(jsonBatchResponseObject != null) {
            JsonElement nextLinkElement = jsonBatchResponseObject.get(CoreConstants.Serialization.ODATA_NEXT_LINK);
            if (nextLinkElement != null && nextLinkElement.isJsonPrimitive()) {
                return nextLinkElement.getAsString();
            }
        }
        return null;
    }
    private JsonObject getBatchResponseContent() {
        if (this.batchResponse.body() != null && this.batchResponse.body().contentType() != null) {
            InputStream in = this.batchResponse.body().byteStream();
            try(InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
                return JsonParser.parseReader(reader).getAsJsonObject();
            } catch (IOException e) {
                return null;
            }
        }
        return null;
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
            JsonObject headers = responseObject.get(CoreConstants.BatchRequest.HEADERS).getAsJsonObject();
            for(Map.Entry<String, JsonElement> header : headers.entrySet()) {
                String key = header.getKey();
                String value = header.getValue().getAsString();
                if(key.equalsIgnoreCase("Content-Type")) {
                    contentType = value;
                }
                response.addHeader(key, value);
            }
        }
        if(responseObject.has(CoreConstants.BatchRequest.BODY)) {
            String body = responseObject.get(CoreConstants.BatchRequest.BODY).toString();
            if(responseObject.get("body").isJsonObject()) {
                JsonObject bodyObject = responseObject.get("body").getAsJsonObject();
                if(bodyObject.has(CoreConstants.BatchRequest.ERROR)) {
                    JsonObject errorObject = bodyObject.get(CoreConstants.BatchRequest.ERROR).getAsJsonObject();
                    message = errorObject.get("message").getAsString();
                }
            }
            ResponseBody responseBody = ResponseBody.create(body, MediaType.parse(contentType != null ? contentType : CoreConstants.MimeTypeNames.APPLICATION_JSON));
            response.body(responseBody);
            responseBody.close();
        }
        response.protocol(this.batchResponseProtocol);
        response.message(message == null ? "See status code for details" : message);
        response.request(this.batchResponseRequest);
        return response.build();
    }

    private int getStatusCodeFromJsonObject(JsonElement responseElement) {
        JsonObject responseObject = responseElement.getAsJsonObject();
        if(responseObject.has(CoreConstants.BatchRequest.STATUS)) {
            return responseObject.get(CoreConstants.BatchRequest.STATUS).getAsInt();
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
