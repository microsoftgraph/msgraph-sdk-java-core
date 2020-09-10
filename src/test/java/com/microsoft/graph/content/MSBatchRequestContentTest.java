package com.microsoft.graph.content;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import okhttp3.Request;

public class MSBatchRequestContentTest {

	String testurl = "http://graph.microsoft.com/me";

	@Test
	public void testMSBatchRequestContentCreation() {
		List<MSBatchRequestStep> requestStepArray = new ArrayList<>();
		for(int i=0;i<5;i++) {
			Request request = new Request.Builder().url(testurl).build();
			List<String> arrayOfDependsOnIds = new ArrayList<>();
			MSBatchRequestStep requestStep = new MSBatchRequestStep("" + i, request, arrayOfDependsOnIds);
			requestStepArray.add(requestStep);
		}
		MSBatchRequestContent requestContent = new MSBatchRequestContent(requestStepArray);
		assertTrue(requestContent.getBatchRequestContent() != null);
	}

	@Test
	public void testGetBatchRequestContent() {
		Request request = new Request.Builder().url(testurl).build();
		List<String> arrayOfDependsOnIds = new ArrayList<>();
		MSBatchRequestStep requestStep = new MSBatchRequestStep("1", request, arrayOfDependsOnIds);
		MSBatchRequestContent requestContent = new MSBatchRequestContent();
		requestContent.addBatchRequestStep(requestStep);
		String content = requestContent.getBatchRequestContent();
		String expectedContent = "{\"requests\":[{\"id\":\"1\",\"url\":\"http://graph.microsoft.com/me\",\"method\":\"GET\",\"dependsOn\":[]}]}";
		assertTrue(content.compareTo(expectedContent) == 0);
	}

	@Test
	public void testGetBatchRequestContentWithHeader() {
		Request request = new Request.Builder().url(testurl).header("testkey", "testvalue").build();
		List<String> arrayOfDependsOnIds = new ArrayList<>();
		MSBatchRequestStep requestStep = new MSBatchRequestStep("1", request, arrayOfDependsOnIds);
		MSBatchRequestContent requestContent = new MSBatchRequestContent();
		requestContent.addBatchRequestStep(requestStep);
		String content = requestContent.getBatchRequestContent();
		System.out.println(content);
		String expectedContent = "{\"requests\":[{\"id\":\"1\",\"url\":\"http://graph.microsoft.com/me\",\"method\":\"GET\",\"headers\":{\"testkey\":\"testvalue\"},\"dependsOn\":[]}]}";
		assertTrue(content.compareTo(expectedContent) == 0);
	}

	@Test
	public void testRemoveBatchRequesStepWithId() {
		Request request = new Request.Builder().url(testurl).build();
		List<String> arrayOfDependsOnIds = new ArrayList<>();
		MSBatchRequestStep requestStep = new MSBatchRequestStep("1", request, arrayOfDependsOnIds);
		MSBatchRequestContent requestContent = new MSBatchRequestContent();
		requestContent.addBatchRequestStep(requestStep);
		requestContent.removeBatchRequestStepWithId("1");
		String content = requestContent.getBatchRequestContent();
		String expectedContent = "{\"requests\":[]}";
		assertTrue(content.compareTo(expectedContent) == 0);
	}

	@Test
	public void testRemoveBatchRequesStepWithIdByAddingMultipleBatchSteps() {
		Request request = new Request.Builder().url(testurl).build();
		List<String> arrayOfDependsOnIds = new ArrayList<>();
		MSBatchRequestStep requestStep = new MSBatchRequestStep("1", request, arrayOfDependsOnIds);

		Request request1 = new Request.Builder().url(testurl).build();
		List<String> arrayOfDependsOnIds1 = new ArrayList<>();
		arrayOfDependsOnIds1.add("1");
		MSBatchRequestStep requestStep1 = new MSBatchRequestStep("2", request1, arrayOfDependsOnIds1);

		MSBatchRequestContent requestContent = new MSBatchRequestContent();
		requestContent.addBatchRequestStep(requestStep);
		requestContent.addBatchRequestStep(requestStep1);

		requestContent.removeBatchRequestStepWithId("1");
		String content = requestContent.getBatchRequestContent();
		String expectedContent = "{\"requests\":[{\"id\":\"2\",\"url\":\"http://graph.microsoft.com/me\",\"method\":\"GET\",\"dependsOn\":[]}]}";
		assertTrue(content.compareTo(expectedContent) == 0);
	}

}
