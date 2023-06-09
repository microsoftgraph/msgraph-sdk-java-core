package com.microsoft.graph.content;

import com.microsoft.kiota.ResponseHandler;
import com.microsoft.kiota.serialization.Parsable;
import com.microsoft.kiota.serialization.ParsableFactory;
import okhttp3.Response;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * A collection of BatchResponseContent objects.
 */
public class BatchResponseContentCollection {

    private List<KeyedBatchResponseContent> batchResponses;

    /**
     * Instantiates a new Batch response content collection.
     */
    public BatchResponseContentCollection() {
        batchResponses = new ArrayList<>();
    }
    /**
     * Add BatchResponseContent object to the collection.
     * @param keys the keys of the requests that were batched together.
     * @param content the BatchResponseContent object to add to the collection.
     */
    public void addBatchResponse(@Nonnull Collection<String> keys, @Nonnull BatchResponseContent content) {
        batchResponses.add(new KeyedBatchResponseContent(new HashSet<>(keys), content));
    }
    /**
     * Gets the BatchResponseContent object containing the response for the request with the given id.
     * @param requestId the id of the request to get the response for.
     * @return the BatchResponseContent object containing the response for the request with the given id, null if no response was found.
     */
    private BatchResponseContent getBatchResponseContaining(@Nonnull String requestId) {
        for(KeyedBatchResponseContent keyedResponse : batchResponses) {
            if(keyedResponse.keys.contains(requestId)) {
                return keyedResponse.response;
            }
        }
        return null;
    }
    /**
     * Gets the response for the request with the given id.
     * @param requestId the id of the request to get the response for.
     * @return the response for the request with the given id, null if no response was found.
     */
    @Nullable
    public CompletableFuture<Response> getResponseByIdAsync(@Nonnull String requestId) {
        BatchResponseContent response = getBatchResponseContaining(requestId);
        return response == null ? CompletableFuture.completedFuture(null) : response.getResponseById(requestId);
    }
    /**
     * Gets the response for the request with the given id.
     * @param requestId the id of the request to get the response for.
     * @param handler the handler to use when deserializing the response.
     * @return the response for the request with the given id, null if no response was found.
     * @param <T> the type of the response.
     */
    @Nullable
    public <T extends Parsable> CompletableFuture<T> getResponseByIdAsync(@Nonnull String requestId, @Nonnull ResponseHandler handler) {
        BatchResponseContent response = getBatchResponseContaining(requestId);
        return response == null ? CompletableFuture.completedFuture(null) : response.getResponseById(requestId, handler);
    }
    /**
     * Gets the response for the request with the given id.
     * @param requestId the id of the request to get the response for.
     * @param factory the factory to use when deserializing the response.
     * @return the response for the request with the given id, null if no response was found.
     * @param <T> the type of the response.
     */
    @Nullable
    public <T extends Parsable> CompletableFuture<T> getResponseByIdAsync(@Nonnull String requestId, @Nonnull ParsableFactory<T> factory) {
        BatchResponseContent response = getBatchResponseContaining(requestId);
        return response == null ? CompletableFuture.completedFuture(null) : response.getResponseById(requestId, factory);
    }
    /**
     * Gets the response for the request with the given id as a stream.
     * @param requestId the id of the request to get the response for.
     * @return the response for the request with the given id, null if no response was found.
     */
    @Nullable
    public CompletableFuture<InputStream> getResponseStreamByIdAsync(@Nonnull String requestId) {
        BatchResponseContent response = getBatchResponseContaining(requestId);
        return response == null ? CompletableFuture.completedFuture(null) : response.getResponseStreamById(requestId);
    }
    /**
     * Gets the response codes for all the requests in the batch.
     * @return the response codes for all the requests in the batch.
     */
    @Nonnull
    public CompletableFuture<HashMap<String, Integer>> getResponsesStatusCodesAsync() {
        HashMap<String, Integer> statusCodes = new HashMap<>();
        for(KeyedBatchResponseContent keyedResponse : batchResponses) {
            HashMap<String, Integer> responseStatusCodes = keyedResponse.response.getResponsesStatusCode().join();
            statusCodes.putAll(responseStatusCodes);
        }
        return CompletableFuture.completedFuture(statusCodes);
    }
}
