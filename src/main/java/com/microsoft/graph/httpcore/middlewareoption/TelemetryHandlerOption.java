package com.microsoft.graph.httpcore.middlewareoption;

import java.util.Objects;
import java.util.UUID;

import javax.annotation.Nonnull;

import com.microsoft.graph.httpcore.FeatureFlag;

/**
 * Options to be passed to the telemetry middleware.
 */
public class TelemetryHandlerOption {

    private int featureUsage = FeatureFlag.NONE_FLAG;
    private String clientRequestId;

    /**
     * Sets a numeric representation of the SDK feature usage
     * @param flag a numeric representation of the SDK feature usage
     */
    public void setFeatureUsage(int flag) {
        featureUsage = featureUsage | flag;
    }

    /**
     * Gets a numeric representation of the SDK feature usage
     * @return a numeric representation of the SDK feature usage
     */
    public int getFeatureUsage() {
        return featureUsage;
    }

    /**
     * Gets a serialized representation of the SDK feature usage.
     * @return a serialized representation of the SDK feature usage
     */
    @Nonnull
    public String getSerializedFeatureUsage() {
        return Integer.toHexString(featureUsage);
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
