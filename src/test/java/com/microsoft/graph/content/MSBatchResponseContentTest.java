package com.microsoft.graph.content;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import okhttp3.Response;

public class MSBatchResponseContentTest {
	
	@Test
	public void testNullMSBatchResponseContent() {
		String responsedata = null;
		MSBatchResponseContent batchresponse = new MSBatchResponseContent(responsedata);
		assertTrue(batchresponse.getResponses() == null);
	}
	
	@Test
	public void testValidMSBatchResponseContent() {
		String responsedata = "{\"responses\": [{ \"id\": \"1\", \"status\": 302, \"headers\": { \"location\": \"https://b0mpua-by3301.files.1drv.com/y23vmagahszhxzlcvhasdhasghasodfi\" } }, { \"id\": \"3\", \"status\": 401, \"body\": { \"error\": { \"code\": \"Forbidden\", \"message\": \"...\" } } }, { \"id\": \"2\", \"status\": 200, \"body\": { \"@odata.context\": \"https://graph.microsoft.com/v1.0/$metadata#Collection(microsoft.graph.plannerTask)\", \"value\": [] } }, { \"id\": \"4\", \"status\": 204, \"body\": null } ] }";
		MSBatchResponseContent batchresponse = new MSBatchResponseContent(responsedata);
		assertTrue(batchresponse.getResponses() != null);
	}
	
	@Test
	public void testInvalidMSBatchResponseContentWithEmptyResponse() {
		String responsedata = "{\"responses\": [] }";
		MSBatchResponseContent batchresponse = new MSBatchResponseContent(responsedata);
		assertTrue(batchresponse.getResponseById("1") == null);
	}
	
	@Test
	public void testInvalidMSBatchResponseContentWithNullResponseString() {
		MSBatchResponseContent batchresponse = new MSBatchResponseContent(null);
		assertTrue(batchresponse.getResponseById("1") == null);
	}
	
	@Test
	public void testInvalidMSBatchResponseContentWithMalformedResponse() {
		String invalidResponsedata = "{responses: [] }";
		MSBatchResponseContent batchresponse = new MSBatchResponseContent(invalidResponsedata);
		assertTrue(batchresponse.getResponses() == null);
	}
	
	@Test
	public void testGetMSBatchResponseContentByID() {
		String responsedata = "{\"responses\": [{ \"id\": \"1\", \"status\": 302, \"headers\": { \"location\": \"https://b0mpua-by3301.files.1drv.com/y23vmagahszhxzlcvhasdhasghasodfi\" } }, { \"id\": \"3\", \"status\": 401, \"body\": { \"error\": { \"code\": \"Forbidden\", \"message\": \"...\" } } }, { \"id\": \"2\", \"status\": 200, \"body\": { \"@odata.context\": \"https://graph.microsoft.com/v1.0/$metadata#Collection(microsoft.graph.plannerTask)\", \"value\": [] } }, { \"id\": \"4\", \"status\": 204, \"body\": null } ] }";
		MSBatchResponseContent batchresponse = new MSBatchResponseContent(responsedata);
		Response response = batchresponse.getResponseById("1");
		assertTrue(response != null);
	}
}
