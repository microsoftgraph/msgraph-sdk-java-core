package com.microsoft.graph.core.models;

import com.microsoft.graph.core.CoreConstants;
import com.microsoft.graph.core.ErrorConstants;
import com.microsoft.kiota.Compatibility;
import com.microsoft.kiota.http.middleware.UrlReplaceHandler;
import okhttp3.Request;

import jakarta.annotation.Nonnull;
import java.util.*;

/**
 * Represents a single request in a batch request
 */
public class BatchRequestStep {

    private final String requestId;
    private final Request request;
    private List<String> dependsOn;
    /**
     * Creates a new BatchRequestStep
     * @param requestId The id of the request
     * @param request The request
     */
    public BatchRequestStep(@Nonnull String requestId, @Nonnull Request request) {
        Objects.requireNonNull(request, ErrorConstants.Messages.NULL_PARAMETER + "request");
        if(Compatibility.isBlank(requestId)) {
            throw new IllegalArgumentException("requestId cannot be null or empty.");
        }
        this.requestId = requestId;
        this.request = UrlReplaceHandler.replaceRequestUrl(request, CoreConstants.ReplacementConstants.getDefaultReplacementPairs());
    }
    /**
     * Creates a new BatchRequestStep
     * @param requestId The id of the request
     * @param request The request
     * @param dependsOn The ids of the requests that this request depends on
     */
    public BatchRequestStep(@Nonnull String requestId, @Nonnull Request request, @Nonnull List<String> dependsOn) {
        this(requestId, request);
        this.dependsOn = new ArrayList<>(dependsOn);
    }
    /**
     * Gets the request
     * @return The request
     */
    @Nonnull
    public Request getRequest() {
        return this.request;
    }
    /**
     * Gets the id of the request
     * @return The id of the request
     */
    @Nonnull
    public String getRequestId() {
        return this.requestId;
    }
    /**
     * Gets the ids of the requests that this request depends on
     * @return The ids of the requests that this request depends on
     */
    @Nonnull
    public List<String> getDependsOn() {
        if(dependsOn == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(dependsOn);
    }
    /**
     * Sets the ids of the requests that this request depends on
     * @param dependsOn The ids of the requests that this request depends on
     */
    public void setDependsOn(@Nonnull List<String> dependsOn) {
        this.dependsOn = new ArrayList<>(dependsOn);
    }
    /**
     * Adds a request id to the dependsOn list.
     * @param id The id of the request to add to the dependsOn list.
     */
    public void addDependsOnId(@Nonnull String id) {
        if(Compatibility.isBlank(id)) {
            throw new IllegalArgumentException("id cannot be null or empty");
        }
        if(dependsOn == null) {
            dependsOn = new ArrayList<>();
        }
        dependsOn.add(id);
    }
    /**
     * Removes a request id from the dependsOn list.
     *
     * @param id The id of the request to remove.
     * @return true if the request id is no longer present in the dependsOn collection, false if dependsOn is null.
     */
    public boolean removeDependsOnId(@Nonnull String id) {
        Objects.requireNonNull(id);
        if(dependsOn != null) {
            if(!dependsOn.contains(id) || id.isEmpty()) {
                throw new IllegalArgumentException("id is not present in the dependsOn collection or is empty");
            }
            dependsOn.removeAll(Collections.singleton(id));
            return true;
        }
        return false;
    }
}
