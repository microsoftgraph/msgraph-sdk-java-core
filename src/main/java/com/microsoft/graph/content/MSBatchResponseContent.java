package com.microsoft.graph.content;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class MSBatchResponseContent {

	private JSONObject batchResponseObj;
	
	public MSBatchResponseContent(String batchResponseData ) {
		JSONParser parser = new JSONParser();
		try {
			batchResponseObj = (JSONObject) parser.parse(batchResponseData);
		}
		catch(ParseException e) {
		}
	}
	
	public String getResponseById(String requestId) {
		 if(batchResponseObj.get(requestId) != null)
			 return batchResponseObj.get(requestId).toString();
		 return null;
	}
	
	public String getResponses() {
		if(batchResponseObj != null)
			return batchResponseObj.toJSONString();
		return null;
	}
}
