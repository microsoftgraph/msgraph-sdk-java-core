package com.microsoft.graph.content;

import com.google.gson.*;
import com.google.gson.stream.JsonWriter;
import com.microsoft.graph.CoreConstants;
import com.microsoft.graph.exceptions.ClientException;
import com.microsoft.graph.exceptions.ErrorConstants;
import com.microsoft.graph.models.BatchRequestStep;
import com.microsoft.graph.requests.IBaseClient;
import com.microsoft.kiota.RequestAdapter;
import com.microsoft.kiota.RequestInformation;

import okhttp3.*;
import okio.Buffer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * A class representing the content of a batch request.
 */
public class BatchRequestContent {
    private HashMap<String, BatchRequestStep> batchRequestSteps;
    private RequestAdapter requestAdapter;
    private static final String CONTENT_TYPE_HEADER = "Content-Type";

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
        this.requestAdapter = Objects.requireNonNull(requestAdapter, String.format(Locale.US, ErrorConstants.Messages.NULL_PARAMETER, "requestAdapter"));

        Objects.requireNonNull(batchRequestSteps, String.format(Locale.US, ErrorConstants.Messages.NULL_PARAMETER, "batchRequestSteps"));
        if(batchRequestSteps.size() >= CoreConstants.BatchRequest.MAX_REQUESTS) {
            throw new IllegalArgumentException(String.format(Locale.US,ErrorConstants.Messages.MAXIMUM_VALUE_EXCEEDED, "Number of request steps", CoreConstants.BatchRequest.MAX_REQUESTS));
        }

        this.batchRequestSteps = new HashMap<>();
        for (BatchRequestStep requestStep : batchRequestSteps) {
            if(requestStep.getDependsOn() != null && !ContainsCorrespondingRequestId(requestStep.getDependsOn())) {
                throw new IllegalArgumentException(ErrorConstants.Messages.INVALID_DEPENDS_ON_REQUEST_ID);
            }
            addBatchRequestStep(requestStep);
        }
    }
    /**
     * Gets the batch request steps.
     * @return The batch request steps.
     */
    @Nonnull
    public HashMap<String, BatchRequestStep> getBatchRequestSteps() {
        return batchRequestSteps;
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
        this.batchRequestSteps.put(requestStep.getRequestId(), requestStep);
        return true;
    }
    /**
     * Adds a batch request step to the batch request.
     * @param request The request to add.
     * @return The request id of the added request.
     */
    public String addBatchRequestStep(@Nonnull Request request) {
        if(this.batchRequestSteps.size() >= CoreConstants.BatchRequest.MAX_REQUESTS) {
            throw new IllegalArgumentException(String.format(Locale.US,ErrorConstants.Messages.MAXIMUM_VALUE_EXCEEDED, "Number of request steps", CoreConstants.BatchRequest.MAX_REQUESTS));
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
    public CompletableFuture<String> addBatchRequestAsync(@Nonnull RequestInformation requestInformation) {
        if(this.batchRequestSteps.size() >= CoreConstants.BatchRequest.MAX_REQUESTS) {
            throw new IllegalArgumentException(String.format(Locale.US,ErrorConstants.Messages.MAXIMUM_VALUE_EXCEEDED, "Number of request steps", CoreConstants.BatchRequest.MAX_REQUESTS));
        }
        String requestId = java.util.UUID.randomUUID().toString();
        return this.requestAdapter.convertToNativeRequestAsync(requestInformation).thenCompose(request -> {
            BatchRequestStep requestStep = new BatchRequestStep(requestId, (Request) request);
            this.batchRequestSteps.put(requestId, requestStep);
            return CompletableFuture.completedFuture(requestId);
        });
    }
    /**
     * Removes a batch request step from the batch request.
     * @param requestId The request id of the request to remove.
     * @return True if the request was removed, false otherwise.
     */
    public boolean removeBatchRequestStepWithId(String requestId) {
        if(requestId == null || requestId.isEmpty()) {
            throw new IllegalArgumentException(String.format(Locale.US, ErrorConstants.Messages.NULL_PARAMETER, "requestId"));
        }
        boolean isRemoved = false;
        if(this.batchRequestSteps.containsKey(requestId)) {
            this.batchRequestSteps.remove(requestId);
            isRemoved = true;
            for (BatchRequestStep requestStep : this.batchRequestSteps.values()) {
                if(requestStep != null && requestStep.getDependsOn() != null) {
                    while(true) {
                        if(!requestStep.getDependsOn().remove(requestId))
                            break;
                    }
                }
            }
        }
        return isRemoved;
    }
    /**
     * Builds a BatchRequestContent object from failed requests.
     * @param responseStatusCodes The response status codes of the failed requests.
     * @return The BatchRequestContent object.
     */
    public BatchRequestContent newBatchWithFailedRequests (@Nonnull HashMap<String, Integer> responseStatusCodes) {
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
     */
    @Nonnull
    public CompletableFuture<InputStream> getBatchRequestContentAsync() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        JsonWriter writer = new JsonWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
        try {
            writer.setIndent("  ");
            writer.beginObject();
            writer.name(CoreConstants.BatchRequest.REQUESTS);
            writer.beginArray();
            for (BatchRequestStep requestStep : this.batchRequestSteps.values()) {
                writeBatchRequestStepAsync(requestStep, writer);
            }
            writer.endArray();
            writer.endObject();
            writer.flush();

            ByteArrayInputStream stream = new ByteArrayInputStream(outputStream.toByteArray());
            return CompletableFuture.completedFuture(stream);
        } catch(IOException e) {
            CompletableFuture<InputStream> exception = new CompletableFuture<>();
            exception.completeExceptionally(e);
            return exception;
        }
    }
    private void writeBatchRequestStepAsync(BatchRequestStep requestStep, JsonWriter writer) {
        try {
            Request request = requestStep.getRequest();
            writer.beginObject();
            writer.name(CoreConstants.BatchRequest.ID).value(requestStep.getRequestId());
            writer.name(CoreConstants.BatchRequest.URL).value(getRelativeUrl(request.url()));
            writer.name(CoreConstants.BatchRequest.METHOD).value(request.method());

            List<String> dependsOn = requestStep.getDependsOn();
            Headers headers = request.headers();
            if(dependsOn != null && !dependsOn.isEmpty()) {
                writer.name(CoreConstants.BatchRequest.DEPENDS_ON);
                writer.beginArray();
                for(String id : dependsOn) {
                    writer.value(id);
                }
                writer.endArray();
            }
            RequestBody requestBody = request.body();
            if(headers.size() != 0 || requestBody != null) {
                writer.name(CoreConstants.BatchRequest.HEADERS);
                writer.beginObject();
                for(int i = 0; i < headers.size(); i++) {
                    writer.name(headers.name(i)).value(headers.value(i));
                }
                if (requestBody != null) {
                    writer.name("Content-Type").value(Objects.requireNonNull(requestBody.contentType()).toString());
                    writer.endObject();
                } else {
                    writer.endObject();
                }
            }
            if(requestBody != null) {
                JsonObject bodyObject = getRequestContentAsync(requestBody).get();
                writer.name(CoreConstants.BatchRequest.BODY);
                writeJsonElement(writer, bodyObject);
            }
            writer.endObject();
        } catch (IOException | InterruptedException | ExecutionException e) {
            CompletableFuture<Void> exception = new CompletableFuture<>();
            exception.completeExceptionally(e);
        }
    }
    private CompletableFuture<JsonObject> getRequestContentAsync(RequestBody requestBody) {
        Objects.requireNonNull(String.format(Locale.US, ErrorConstants.Messages.NULL_PARAMETER, "requestBody"));
        try {
            Buffer buffer = new Buffer();
            requestBody.writeTo(buffer);
            JsonObject jsonObject = JsonParser.parseString(buffer.readUtf8()).getAsJsonObject();
            return CompletableFuture.completedFuture(jsonObject);
        } catch(IOException e) {
            ClientException clientException = new ClientException(ErrorConstants.Messages.UNABLE_TO_DESERIALIZE_CONTENT, e);
            CompletableFuture<JsonObject> exception = new CompletableFuture<>();
            exception.completeExceptionally(clientException);
            return exception;
        }
    }
    //Used to solve indentation issue when there are several nested jsonElements.
    private void writeJsonElement(JsonWriter writer, JsonElement element) throws IOException {
        if(element.isJsonPrimitive()) {
            JsonPrimitive primitive = element.getAsJsonPrimitive();
            if(primitive.isString()) {
                writer.value(primitive.getAsString());
            } else if(primitive.isBoolean()) {
                writer.value(primitive.getAsBoolean());
            } else if(primitive.isNumber()) {
                writer.value(primitive.getAsNumber());
            }
        } else if(element.isJsonArray()) {
            writer.beginArray();
            for(JsonElement arrayElement : element.getAsJsonArray()) {
                writeJsonElement(writer, arrayElement);
            }
            writer.endArray();
        } else if(element.isJsonObject()) {
            writer.beginObject();
            for(Map.Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet()) {
                writer.name(entry.getKey());
                writeJsonElement(writer, entry.getValue());
            }
            writer.endObject();
        }
    }
    private boolean ContainsCorrespondingRequestId(List<String> dependsOn) {
        return dependsOn.stream().allMatch(id -> this.batchRequestSteps.containsKey(id));
    }
    private String getRelativeUrl(HttpUrl url) {
        String query = url.encodedQuery(); //Query must be encoded in order for batch requests to work.
        String path = url.encodedPath();
        if(query == null || query.isEmpty()) {
            return path.substring(5);
        }
        return String.format(Locale.US, "%s?%s", path, query).substring(5); // `v1.0/` and `beta/` are both 5 characters
    }
}
