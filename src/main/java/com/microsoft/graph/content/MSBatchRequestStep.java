package com.microsoft.graph.content;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;

import javax.annotation.Nullable;
import javax.annotation.Nonnull;

import okhttp3.Request;

/**
 * Respresents a step in a batch request
 */
public class MSBatchRequestStep {
    private String requestId;
    private Request request;
    private HashSet<String> dependsOnIds;

    /**
     * Initializes a batch step from a raw HTTP request
     * @param requestId the id to assign to this step
     * @param request the request to send in the batch
     * @param arrayOfDependsOnIds the ids of steps this step depends on
     */
    public MSBatchRequestStep(@Nonnull final String requestId, @Nonnull final Request request, @Nullable final String... arrayOfDependsOnIds) {
        Objects.requireNonNull(requestId, "Request Id cannot be null.");
        if(requestId.isEmpty())
            throw new IllegalArgumentException("Request Id cannot be empty.");

        this.requestId = requestId;
        this.request = Objects.requireNonNull(request, "Request cannot be null.");
        this.dependsOnIds = new HashSet<>(Arrays.asList(arrayOfDependsOnIds));
    }

    /**
     * Gets the current step ID
     * @return the current step ID
     */
    @Nonnull
    public String getRequestId() {
        return requestId;
    }

    /**
     * Gets the raw HTTP request representation for the step
     * @return the raw HTTP request representation for the step
     */
    @Nonnull
    public Request getRequest() {
        return request;
    }

    /**
     * Gets the list of steps this step depends on
     * @return the list of steps this step depends on
     */
    @Nullable
    public HashSet<String> getDependsOnIds(){
        return dependsOnIds;
    }
}
