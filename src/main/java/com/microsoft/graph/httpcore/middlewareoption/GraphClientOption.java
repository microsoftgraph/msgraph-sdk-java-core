package com.microsoft.graph.httpcore.middlewareoption;

import java.util.Objects;
import java.util.UUID;

import javax.annotation.Nonnull;

import com.microsoft.graph.CoreConstants;
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
     * Sets the client request id
     * @param clientRequestId the client request id to set, preferably the string representation of a GUID
     */
    public void setClientRequestId(@Nonnull final String clientRequestId) {
        this.clientRequestId = Objects.requireNonNull(clientRequestId, "parameter clientRequestId cannot be null");
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
     * @param version client library version specified by user.
     */
    public void setClientLibraryVersion(@Nonnull final String version) {
        this.clientLibraryVersion = Objects.requireNonNull(version, "parameter version cannot be null");
    }
    /**
     * Get the client library version as a string
     * If null return null;
     * @return client library version.
     */
    public String getClientLibraryVersion() {
        return this.clientLibraryVersion == null ? null : this.clientLibraryVersion;
    }
    /**
     * Set the core library version as a String, in this format 'x.x.x'
     * @param version core library version specified by user.
     */
    public void setCoreLibraryVersion(@Nonnull final String version) {
        this.coreLibraryVersion = Objects.requireNonNull(version, "parameter version cannot be null");
    }
    /**
     * Get the core library version as a String, in this format 'x.x.x'
     * If null return the value in CoreConstants.
     * @return core library version.
     */
    public String getCoreLibraryVersion() {
        return this.coreLibraryVersion == null ? CoreConstants.Headers.VERSION : this.coreLibraryVersion;
    }
    /**
     * Set the target version of the api endpoint we are targeting (v1 or beta)
     * @param version the version of the Api endpoint we are targeting
     */
    public void setGraphServiceTargetVersion(@Nonnull final String version) {
        this.graphServiceTargetVersion = Objects.requireNonNull(version, "parameter version cannot be null");
    }
    /**
     * Get the target version of the api endpoint we are targeting (v1 or beta)
     * return 'v1' if not specified.
     * @return the version of the Api endpoint we are targeting.
     */
    public String getGraphServiceTargetVersion() {
        return this.graphServiceTargetVersion == null ? "v1" : this.graphServiceTargetVersion;
    }

    @Override
    public <T extends RequestOption> Class<T> getType() {
        return (Class<T>) GraphClientOption.class;
    }
}
