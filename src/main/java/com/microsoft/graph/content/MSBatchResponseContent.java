package com.microsoft.graph.content;

import java.io.IOException;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.microsoft.graph.httpcore.RequestSerializer;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MSBatchResponseContent {

	private JSONObject batchResponseObj;
	private Response batchResponse;
	private Map<String, Request> batchRequestsHashMap;
	private JSONArray batchResponseArray;
	
	public MSBatchResponseContent(Response batchResponse) {
		if(batchResponse == null)
			throw new IllegalArgumentException("Batch Response cannot be null");
		
		this.batchRequestsHashMap = RequestSerializer.createBatchRequestsHashMap(batchResponse);
		this.batchResponse = batchResponse;
		if(batchResponse.body() != null) {
			try {
				String batchResponseData = batchResponse.body().string();
				if(batchResponseData != null) {
					batchResponseObj = RequestSerializer.stringToJSONObject(batchResponseData);
					if(batchResponseObj != null) {
						batchResponseArray = (JSONArray)batchResponseObj.get("responses");
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public Response getResponseById(String requestId) {
		if(batchResponseObj == null)
			return null;

		JSONArray responses = (JSONArray)batchResponseObj.get("responses");
		if(responses == null)
			return null;

		for(Object response : responses) {
			JSONObject jsonresponse = (JSONObject)response;
			String id = (String)jsonresponse.get("id");
			if(id.compareTo(requestId) == 0) {
				Response.Builder builder = new Response.Builder();
				
				// Put corresponding request into the constructed response
				builder.request(batchRequestsHashMap.get(requestId));
				// copy protocol and message same as of batch response
				builder.protocol(batchResponse.protocol());
				builder.message(batchResponse.message());
				
				// Put status code of the corresponding request in JSONArray
				if(jsonresponse.get("status") != null) {
					Long status = (Long)jsonresponse.get("status");
					builder.code(status.intValue());
				}
				
				// Put body from response array for corresponding id into constructing response
				if(jsonresponse.get("body") != null) {
					JSONObject jsonObject = (JSONObject)jsonresponse.get("body");
					String bodyAsString = jsonObject.toJSONString();
					ResponseBody responseBody = ResponseBody.create(MediaType.parse("application/json; charset=utf-8"), bodyAsString);
					builder.body(responseBody);
				}
				
				// Put headers from response array for corresponding id into constructing response
				if(jsonresponse.get("headers") != null){
					JSONObject jsonheaders = (JSONObject)jsonresponse.get("headers");
					for(Object key: jsonheaders.keySet()) {
						String strkey = (String)key;
						String strvalue = (String)jsonheaders.get(strkey);
						for(String value : strvalue.split(";")) {
							builder.header(strkey, value);
						}
					}	
				}
				return builder.build();
			}
		}
		return null;
	}
	
	/*
	 * @return responses as a string
	 */
	public String getResponses() {
		return batchResponseArray != null ? batchResponseArray.toJSONString() : null;
	}
	
	/*
	 * @return nextLink of batch response
	 */
	public String nextLink() {
		if(batchResponseObj == null) return null;
		Object nextLinkObject = batchResponseObj.get("nextLink");
		return nextLinkObject != null ? ((JSONObject)nextLinkObject).toString() : null;
	}
}
