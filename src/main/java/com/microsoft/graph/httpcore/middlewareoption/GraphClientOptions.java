package com.microsoft.graph.httpcore.middlewareoption;

import java.util.Objects;
import java.util.UUID;

import javax.annotation.Nonnull;

import com.microsoft.graph.CoreConstants;

/**
 * Options to be passed to the telemetry middleware.
 */
public class GraphClientOptions implements IMiddlewareControl{

    private String clientRequestId;
    private String coreLibraryVersion;
    private String clientLibraryVersion;

    /**
     * Set the core library version as a String, in this format 'x.x.x'
     * @param version core library version specified by user.
     */
    public void setCoreLibraryVersion(@Nonnull final String version){
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
}
