package com.microsoft.graph.core.requests.options;

import java.util.UUID;

import com.microsoft.graph.core.requests.FeatureTracker;
import com.microsoft.kiota.Compatibility;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import com.microsoft.graph.core.CoreConstants;
import com.microsoft.kiota.RequestOption;

/**
 * Options to be passed to the telemetry middleware.
 */
public class GraphClientOption implements RequestOption {

    private String clientRequestId;
    private String clientLibraryVersion;
    private String coreLibraryVersion;
    private String graphServiceTargetVersion;
    /**
     * Default constructor
     */
    public GraphClientOption() {
        //Default constructor
    }

    /**
     * Feature Tracker instance
     */
    public final FeatureTracker featureTracker = new FeatureTracker();
    /**
     * Sets the client request id
     * @param clientRequestId the client request id to set, preferably the string representation of a GUID
     */
    public void setClientRequestId(@Nonnull final String clientRequestId) {
        if(Compatibility.isBlank(clientRequestId)) {
            throw new IllegalArgumentException("clientRequestId cannot be null or empty");
        }
        this.clientRequestId = clientRequestId;
    }
    /**
     * Gets the client request id
     * @return the client request id
     */
    @Nonnull
    public String getClientRequestId() {
        if(clientRequestId == null) {
            clientRequestId = UUID.randomUUID().toString();
        }
        return clientRequestId;
    }
    /**
     * Sets a string representation of the client library
     * @param clientLibraryVersion client library version specified by user.
     */
    public void setClientLibraryVersion(@Nonnull final String clientLibraryVersion) {
        if(Compatibility.isBlank(clientLibraryVersion))
        {
            throw new IllegalArgumentException("clientLibraryVersion cannot be null or empty");
        }
        this.clientLibraryVersion = clientLibraryVersion;
    }
    /**
     * Get the client library version as a string
     * If null return null;
     * @return client library version.
     */
    @Nullable
    public String getClientLibraryVersion() {
        return this.clientLibraryVersion == null ? null : this.clientLibraryVersion;
    }
    /**
     * Set the core library version as a String, in this format 'x.x.x'
     * @param coreLibraryVersion core library version specified by user.
     */
    public void setCoreLibraryVersion(@Nonnull final String coreLibraryVersion) {
        if(Compatibility.isBlank(coreLibraryVersion))
        {
            throw new IllegalArgumentException("coreLibraryVersion cannot be null or empty");
        }
        this.coreLibraryVersion = coreLibraryVersion;
    }
    /**
     * Get the core library version as a String, in this format 'x.x.x'
     * If null return the value in CoreConstants.
     * @return core library version.
     */
    @Nonnull
    public String getCoreLibraryVersion() {
        return this.coreLibraryVersion == null ? CoreConstants.Headers.VERSION : this.coreLibraryVersion;
    }
    /**
     * Set the target version of the api endpoint we are targeting (v1 or beta)
     * @param graphServiceVersion the version of the Api endpoint we are targeting
     */
    public void setGraphServiceTargetVersion(@Nonnull final String graphServiceVersion) {
        if(Compatibility.isBlank(graphServiceVersion))
        {
            throw new IllegalArgumentException("graphServiceVersion cannot be null or empty");
        }
        this.graphServiceTargetVersion = graphServiceVersion;
    }
    /**
     * Get the target version of the api endpoint we are targeting (v1 or beta)
     * return 'v1' if not specified.
     * @return the version of the Api endpoint we are targeting.
     */
    @Nonnull
    public String getGraphServiceTargetVersion() {
        return this.graphServiceTargetVersion == null ? "v1.0" : this.graphServiceTargetVersion;
    }

    @Override
    @Nonnull
    public <T extends RequestOption> Class<T> getType() {
        return (Class<T>) GraphClientOption.class;
    }
}
