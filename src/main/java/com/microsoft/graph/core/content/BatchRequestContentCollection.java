package com.microsoft.graph.core.content;

import com.microsoft.graph.core.CoreConstants;
import com.microsoft.graph.core.ErrorConstants;
import com.microsoft.graph.core.models.BatchRequestStep;
import com.microsoft.graph.core.requests.IBaseClient;
import com.microsoft.kiota.RequestInformation;
import okhttp3.Request;

import jakarta.annotation.Nonnull;
import java.util.*;

/**
 * A collection of batch requests
 */
public class BatchRequestContentCollection {
    private IBaseClient baseClient;
    private List<BatchRequestContent> batchRequests;
    private int batchRequestLimit;
    private BatchRequestContent currentBatchRequest;
    private boolean readOnly = false;
    /**
     * Creates a new batch request collection with the default maximum number of requests.
     * @param baseClient the base client to use for requests.
     */
    public BatchRequestContentCollection(@Nonnull IBaseClient baseClient) {
        this(baseClient, CoreConstants.BatchRequest.MAX_REQUESTS);
    }
    /**
     * Creates a new batch request collection.
     * @param baseClient the base client to use for requests.
     * @param batchRequestLimit the maximum number of requests to batch together.
     */
    public BatchRequestContentCollection(@Nonnull IBaseClient baseClient, int batchRequestLimit) {
        Objects.requireNonNull(baseClient, ErrorConstants.Messages.NULL_PARAMETER + "baseClient");
        if(batchRequestLimit < 2 || batchRequestLimit > CoreConstants.BatchRequest.MAX_REQUESTS) {
            throw new IllegalArgumentException("batchRequestLimit must be between 2 and " + CoreConstants.BatchRequest.MAX_REQUESTS);
        }
        this.baseClient = baseClient;
        this.batchRequestLimit = batchRequestLimit;
        batchRequests = new ArrayList<>();
        currentBatchRequest = new BatchRequestContent(baseClient);
    }
    /**
     * Adds a request to the current BatchRequestContent object of the collection.
     * @param request the request to add.
     * @return the id of the request in the batch.
     */
    @Nonnull
    public String addBatchRequestStep(@Nonnull Request request) {
        setupCurrentRequest();
        return currentBatchRequest.addBatchRequestStep(request);
    }
    /**
     * Adds a request to the current BatchRequestContent object of the collection.
     * @param requestInformation the requestInformation for the request being added.
     * @return the id of the request in the batch.
     */
    @Nonnull
    public String addBatchRequestStep(@Nonnull RequestInformation requestInformation) {
        setupCurrentRequest();
        return currentBatchRequest.addBatchRequestStep(requestInformation);
    }
    /**
     * removes a request from a BatchRequestContent object within the collection.
     * @param requestId the id of the request to remove.
     * @return true if the request was removed, false if it was not found.
     */
    public boolean removeBatchRequestStepWithId(@Nonnull String requestId) {
        validateReadOnly();
        boolean removed = currentBatchRequest.removeBatchRequestStepWithId(requestId);
        if(!removed && !batchRequests.isEmpty()) {
            for (BatchRequestContent batchRequest : batchRequests) {
                removed = batchRequest.removeBatchRequestStepWithId(requestId);
                if(removed) {
                    return true;
                }
            }
        }
        return removed;
    }
    /**
     * Get list of BatchRequestContent objects for execution.
     * @return list of BatchRequestContent objects for execution.
     */
    @Nonnull
    public List<BatchRequestContent> getBatchRequestsForExecution() {
        readOnly = true;
        if(currentBatchRequest.getBatchRequestSteps().size() > 0) {
            batchRequests.add(currentBatchRequest);
        }
        return new ArrayList<>(batchRequests);
    }
    /**
     * Get all BatchRequestSteps from all BatchRequestContent objects within the collection.
     * @return HashMap of BatchRequestSteps from all BatchRequestContent objects within the collection.
     */
    @Nonnull
    public Map<String, BatchRequestStep> getBatchRequestSteps() {
        if (!batchRequests.isEmpty()) {
            Map<String, BatchRequestStep> result = currentBatchRequest.getBatchRequestSteps();
            for (BatchRequestContent batchRequestContent : batchRequests) {
                result.putAll(batchRequestContent.getBatchRequestSteps());
            }
            return result;
        }
        return currentBatchRequest.getBatchRequestSteps();
    }
    /**
     * Get BatchRequestContentCollection with only failed requests.
     * @param responseStatusCodes HashMap of response status codes.
     * @return BatchRequestContentCollection with only failed requests.
     */
    @Nonnull
    public BatchRequestContentCollection newBatchWithFailedRequests(@Nonnull Map<String, Integer> responseStatusCodes) {
        BatchRequestContentCollection newBatch = new BatchRequestContentCollection(this.baseClient, this.batchRequestLimit);
        Map<String, BatchRequestStep> steps = this.getBatchRequestSteps();
        responseStatusCodes.forEach((id, statusCode) -> {
            if(steps.containsKey(id) && !BatchResponseContent.isSuccessStatusCode(statusCode)) {
                newBatch.addBatchRequestStep(steps.get(id).getRequest());
            }
        });
        return newBatch;
    }
    private void validateReadOnly() {
        if(readOnly) {
            throw new UnsupportedOperationException("Batch request collection is already executed");
        }
    }
    private void setupCurrentRequest() {
        validateReadOnly();
        if(currentBatchRequest.getBatchRequestSteps().size() >= batchRequestLimit) {
            batchRequests.add(currentBatchRequest);
            currentBatchRequest = new BatchRequestContent(baseClient);
        }
    }
}
