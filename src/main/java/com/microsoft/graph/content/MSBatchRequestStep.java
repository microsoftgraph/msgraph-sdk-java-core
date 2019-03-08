package com.microsoft.graph.content;

import java.util.List;

import okhttp3.Request;

public class MSBatchRequestStep {
	private String requestId;
	private Request request;
	private List<String> arrayOfDependsOnIds;
	
	public MSBatchRequestStep(String requestId, Request request, List<String> arrayOfDependsOnIds) {
		if(requestId == null)
			throw new IllegalArgumentException("Request Id cannot be null.");
		if(request == null)
			new IllegalArgumentException("Request cannot be null.");
				
		this.requestId = requestId;
		this.request = request;
		this.arrayOfDependsOnIds = arrayOfDependsOnIds;
	}
	
	public String getRequestId() {
		return requestId;
	}
	
	public Request getRequest() {
		return request;
	}
	
	public List<String> getArrayOfDependsOnIds(){
		return arrayOfDependsOnIds;
	}
}
