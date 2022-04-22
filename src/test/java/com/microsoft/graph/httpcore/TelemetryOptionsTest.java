package com.microsoft.graph.httpcore;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.microsoft.graph.httpcore.middlewareoption.GraphClientOptions;

public class TelemetryOptionsTest {

    @Test
    public void createTelemetryOptionsTest() {
        GraphClientOptions graphClientOptions = new GraphClientOptions();
        assertNotNull(graphClientOptions);
        assertNotNull(graphClientOptions.getClientRequestId());
    }

    @Test
    public void setClientRequestIdTest() {
        GraphClientOptions graphClientOptions = new GraphClientOptions();
        graphClientOptions.setClientRequestId("test id");
        assertTrue(graphClientOptions.getClientRequestId().compareTo("test id")==0);
    }

    @Test
    public void getClientRequestIdTest() {
        GraphClientOptions graphClientOptions = new GraphClientOptions();
        assertNotNull(graphClientOptions.getClientRequestId());
        graphClientOptions.setClientRequestId("test id 1");
        assertTrue(graphClientOptions.getClientRequestId().compareTo("test id 1")==0);
        graphClientOptions.setClientRequestId("test id 2");
        assertTrue(graphClientOptions.getClientRequestId().compareTo("test id 2")==0);
    }

}
