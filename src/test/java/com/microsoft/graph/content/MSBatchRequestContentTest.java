package com.microsoft.graph.content;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpRequest;
import org.apache.http.client.methods.HttpGet;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class MSBatchRequestContentTest {

	String testurl = "http://graph.microsoft.com";
	
	@Test
	public void testMSBatchRequestContentCreation() {
		List<MSBatchRequestStep> requestStepArray = new ArrayList<>();
		 for(int i=0;i<5;i++) {
			 HttpRequest request = new HttpGet(testurl);
			 List<String> arrayOfDependsOnIds = new ArrayList<>();
			 MSBatchRequestStep requestStep = new MSBatchRequestStep("" + i, request, arrayOfDependsOnIds);
		     requestStepArray.add(requestStep);
		 }
		 MSBatchRequestContent requestContent = new MSBatchRequestContent(requestStepArray);
		 assertTrue(requestContent.getBatchRequestContent() != null);
	}
	
	@Test
	public void testGetBatchRequestContent() {
		 HttpRequest request = new HttpGet(testurl);
		 List<String> arrayOfDependsOnIds = new ArrayList<>();
		 MSBatchRequestStep requestStep = new MSBatchRequestStep("1", request, arrayOfDependsOnIds);
	     MSBatchRequestContent requestContent = new MSBatchRequestContent();
	     requestContent.addBatchRequestStep(requestStep);
	     String content = requestContent.getBatchRequestContent();
	     String expectedContent = "{\"requests\":[{\"method\":\"GET\",\"dependsOn\":\"[]\",\"id\":\"1\",\"url\":\"http:\\/\\/graph.microsoft.com\"}]}";
	     assertTrue(content.compareTo(expectedContent) == 0);
	}
	
	@Test
	public void testGetBatchRequestContentWithHeader() {
		 HttpRequest request = new HttpGet(testurl);
		 request.setHeader("testkey", "testvalue");
		 List<String> arrayOfDependsOnIds = new ArrayList<>();
		 MSBatchRequestStep requestStep = new MSBatchRequestStep("1", request, arrayOfDependsOnIds);
	     MSBatchRequestContent requestContent = new MSBatchRequestContent();
	     requestContent.addBatchRequestStep(requestStep);
	     String content = requestContent.getBatchRequestContent();
	     String expectedContent = "{\"requests\":[{\"headers\":\"{\\\"testkey\\\":\\\"testvalue\\\"}\",\"method\":\"GET\",\"dependsOn\":\"[]\",\"id\":\"1\",\"url\":\"http:\\/\\/graph.microsoft.com\"}]}";
	     assertTrue(content.compareTo(expectedContent) == 0);
	}
	
	@Test
	public void testRemoveBatchRequesStepWithId() {
		HttpRequest request = new HttpGet(testurl);
		 List<String> arrayOfDependsOnIds = new ArrayList<>();
		 MSBatchRequestStep requestStep = new MSBatchRequestStep("1", request, arrayOfDependsOnIds);
	     MSBatchRequestContent requestContent = new MSBatchRequestContent();
	     requestContent.addBatchRequestStep(requestStep);
	     requestContent.removeBatchRequesStepWithId("1");
	     String content = requestContent.getBatchRequestContent();
	     String expectedContent = "{\"requests\":[]}";
	     assertTrue(content.compareTo(expectedContent) == 0);
	}
	
	@Test
	public void testRemoveBatchRequesStepWithIdByAddingMultipleBatchSteps() {
		 HttpRequest request = new HttpGet(testurl);
		 List<String> arrayOfDependsOnIds = new ArrayList<>();
		 MSBatchRequestStep requestStep = new MSBatchRequestStep("1", request, arrayOfDependsOnIds);
		 
		 HttpRequest request1 = new HttpGet(testurl);
		 List<String> arrayOfDependsOnIds1 = new ArrayList<>();
		 arrayOfDependsOnIds1.add("1");
		 MSBatchRequestStep requestStep1 = new MSBatchRequestStep("2", request1, arrayOfDependsOnIds1);
		 
	     MSBatchRequestContent requestContent = new MSBatchRequestContent();
	     requestContent.addBatchRequestStep(requestStep);
	     requestContent.addBatchRequestStep(requestStep1);
	     
	     requestContent.removeBatchRequesStepWithId("1");
	     String content = requestContent.getBatchRequestContent();
	     String expectedContent = "{\"requests\":[{\"method\":\"GET\",\"dependsOn\":\"[]\",\"id\":\"2\",\"url\":\"http:\\/\\/graph.microsoft.com\"}]}";
	     assertTrue(content.compareTo(expectedContent) == 0);
	}

}
