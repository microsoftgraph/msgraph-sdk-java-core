package com.microsoft.graph.content;

import java.util.Set;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class MSBatchResponseContent {

	private JSONObject batchResponseObj;
	
	public MSBatchResponseContent(String batchResponseData ) {
		JSONParser parser = new JSONParser();
		try {
			if(batchResponseData != null)
				batchResponseObj = (JSONObject) parser.parse(batchResponseData);
		}
		catch(ParseException e) {
		}
	}
	
	public HttpResponse getResponseById(String requestId) {
		if(batchResponseObj == null)
			return null;

		JSONArray responses = (JSONArray)batchResponseObj.get("responses");
		if(responses == null)
			return null;

		for(Object response: responses) {
			JSONObject jsonresponse = (JSONObject)response;
			String id = (String)jsonresponse.get("id");
			if(id.compareTo(requestId) == 0) {
				HttpResponse httpresponse = new BasicHttpResponse(null, ((Long)jsonresponse.get("status")).intValue(), null);
				if(jsonresponse.get("body") != null) {
					HttpEntity entity = new StringEntity(jsonresponse.get("body").toString(), ContentType.APPLICATION_JSON);
					httpresponse.setEntity(entity);	
				}
				if(jsonresponse.get("headers") != null){
					JSONObject jsonheaders = (JSONObject)jsonresponse.get("headers");
					for(Object key: jsonheaders.keySet()) {
						String strkey = (String)key;
						String strvalue = (String)jsonheaders.get(strkey);
						httpresponse.setHeader(strkey, strvalue);
					}	
				}
				return httpresponse;

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
