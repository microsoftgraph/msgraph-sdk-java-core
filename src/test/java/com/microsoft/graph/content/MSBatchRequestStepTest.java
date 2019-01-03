package com.microsoft.graph.content;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpRequest;
import org.apache.http.client.methods.HttpGet;
import org.junit.Test;

public class MSBatchRequestStepTest {

	@Test
	public void testMSBatchRequestStepCreation() {
		HttpRequest request = new HttpGet("http://graph.microsoft.com");
		List<String> arrayOfDependsOnIds = new ArrayList();
		MSBatchRequestStep requestStep = new MSBatchRequestStep("1", request, arrayOfDependsOnIds);
		assertTrue("Test BatchRequestStep creation", requestStep != null);
		assertTrue("Test Request id", requestStep.getRequestId().compareTo("1") == 0);
		assertTrue("Test Request object", requestStep.getRequest() == request);
		assertTrue("Test Array of depends on Ids", requestStep.getArrayOfDependsOnIds() != null);
	}

}
