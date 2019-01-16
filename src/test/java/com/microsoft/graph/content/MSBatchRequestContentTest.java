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

	@Test
	public void testMSBatchRequestContentCreation() {
		
		List<MSBatchRequestStep> requestStepArray = new ArrayList<>();
		 for(int i=0;i<5;i++) {
			 HttpRequest request = new HttpGet("http://graph.microsoft.com");
			 List<String> arrayOfDependsOnIds = new ArrayList();
			 MSBatchRequestStep requestStep = new MSBatchRequestStep("" + i, request, arrayOfDependsOnIds);
		     requestStepArray.add(requestStep);
		 }
		 MSBatchRequestContent requestContent = new MSBatchRequestContent(requestStepArray);
		 assertTrue(requestContent.getBatchRequestContent() != null);
	}
	
	@Test
	public void testGetBatchRequestContent() {
		 HttpRequest request = new HttpGet("http://graph.microsoft.com");
		 List<String> arrayOfDependsOnIds = new ArrayList();
		 MSBatchRequestStep requestStep = new MSBatchRequestStep("1", request, arrayOfDependsOnIds);
	     MSBatchRequestContent requestContent = new MSBatchRequestContent();
	     requestContent.addBatchRequestStep(requestStep);
	     String content = requestContent.getBatchRequestContent();
	     String expectedContent = "{\"requests\":[{\"method\":\"GET\",\"dependsOn\":\"[]\",\"id\":\"1\",\"url\":\"http:\\/\\/graph.microsoft.com\"}]}";
	     assertTrue(content.compareTo(expectedContent) == 0);
	}

}
