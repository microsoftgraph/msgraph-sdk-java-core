package com.microsoft.graph.content;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.RequestBody;
import okio.Buffer;

public class MSBatchRequestContent {
	private Map<String, MSBatchRequestStep> batchRequestStepsHashMap;
	
	// Maximum number of requests that can be sent in a batch
	public static final int MAX_NUMBER_OF_REQUESTS = 20;
	
	/*
	 * Creates Batch request content using list provided
	 * 
	 * @param batchRequestStepsArray List of batch steps for batching
	 */
	public MSBatchRequestContent(List<MSBatchRequestStep> batchRequestStepsArray) {
		if(batchRequestStepsArray.size() > MAX_NUMBER_OF_REQUESTS)
			throw new IllegalArgumentException("Number of batch request steps cannot exceed " + MAX_NUMBER_OF_REQUESTS);
		
		this.batchRequestStepsHashMap = new HashMap<>();
		for(MSBatchRequestStep requestStep: batchRequestStepsArray)
			addBatchRequestStep(requestStep);
	}
	
	/*
	 * Creates empty batch request content
	 */
	public MSBatchRequestContent() {
		batchRequestStepsHashMap = new HashMap<String, MSBatchRequestStep>();
	}
	
	/*
	 * @param batchRequestStep Batch request step adding to batch content
	 * @return true or false based on addition or no addition of batch request step given
	 */
	public boolean addBatchRequestStep(MSBatchRequestStep batchRequestStep) {
		if(batchRequestStepsHashMap.containsKey(batchRequestStep.getRequestId())) 
			return false;
		batchRequestStepsHashMap.put(batchRequestStep.getRequestId(), batchRequestStep);
		return true;
	}
	
	/*
	 * @param requestId Id of Batch request step to be removed
	 * @return true or false based on removal or no removal of batch request step with given id
	 */
	public boolean removeBatchRequestStepWithId(String requestId) {
		boolean removed = false;
		if(batchRequestStepsHashMap.containsKey(requestId)) {
			batchRequestStepsHashMap.remove(requestId);
			removed = true;
			for(Map.Entry<String, MSBatchRequestStep> steps : batchRequestStepsHashMap.entrySet()) {
				while(steps.getValue().getArrayOfDependsOnIds().remove(requestId));
			}
		}
		return removed;
	}
	
	/*
	 * @return Batch request content's json as String
	 */
	public String getBatchRequestContent() {
		JSONObject batchRequestContentMap = new JSONObject();
		JSONArray batchContentArray = new JSONArray();
		for(Map.Entry<String, MSBatchRequestStep> requestStep : batchRequestStepsHashMap.entrySet()) {
			batchContentArray.add(getBatchRequestObjectFromRequestStep(requestStep.getValue()));
		}
		batchRequestContentMap.put("requests", batchContentArray);
		
		String content = batchRequestContentMap.toString();
		return content;
	}
	
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
				headerMap.put(entry.getKey(), getHeaderValuesAsString(entry.getValue()));
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
				contentmap.put("body", requestBodyToJSONObject(batchRequestStep.getRequest()));
			}catch(IOException | ParseException e) {
				e.printStackTrace();
			} 
		}
		return contentmap;
	}
	
	private String getHeaderValuesAsString(final List<String> list) {
		if(list == null || list.size() == 0)return "";
		StringBuilder builder = new StringBuilder(list.get(0));
		for(int i=1;i<list.size();i++) {
			builder.append(";");
			builder.append(list.get(i));
		}
		return builder.toString();
	}
	
	private JSONObject requestBodyToJSONObject(final Request request) throws IOException, ParseException{
		if(request == null || request.body() == null)return null;
		Request copy = request.newBuilder().build();
		Buffer buffer = new Buffer();
		copy.body().writeTo(buffer);
		String requestBody = buffer.readUtf8();
		JSONObject jsonObject = (JSONObject)new JSONParser().parse(requestBody);
		return jsonObject;
	}
	
}
