package com.microsoft.graph.httpcore;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

public class RequestSerializer {
	
	public static Map<String, Request> createBatchRequestsHashMap(Response batchResponse) {
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
	
	/*
	 * Convert request body to JSONObject
	 */
	public static JSONObject requestToJSONObject(final Request request) throws IOException, ParseException{
		Request copy = request.newBuilder().build();
		Buffer buffer = new Buffer();
		copy.body().writeTo(buffer);
		String body = buffer.readUtf8();
		JSONObject json = (JSONObject)new JSONParser().parse(body);
		return json;
	}
	
	/*
	 * Converts String to JSONObject
	 */
	public static JSONObject stringToJSONObject(String input) {
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
	
	/*
	 * @param list List of parameters of a single header
	 * @return List of headers to a String seperated by "; "
	 */
	public static String getHeaderValuesAsString(final List<String> list) {
		if(list == null || list.size() == 0)return "";
		StringBuilder builder = new StringBuilder(list.get(0));
		for(int i=1;i<list.size();i++) {
			builder.append(";");
			builder.append(list.get(i));
		}
		return builder.toString();
	}
	
	public static JSONObject requestBodyToJSONObject(final Request request) throws IOException, ParseException{
		if(request == null || request.body() == null)return null;
		Request copy = request.newBuilder().build();
		Buffer buffer = new Buffer();
		copy.body().writeTo(buffer);
		String requestBody = buffer.readUtf8();
		JSONObject jsonObject = (JSONObject)new JSONParser().parse(requestBody);
		return jsonObject;
	}
}
