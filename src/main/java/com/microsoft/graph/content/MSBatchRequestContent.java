package com.microsoft.graph.content;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import com.microsoft.graph.httpcore.RequestSerializer;

import okhttp3.Headers;
import okhttp3.RequestBody;

public class MSBatchRequestContent {
	private Map<String, MSBatchRequestStep> batchRequestStepsHashMap;
	public static final int MAX_NUMBER_OF_REQUESTS = 20;
	
	public MSBatchRequestContent(List<MSBatchRequestStep> batchRequestStepsArray) {
		if(batchRequestStepsArray.size() > MAX_NUMBER_OF_REQUESTS)
			throw new IllegalArgumentException("Number of batch request steps cannot exceed " + MAX_NUMBER_OF_REQUESTS);
		
		this.batchRequestStepsHashMap = new HashMap<>();
		for(MSBatchRequestStep requestStep: batchRequestStepsArray)
			addBatchRequestStep(requestStep);
	}
	
	public MSBatchRequestContent() {
		batchRequestStepsHashMap = new HashMap<String, MSBatchRequestStep>();
	}
	
	public boolean addBatchRequestStep(MSBatchRequestStep batchRequestStep) {
		if(batchRequestStepsHashMap.containsKey(batchRequestStep.getRequestId())) 
			return false;
		batchRequestStepsHashMap.put(batchRequestStep.getRequestId(), batchRequestStep);
		return true;
	}
	
	public boolean removeBatchRequestStepWithId(String requestId) {
		boolean removed = false;
		if(batchRequestStepsHashMap.containsKey(requestId)) {
			batchRequestStepsHashMap.remove(requestId);
			removed = true;
		}
		for(Map.Entry<String, MSBatchRequestStep> steps : batchRequestStepsHashMap.entrySet()) {
			while(steps.getValue().getArrayOfDependsOnIds().remove(requestId))
				removed = true;
		}
		return removed;
	}
	
	@SuppressWarnings("unchecked")
	public String getBatchRequestContent() {
		JSONObject batchRequestContentMap = new JSONObject();
		JSONArray batchContentArray = new JSONArray();
		for(Map.Entry<String, MSBatchRequestStep> requestStep : batchRequestStepsHashMap.entrySet()) {
			batchContentArray.add(getBatchRequestObjectFromRequestStep(requestStep.getValue()));
		}
		batchRequestContentMap.put("requests", batchContentArray);
		
		String content =  batchRequestContentMap.toString();
		return content;
	}
	
	@SuppressWarnings("unchecked")
	private JSONObject getBatchRequestObjectFromRequestStep(final MSBatchRequestStep batchRequestStep){
		JSONObject contentmap = new JSONObject();
		contentmap.put("id", batchRequestStep.getRequestId());
		
		String url = batchRequestStep.getRequest().url().toString();
		url = url.replaceAll("https://graph.microsoft.com/v1.0/", "");
		url = url.replaceAll("http://graph.microsoft.com/v1.0/", "");
		url = url.replaceAll("https://graph.microsoft.com/beta/", "");
		url = url.replaceAll("http://graph.microsoft.com/beta/", "");
		contentmap.put("url", url);
		
		contentmap.put("method", batchRequestStep.getRequest().method().toString());
		
		Headers headers = batchRequestStep.getRequest().headers();
		if(headers != null && headers.size() != 0) {
			JSONObject headerMap = new JSONObject();
			for(Map.Entry<String, List<String>> entry : headers.toMultimap().entrySet()) {
				headerMap.put(entry.getKey(), RequestSerializer.getHeaderValuesAsString(entry.getValue()));
			}
			contentmap.put("headers", headerMap);
		}
		
		List<String> arrayOfDependsOnIds = batchRequestStep.getArrayOfDependsOnIds();
		if(arrayOfDependsOnIds != null) {
			JSONArray array = new JSONArray();
			for(String dependsOnId : arrayOfDependsOnIds) array.add(dependsOnId);
			contentmap.put("dependsOn", array);
		}
		
		RequestBody body = batchRequestStep.getRequest().body(); 
		if(body != null) {
			try {
				contentmap.put("body", RequestSerializer.requestBodyToJSONObject(batchRequestStep.getRequest()));
			}catch(IOException | ParseException e) {
				e.printStackTrace();
			} 
		}
		return contentmap;
	}
	
}
