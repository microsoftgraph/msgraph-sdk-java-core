package com.microsoft.graph.requests;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FeatureTrackerTest {
    @Test
    void setFeatureUsageTest() {
        FeatureTracker featureTracker = new FeatureTracker();
        featureTracker.setFeatureUsage(FeatureFlag.AUTH_HANDLER_FLAG);
        featureTracker.setFeatureUsage(FeatureFlag.REDIRECT_HANDLER_FLAG);
        assertEquals("5", featureTracker.getSerializedFeatureUsage());
    }
    @Test
    void getSerializedFeatureUsageTest() {
        FeatureTracker featureTracker = new FeatureTracker();
        featureTracker.setFeatureUsage(FeatureFlag.AUTH_HANDLER_FLAG);
        featureTracker.setFeatureUsage(FeatureFlag.REDIRECT_HANDLER_FLAG);
        featureTracker.setFeatureUsage(FeatureFlag.RETRY_HANDLER_FLAG);
        assertEquals("7", featureTracker.getSerializedFeatureUsage());
    }
}
