package com.microsoft.graph.content;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

public class MSBatchResponseContent {

	private Response batchResponse;
	private Map<String, Request> batchRequestsHashMap;
	private JSONArray batchResponseArray;
	private String nextLink;
	
	/*
	 * @param batchResponse OkHttp batch response on execution of batch requests
	 */
	public MSBatchResponseContent(Response batchResponse) {
		this.batchResponse = batchResponse;
		update(batchResponse);
	}
	
	/*
	 * Returns OkHttp Response of given request Id
	 * 
	 * @param requestId Request Id of batch step
	 * @return OkHttp Response corresponding to requestId
	 */
	public Response getResponseById(String requestId) {
		if(batchResponseArray == null) return null;

		JSONArray responses = batchResponseArray;

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
	
	/**
	 * Get map of id and responses
	 * 
	 * @return responses in Map of id and response
	 */
	public Map<String, Response> getResponses() {
		if(batchResponseArray == null) 
			return null;
		Map<String, Response> responsesMap = new HashMap<>();
		for(String id : batchRequestsHashMap.keySet()) {
			responsesMap.put(id, getResponseById(id));
		}
		return responsesMap;
	}
	
	/**
	 * Get iterator over the responses
	 * 
	 * @return iterator for responses
	 */
	public Iterator<Map.Entry<String, Response>> getResponsesIterator() {
		if(batchResponseArray == null) 
			return null;
		Map<String, Response> responsesMap = new HashMap<>();
		for(String id : batchRequestsHashMap.keySet()) {
			responsesMap.put(id, getResponseById(id));
		}
		return responsesMap.entrySet().iterator();
	}
	
	public void update(Response batchResponse) {
		if(batchResponse == null)
			throw new IllegalArgumentException("Batch Response cannot be null");
		
		Map<String, Request> requestMap = createBatchRequestsHashMap(batchResponse);
		if(batchRequestsHashMap == null)
			batchRequestsHashMap = new HashMap<>();
		if(requestMap != null)
			batchRequestsHashMap.putAll(requestMap);
		
		if(batchResponse.body() != null) {
			try {
				String batchResponseData = batchResponse.body().string();
				if(batchResponseData != null) {
					JSONObject batchResponseObj = stringToJSONObject(batchResponseData);
					if(batchResponseObj != null) {
						
						JSONObject nextLinkObject = (JSONObject) batchResponseObj.get("@odata.nextLink");
						if(nextLinkObject!=null)
							nextLink = nextLinkObject.toString();		
						
						if(batchResponseArray == null)
							batchResponseArray = new JSONArray();
						
						JSONArray responseArray = (JSONArray)batchResponseObj.get("responses");
						if(responseArray!=null)
							batchResponseArray.addAll(responseArray);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/*
	 * @return nextLink of batch response
	 */
	public String nextLink() {
		return nextLink;
	}
	
	private Map<String, Request> createBatchRequestsHashMap(Response batchResponse) {
		if(batchResponse == null)return null;
		try {
			Map<String, Request> batchRequestsHashMap = new HashMap<>();
			JSONObject requestJSONObject = requestBodyToJSONObject(batchResponse.request());
			JSONArray requestArray = (JSONArray)requestJSONObject.get("requests");
			for(Object item : requestArray) {
				JSONObject requestObject = (JSONObject)item;
				
				Request.Builder builder = new Request.Builder();
				
				if(requestObject.get("url") != null) {
					StringBuilder fullUrl = new StringBuilder(batchResponse.request().url().toString().replace("$batch",""));
					fullUrl.append(requestObject.get("url").toString());
					builder.url(fullUrl.toString());
				}
				if(requestObject.get("headers") != null) {
					JSONObject jsonheaders = (JSONObject)requestObject.get("headers");
					for(Object key: jsonheaders.keySet()) {
						String strkey = (String)key;
						String strvalue = (String)jsonheaders.get(strkey);
						for(String value : strvalue.split("; ")) {
							builder.header(strkey, value);
						}
					}	
				}
				if(requestObject.get("body") != null) {
					JSONObject jsonObject = (JSONObject)requestObject.get("body");
					String bodyAsString = jsonObject.toJSONString();
					RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), bodyAsString);
					builder.method(requestObject.get("method").toString(), requestBody);
				} else {
					builder.method(requestObject.get("method").toString(), null);
				}
				batchRequestsHashMap.put(requestObject.get("id").toString(), builder.build());
			}
			return batchRequestsHashMap;
			
		} catch (IOException | ParseException e) { e.printStackTrace(); }
		return null;
	}
	
	private JSONObject stringToJSONObject(String input) {
		JSONParser parser = new JSONParser();
		JSONObject jsonObject = null;
		try {
			if(input != null) {
				jsonObject = (JSONObject) parser.parse(input);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return jsonObject;
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
