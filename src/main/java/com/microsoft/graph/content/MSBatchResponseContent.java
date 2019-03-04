package com.microsoft.graph.content;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import okhttp3.MediaType;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MSBatchResponseContent {

	private JSONObject batchResponseObj;
	
	public MSBatchResponseContent(String batchResponseData) {
		JSONParser parser = new JSONParser();
		try {
			if(batchResponseData != null)
				batchResponseObj = (JSONObject) parser.parse(batchResponseData);
		}
		catch(ParseException e) {
			e.printStackTrace();
		}
	}
	
	public Response getResponseById(String requestId) {
		if(batchResponseObj == null)
			return null;

		JSONArray responses = (JSONArray)batchResponseObj.get("responses");
		if(responses == null)
			return null;

		for(Object response: responses) {
			JSONObject jsonresponse = (JSONObject)response;
			String id = (String)jsonresponse.get("id");
			if(id.compareTo(requestId) == 0) {
				Response.Builder builder = new Response.Builder();
				
				if(jsonresponse.get("status") != null) {
					String status = (String)jsonresponse.get("status");
					builder.code(Integer.parseInt(status));
				}
				
				if(jsonresponse.get("body") != null) {
					String bodyAsString = (String)jsonresponse.get("body");
					ResponseBody responseBody = ResponseBody.create(MediaType.parse("application/json; charset=utf-8"), bodyAsString);
					builder.body(responseBody);
				}
				if(jsonresponse.get("headers") != null){
					JSONObject jsonheaders = (JSONObject)jsonresponse.get("headers");
					for(Object key: jsonheaders.keySet()) {
						String strkey = (String)key;
						String strvalue = (String)jsonheaders.get(strkey);
						for(String value : strvalue.split("; ")) {
							builder.header(strkey, value);
						}
					}	
				}
				return builder.build();
			}
		}
		return null;
	}
	
	public String getResponses() {
		if(batchResponseObj != null)
			return ((JSONArray)batchResponseObj.get("responses")).toJSONString();
		return null;
	}
}
