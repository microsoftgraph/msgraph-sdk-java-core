package com.microsoft.graph.content;

import java.util.List;

import org.apache.http.HttpRequest;

public class MSBatchRequestStep {
	private String requestId;
	private HttpRequest request;
	private List<String> arrayOfDependsOnIds;
	
	public MSBatchRequestStep(String requestId, HttpRequest request, List<String> arrayOfDependsOnIds) {
		this.requestId = requestId;
		this.request = request;
		this.arrayOfDependsOnIds = arrayOfDependsOnIds;
	}
	
	public String getRequestId() {
		return requestId;
	}
	
	public HttpRequest getRequest() {
		return request;
	}
	
	public List<String> getArrayOfDependsOnIds(){
		return arrayOfDependsOnIds;
	}
}
