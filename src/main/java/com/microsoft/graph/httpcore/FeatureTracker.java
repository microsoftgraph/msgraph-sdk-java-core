package com.microsoft.graph.httpcore;

import javax.annotation.Nonnull;

/**
 * Manages and tracks the flags for tasks and handlers.
 */
public class FeatureTracker {

    private int featureUsage = FeatureFlag.NONE_FLAG;
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

    // TODO: add a method to add a feature flag to the header, do this once implementation on how to address tasks and their flags is decided
}
