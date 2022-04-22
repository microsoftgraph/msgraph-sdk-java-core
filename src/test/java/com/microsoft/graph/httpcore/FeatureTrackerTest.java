package com.microsoft.graph.httpcore;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class FeatureTrackerTest {
    @Test
    public void setFeatureUsageTest() {
        FeatureTracker featureTracker = new FeatureTracker();
        featureTracker.setFeatureUsage(FeatureFlag.AUTH_HANDLER_FLAG);
        featureTracker.setFeatureUsage(FeatureFlag.REDIRECT_HANDLER_FLAG);
        assertTrue(featureTracker.getSerializedFeatureUsage().compareTo("5")==0);
    }

    @Test
    public void getSerializedFeatureUsageTest() {
        FeatureTracker featureTracker = new FeatureTracker();
        featureTracker.setFeatureUsage(FeatureFlag.AUTH_HANDLER_FLAG);
        featureTracker.setFeatureUsage(FeatureFlag.REDIRECT_HANDLER_FLAG);
        featureTracker.setFeatureUsage(FeatureFlag.RETRY_HANDLER_FLAG);
        assertTrue(featureTracker.getSerializedFeatureUsage().compareTo("7")==0);
    }
}
