package com.microsoft.graph.Requests;

import com.microsoft.graph.Requests.FeatureFlag;

import javax.annotation.Nonnull;

/**
 * Manages and tracks the flags for tasks and handlers.
 */
public class FeatureTracker {

    /**
     * Instantiate a Feature Tracker
     */
    public FeatureTracker() {};

    private int featureUsage = FeatureFlag.NONE_FLAG;
    /**
     * Sets a numeric representation of the SDK feature usage
     * @param flags a numeric representation of the SDK feature usage
     */
    public void setFeatureUsage(int... flags) {
        for(int flag : flags){
            featureUsage = featureUsage | flag;
        }
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
