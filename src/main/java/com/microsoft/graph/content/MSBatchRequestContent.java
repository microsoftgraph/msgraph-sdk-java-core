package com.microsoft.graph.content;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class MSBatchRequestContent {
	private List<MSBatchRequestStep> batchRequestStepsArray;
	private final int maxNumberOfRequests = 20;
	
	public MSBatchRequestContent(List<MSBatchRequestStep> batchRequestStepsArray) {
		this.batchRequestStepsArray = new ArrayList<>();
		if(batchRequestStepsArray.size() <= maxNumberOfRequests) {
			for(MSBatchRequestStep requestStep: batchRequestStepsArray)
				addBatchRequestStep(requestStep);
		}
	}
	
	public MSBatchRequestContent() {
		batchRequestStepsArray = new ArrayList<>();
	}
	
	public boolean addBatchRequestStep(MSBatchRequestStep batchRequestStep) {
		if(batchRequestStep.getRequestId().compareTo("") == 0)
			return false;
		if(batchRequestStepsArray.size() == maxNumberOfRequests)
			return false;
		for(MSBatchRequestStep requestStep: batchRequestStepsArray) {
			if(batchRequestStep.getRequestId().compareTo(requestStep.getRequestId()) == 0)
				return false;
		}
		return batchRequestStepsArray.add(batchRequestStep);
	}
	
	public boolean removeBatchRequesStepWithId(String requestId) {
		boolean ret = false;
		for (int i = batchRequestStepsArray.size()-1; i >= 0; i--)
	    {
	        MSBatchRequestStep requestStep = batchRequestStepsArray.get(i);
	        for (int j = requestStep.getArrayOfDependsOnIds().size() - 1; j >= 0; j--)
	        {
	            String dependsOnId = requestStep.getArrayOfDependsOnIds().get(j);
	            if(dependsOnId.compareTo(requestId) == 0)
	            {
	                requestStep.getArrayOfDependsOnIds().remove(j);
	                ret = true;
	            }
	        }
	        if(requestId.compareTo(requestStep.getRequestId()) == 0) {
	            batchRequestStepsArray.remove(i);
	            ret = true;
	        }
	    }
		return ret;
	}
	
	public String getBatchRequestContent() {
		Map<String, List<Map<String, String>>> batchRequestContentMap = new HashMap<>();
		List<Map<String, String>> batchContentArray = new ArrayList<>();
		for(MSBatchRequestStep requestStep : batchRequestStepsArray) {
			batchContentArray.add(getBatchRequestMapFromRequestStep(requestStep));
		}
		batchRequestContentMap.put("requests", batchContentArray);
		return JSONValue.toJSONString(batchRequestContentMap);
	}
	
	private Map<String, String> getBatchRequestMapFromRequestStep(MSBatchRequestStep batchRequestStep){
		Map<String, String> contentmap = new HashMap<>();
		contentmap.put("id", batchRequestStep.getRequestId());
		contentmap.put("url", batchRequestStep.getRequest().getRequestLine().getUri());
		contentmap.put("method", batchRequestStep.getRequest().getRequestLine().getMethod());
		Header[] headers = batchRequestStep.getRequest().getAllHeaders();
		if(headers != null && headers.length != 0) {
			JSONObject obj = new JSONObject();
			for(Header header: headers) {
				obj.put(header.getName(), header.getValue());
			}
			contentmap.put("headers", obj.toJSONString());
		}
		HttpEntity entity = null;
		HttpRequest request = batchRequestStep.getRequest();
		if(request instanceof HttpEntityEnclosingRequestBase) {
			HttpEntityEnclosingRequestBase httprequest = (HttpEntityEnclosingRequestBase)request;
			entity = httprequest.getEntity();
		}
		if(entity != null) {
			try {
				String body = EntityUtils.toString(entity);
				contentmap.put("body", body);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		List<String> arrayOfDependsOnIds = batchRequestStep.getArrayOfDependsOnIds();
		if(arrayOfDependsOnIds != null) {
			contentmap.put("dependsOn", JSONValue.toJSONString(arrayOfDependsOnIds));
		}
		
		return contentmap;
	}

}
