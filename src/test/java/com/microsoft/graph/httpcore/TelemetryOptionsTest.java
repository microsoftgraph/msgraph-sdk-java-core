package com.microsoft.graph.httpcore;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.microsoft.graph.httpcore.middlewareoption.TelemetryHandlerOption;

public class TelemetryOptionsTest {

    @Test
    public void createTelemetryOptionsTest() {
        TelemetryHandlerOption telemetryHandlerOption = new TelemetryHandlerOption();
        assertNotNull(telemetryHandlerOption);
        assertNotNull(telemetryHandlerOption.getClientRequestId());
    }

    @Test
    public void setFeatureUsageTest() {
        TelemetryHandlerOption telemetryHandlerOption = new TelemetryHandlerOption();
        telemetryHandlerOption.setFeatureUsage(TelemetryHandlerOption.AUTH_HANDLER_ENABLED_FLAG);
        telemetryHandlerOption.setFeatureUsage(TelemetryHandlerOption.REDIRECT_HANDLER_ENABLED_FLAG);
        assertTrue(telemetryHandlerOption.getSerializedFeatureUsage().compareTo("5")==0);
    }

    @Test
    public void getSerializedFeatureUsageTest() {
        TelemetryHandlerOption telemetryHandlerOption = new TelemetryHandlerOption();
        telemetryHandlerOption.setFeatureUsage(TelemetryHandlerOption.AUTH_HANDLER_ENABLED_FLAG);
        telemetryHandlerOption.setFeatureUsage(TelemetryHandlerOption.REDIRECT_HANDLER_ENABLED_FLAG);
        telemetryHandlerOption.setFeatureUsage(TelemetryHandlerOption.RETRY_HANDLER_ENABLED_FLAG);
        assertTrue(telemetryHandlerOption.getSerializedFeatureUsage().compareTo("7")==0);
    }

    @Test
    public void setClientRequestIdTest() {
        TelemetryHandlerOption telemetryHandlerOption = new TelemetryHandlerOption();
        telemetryHandlerOption.setClientRequestId("test id");
        assertTrue(telemetryHandlerOption.getClientRequestId().compareTo("test id")==0);
    }

    @Test
    public void getClientRequestIdTest() {
        TelemetryHandlerOption telemetryHandlerOption = new TelemetryHandlerOption();
        assertNotNull(telemetryHandlerOption.getClientRequestId());
        telemetryHandlerOption.setClientRequestId("test id 1");
        assertTrue(telemetryHandlerOption.getClientRequestId().compareTo("test id 1")==0);
        telemetryHandlerOption.setClientRequestId("test id 2");
        assertTrue(telemetryHandlerOption.getClientRequestId().compareTo("test id 2")==0);
    }

}
