package com.microsoft.graph.core.content;

import com.google.gson.*;
import com.google.gson.stream.JsonWriter;
import com.microsoft.graph.core.CoreConstants;
import com.microsoft.graph.core.ErrorConstants;
import com.microsoft.graph.core.models.BatchRequestStep;
import com.microsoft.graph.core.requests.IBaseClient;
import com.microsoft.kiota.Compatibility;
import com.microsoft.kiota.RequestAdapter;
import com.microsoft.kiota.RequestInformation;

import okhttp3.*;
import okio.Buffer;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * A class representing the content of a batch request.
 */
public class BatchRequestContent {
    private LinkedHashMap<String, BatchRequestStep> batchRequestSteps;
    private RequestAdapter requestAdapter;
    private final String maxStepsExceededMessage = String.format(Locale.US,ErrorConstants.Messages.MAXIMUM_VALUE_EXCEEDED, "Number of request steps", CoreConstants.BatchRequest.MAX_REQUESTS);

    /**
     * Creates a new BatchRequestContent object.
     * @param client The IBaseClient for handling requests.
     */
    public BatchRequestContent(@Nonnull IBaseClient client) {
        this(client, new ArrayList<>());
    }
    /**
     * Creates a new BatchRequestContent object.
     * @param baseClient The IBaseClient for handling requests.
     * @param batchRequestSteps The list of BatchRequestSteps to add to the batch request.
     */
    public BatchRequestContent(@Nonnull IBaseClient baseClient, @Nonnull List<BatchRequestStep> batchRequestSteps) {
        this(baseClient.getRequestAdapter(), batchRequestSteps);
    }
    /**
     * Creates a new BatchRequestContent object.
     * @param requestAdapter The request adapter to use for requests.
     * @param batchRequestSteps The list of BatchRequestSteps to add to the batch request.
     */
    public BatchRequestContent(@Nonnull RequestAdapter requestAdapter, @Nonnull List<BatchRequestStep> batchRequestSteps) {
        this.requestAdapter = Objects.requireNonNull(requestAdapter, ErrorConstants.Messages.NULL_PARAMETER + "requestAdapter");

        Objects.requireNonNull(batchRequestSteps, ErrorConstants.Messages.NULL_PARAMETER + "batchRequestSteps");
        if(batchRequestSteps.size() >= CoreConstants.BatchRequest.MAX_REQUESTS) {
            throw new IllegalArgumentException(maxStepsExceededMessage);
        }

        this.batchRequestSteps = new LinkedHashMap<>();
        for (BatchRequestStep requestStep : batchRequestSteps) {
            addBatchRequestStep(requestStep);
        }
    }
    /**
     * Gets the batch request steps.
     * @return The batch request steps.
     */
    @Nonnull
    public Map<String, BatchRequestStep> getBatchRequestSteps() {

        return new LinkedHashMap<>(batchRequestSteps);
    }
    /**
     * Adds a batch request step to the batch request.
     * @param requestStep The batch request step to add.
     * @return True if the batch request step was added, false otherwise.
     */
    public boolean addBatchRequestStep(@Nullable BatchRequestStep requestStep)
    {
        if( requestStep == null
            || this.batchRequestSteps.containsKey(requestStep.getRequestId())
            || this.batchRequestSteps.size() >= CoreConstants.BatchRequest.MAX_REQUESTS) {
            return false;
        }
        if(!containsCorrespondingRequestId(requestStep.getDependsOn())) {
            throw new IllegalArgumentException(ErrorConstants.Messages.INVALID_DEPENDS_ON_REQUEST_ID);
        }
        this.batchRequestSteps.put(requestStep.getRequestId(), requestStep);
        return true;
    }
    /**
     * Adds a batch request step to the batch request.
     * @param request The request to add.
     * @return The request id of the added request.
     */
    @Nonnull
    public String addBatchRequestStep(@Nonnull Request request) {
        if(this.batchRequestSteps.size() >= CoreConstants.BatchRequest.MAX_REQUESTS) {
            throw new IllegalArgumentException(maxStepsExceededMessage);
        }
        String requestId = java.util.UUID.randomUUID().toString();
        BatchRequestStep requestStep = new BatchRequestStep(requestId, request);
        this.batchRequestSteps.put(requestId, requestStep);
        return requestId;
    }
    /**
     * Adds a batch request step to the batch request.
     * @param requestInformation The request information to add.
     * @return The request id of the added request.
     */
    @Nonnull
    public String addBatchRequestStep(@Nonnull RequestInformation requestInformation) {
        if(this.batchRequestSteps.size() >= CoreConstants.BatchRequest.MAX_REQUESTS) {
            throw new IllegalArgumentException(maxStepsExceededMessage);
        }
        String requestId = java.util.UUID.randomUUID().toString();
        final Request request = this.requestAdapter.convertToNativeRequest(requestInformation);
        BatchRequestStep requestStep = new BatchRequestStep(requestId, request);
        this.batchRequestSteps.put(requestId, requestStep);
        return requestId;
    }
    /**
     * Removes a batch request step from the batch request.
     * @param requestId The request id of the request to remove.
     * @return True if the request was removed, false otherwise.
     */
    public boolean removeBatchRequestStepWithId(@Nonnull String requestId) {
        if(Compatibility.isBlank(requestId)) {
            throw new IllegalArgumentException("requestId cannot be null or empty.");
        }
        boolean isRemoved = false;
        if(this.batchRequestSteps.containsKey(requestId)) {
            this.batchRequestSteps.remove(requestId);
            isRemoved = true;
            for (BatchRequestStep requestStep : this.batchRequestSteps.values()) {
                requestStep.removeDependsOnId(requestId);
            }
        }
        return isRemoved;
    }

    /**
     * Builds a BatchRequestContent object from failed requests.
     * @param responseStatusCodes The response status codes of the failed requests.
     * @return The BatchRequestContent object.
     */
    @Nonnull
    public BatchRequestContent createNewBatchFromFailedRequests (@Nonnull Map<String, Integer> responseStatusCodes) {
        BatchRequestContent request = new BatchRequestContent(this.requestAdapter, new ArrayList<>());
        responseStatusCodes.forEach((key, value) -> {
            if(this.batchRequestSteps.containsKey(key) && !BatchResponseContent.isSuccessStatusCode(value)) {
                request.addBatchRequestStep(this.batchRequestSteps.get(key).getRequest());
            }
        });
        return request;
    }
    /**
     * Builds the json content of the batch request.
     * @return The json content of the batch request as an InputStream.
     * @throws IOException if there was an error writing the batch request content.
     */
    @Nonnull
    public InputStream getBatchRequestContent() throws IOException {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        JsonWriter writer = new JsonWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
        writer.beginObject();
        writer.name(CoreConstants.BatchRequest.REQUESTS);
        writer.beginArray();
        for (BatchRequestStep requestStep : this.batchRequestSteps.values()) {
            writeBatchRequestStep(requestStep, writer);
        }
        writer.endArray();
        writer.endObject();
        writer.flush();
        final ByteArrayInputStream out = new ByteArrayInputStream(outputStream.toByteArray());
        outputStream.close();
        return out;
    }
    private static final String AUTHORIZATION_HEADER_KEY = "authorization";
    private void writeBatchRequestStep(BatchRequestStep requestStep, JsonWriter writer) throws IOException {
            Request request = requestStep.getRequest();
            writer.beginObject();
            writer.name(CoreConstants.BatchRequest.ID).value(requestStep.getRequestId());
            writer.name(CoreConstants.BatchRequest.URL).value(getRelativeUrl(request.url()));
            writer.name(CoreConstants.BatchRequest.METHOD).value(request.method());

            List<String> dependsOn = requestStep.getDependsOn();
            if(!dependsOn.isEmpty()) {
                writer.name(CoreConstants.BatchRequest.DEPENDS_ON);
                writer.beginArray();
                for(String id : dependsOn) {
                    writer.value(id);
                }
                writer.endArray();
            }
            RequestBody requestBody = request.body();
            Headers headers = request.headers();
            if(requestBody != null) {
                String contentType = Objects.requireNonNull(requestBody.contentType()).toString();
                headers = headers.newBuilder().add("Content-Type", contentType).build();
                writer.name(CoreConstants.BatchRequest.BODY);
                if(contentType.toLowerCase(Locale.US).contains(CoreConstants.MimeTypeNames.APPLICATION_JSON)){
                    JsonObject bodyObject = getJsonRequestContent(requestBody);
                    writer.jsonValue(bodyObject.toString());
                } else {
                    String rawBodyContent = getRawRequestContent(requestBody);
                    writer.value(rawBodyContent);
                }
            }
            //Remove the header if it is some version of 'authorization'
            //RemoveAll utilizes ignoreCase natively
            headers = headers.newBuilder().removeAll(AUTHORIZATION_HEADER_KEY).build();
            if(headers.size() != 0 || requestBody != null) {
                writer.name(CoreConstants.BatchRequest.HEADERS);
                writer.beginObject();
                for (int i = 0; i < headers.size(); i++) {
                    writer.name(headers.name(i)).value(headers.value(i));
                }
                writer.endObject();
            }
            writer.endObject();
    }
    private JsonObject getJsonRequestContent(RequestBody requestBody) throws IOException {
        try {
            Buffer buffer = new Buffer();
            requestBody.writeTo(buffer);
            return JsonParser.parseString(buffer.readUtf8()).getAsJsonObject();
        } catch(IOException e) {
            throw new IOException(ErrorConstants.Messages.UNABLE_TO_DESERIALIZE_CONTENT, e);
        }
    }
    private String getRawRequestContent(RequestBody requestBody) throws IOException {
        try{
            Buffer buffer = new Buffer();
            requestBody.writeTo(buffer);
            return buffer.readUtf8();
        } catch(IOException e) {
            throw new IOException(ErrorConstants.Messages.UNABLE_TO_DESERIALIZE_CONTENT, e);
        }
    }
    private boolean containsCorrespondingRequestId(List<String> dependsOn) {
        return dependsOn.stream().allMatch(id -> this.batchRequestSteps.containsKey(id));
    }
    private String getRelativeUrl(HttpUrl url) {
        String query = url.encodedQuery(); //Query must be encoded in order for batch requests to work.
        String path = url.encodedPath().substring(5);
        if(Compatibility.isBlank(query)) {
            return path;
        }
        return (path + "?" + query); // `v1.0/` and `beta/` are both 5 characters
    }
}
