package com.microsoft.graph.httpcore;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.microsoft.graph.httpcore.middlewareoption.TelemetryOptions;

public class TelemetryOptionsTest {
	
	@Test
	public void createTelemetryOptionsTest() {
		TelemetryOptions telemetryOptions = new TelemetryOptions();
		assertNotNull(telemetryOptions);
		assertNotNull(telemetryOptions.getClientRequestId());
	}
	
	@Test
	public void setFeatureUsageTest() {
		TelemetryOptions telemetryOptions = new TelemetryOptions();
		telemetryOptions.setFeatureUsage(TelemetryOptions.AUTH_HANDLER_ENABLED_FLAG);
		telemetryOptions.setFeatureUsage(TelemetryOptions.REDIRECT_HANDLER_ENABLED_FLAG);
		assertTrue(telemetryOptions.getSerializedFeatureUsage().compareTo("5")==0);
	}

	@Test
	public void getSerializedFeatureUsageTest() {
		TelemetryOptions telemetryOptions = new TelemetryOptions();
		telemetryOptions.setFeatureUsage(TelemetryOptions.AUTH_HANDLER_ENABLED_FLAG);
		telemetryOptions.setFeatureUsage(TelemetryOptions.REDIRECT_HANDLER_ENABLED_FLAG);
		telemetryOptions.setFeatureUsage(TelemetryOptions.RETRY_HANDLER_ENABLED_FLAG);
		assertTrue(telemetryOptions.getSerializedFeatureUsage().compareTo("7")==0);
	}

	@Test
	public void setClientRequestIdTest() {
		TelemetryOptions telemetryOptions = new TelemetryOptions();
		telemetryOptions.setClientRequestId("test id");
		assertTrue(telemetryOptions.getClientRequestId().compareTo("test id")==0);
	}

	@Test
	public void getClientRequestIdTest() {
		TelemetryOptions telemetryOptions = new TelemetryOptions();
		assertNotNull(telemetryOptions.getClientRequestId());
		telemetryOptions.setClientRequestId("test id 1");
		assertTrue(telemetryOptions.getClientRequestId().compareTo("test id 1")==0);
		telemetryOptions.setClientRequestId("test id 2");
		assertTrue(telemetryOptions.getClientRequestId().compareTo("test id 2")==0);
	}

}
