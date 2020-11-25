package com.microsoft.graph.content;

import java.util.List;

import javax.annotation.Nullable;
import javax.annotation.Nonnull;

import okhttp3.Request;

public class MSBatchRequestStep {
	private String requestId;
	private Request request;
	private List<String> arrayOfDependsOnIds;
	
	public MSBatchRequestStep(@Nonnull final String requestId, @Nonnull final Request request, @Nullable final List<String> arrayOfDependsOnIds) {
		if(requestId == null)
			throw new IllegalArgumentException("Request Id cannot be null.");
		if(requestId.length() == 0)
			throw new IllegalArgumentException("Request Id cannot be empty.");
		if(request == null)
			new IllegalArgumentException("Request cannot be null.");
				
		this.requestId = requestId;
		this.request = request;
		this.arrayOfDependsOnIds = arrayOfDependsOnIds;
	}
	
	@Nonnull
	public String getRequestId() {
		return requestId;
	}
	
	@Nonnull
	public Request getRequest() {
		return request;
	}
	
	@Nullable
	public List<String> getArrayOfDependsOnIds(){
		return arrayOfDependsOnIds;
	}
}
