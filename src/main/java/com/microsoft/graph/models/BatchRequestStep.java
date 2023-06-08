package com.microsoft.graph.models;

import com.microsoft.graph.exceptions.ErrorConstants;
import okhttp3.Request;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Represents a single request in a batch request
 */
public class BatchRequestStep {

    private String requestId;
    private Request request;
    @Nullable
    private ArrayList<String> dependsOn;
    /**
     * Creates a new BatchRequestStep
     * @param requestId The id of the request
     * @param request The request
     */
    public BatchRequestStep(@Nonnull String requestId, @Nonnull Request request) {
        Objects.requireNonNull(requestId, String.format(Locale.US, ErrorConstants.Messages.NULL_PARAMETER, "requestId"));
        Objects.requireNonNull(request, String.format(Locale.US, ErrorConstants.Messages.NULL_PARAMETER, "request"));
        this.requestId = requestId;
        this.request = request;
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
    @Nullable
    public ArrayList<String> getDependsOn() {
        if(dependsOn == null) {
            return null;
        }
        return new ArrayList<>(dependsOn);
    }
}
