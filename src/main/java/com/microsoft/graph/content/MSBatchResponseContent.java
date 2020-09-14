package com.microsoft.graph.content;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonParseException;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

public class MSBatchResponseContent {

	private final Response batchResponse;
	private LinkedHashMap<String, Request> batchRequestsHashMap;
	private JsonArray batchResponseArray;
	private String nextLink;

	/*
	 * @param batchResponse OkHttp batch response on execution of batch requests
	 */
	public MSBatchResponseContent(final Response batchResponse) {
		this.batchResponse = batchResponse;
		update(batchResponse);
	}

	/*
	 * Returns OkHttp Response of given request Id
	 * 
	 * @param requestId Request Id of batch step
	 * 
	 * @return OkHttp Response corresponding to requestId
	 */
	public Response getResponseById(final String requestId) {
		if (batchResponseArray == null)
			return null;

		final JsonArray responses = batchResponseArray;

		for (final JsonElement response : responses) {
			if(!response.isJsonObject())
				continue;
			final JsonObject jsonresponse = response.getAsJsonObject();
			final JsonElement idElement = jsonresponse.get("id");
			if (idElement != null && idElement.isJsonPrimitive()) {
				final String id = idElement.getAsString();
				if (id.compareTo(requestId) == 0) {
					final Response.Builder builder = new Response.Builder();

					// Put corresponding request into the constructed response
					builder.request(batchRequestsHashMap.get(requestId));
					// copy protocol and message same as of batch response
					builder.protocol(batchResponse.protocol());
					builder.message(batchResponse.message());

					// Put status code of the corresponding request in JsonArray
					final JsonElement statusElement = jsonresponse.get("status");
					if (statusElement != null && statusElement.isJsonPrimitive()) {
						final Long status = statusElement.getAsLong();
						builder.code(status.intValue());
					}

					// Put body from response array for corresponding id into constructing response
					final JsonElement jsonBodyElement = jsonresponse.get("body");
					if (jsonBodyElement != null && jsonBodyElement.isJsonObject()) {
						final JsonObject JsonObject = jsonBodyElement.getAsJsonObject();
						final String bodyAsString = JsonObject.toString();
						final ResponseBody responseBody = ResponseBody
								.create(MediaType.parse("application/json; charset=utf-8"), bodyAsString);
						builder.body(responseBody);
					}

					// Put headers from response array for corresponding id into constructing
					// response
					final JsonElement jsonheadersElement = jsonresponse.get("headers");
					if (jsonheadersElement != null && jsonheadersElement.isJsonObject()) {
						final JsonObject jsonheaders = jsonheadersElement.getAsJsonObject();
						for (final String key : jsonheaders.keySet()) {
							final JsonElement strValueElement = jsonheaders.get(key);
							if (strValueElement != null && strValueElement.isJsonPrimitive()) {
								final String strvalue = strValueElement.getAsString();
								for (final String value : strvalue.split(";")) {
									builder.header(key, value);
								}
							}
						}
					}
					return builder.build();
				}
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
		if (batchResponseArray == null)
			return null;
		final Map<String, Response> responsesMap = new LinkedHashMap<>();
		for (final String id : batchRequestsHashMap.keySet()) {
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
		final Map<String, Response> responsesMap = getResponses();
		return responsesMap != null ? responsesMap.entrySet().iterator() : null;
	}

	public void update(final Response batchResponse) {
		if (batchResponse == null)
			throw new IllegalArgumentException("Batch Response cannot be null");

		final Map<String, Request> requestMap = createBatchRequestsHashMap(batchResponse);
		if (batchRequestsHashMap == null)
			batchRequestsHashMap = new LinkedHashMap<>();
		if (requestMap != null)
			batchRequestsHashMap.putAll(requestMap);

		if (batchResponse.body() != null) {
			try {
				final String batchResponseData = batchResponse.body().string();
				if (batchResponseData != null) {
					final JsonObject batchResponseObj = stringToJSONObject(batchResponseData);
					if (batchResponseObj != null) {

						final JsonElement nextLinkElement = batchResponseObj.get("@odata.nextLink");
						if (nextLinkElement != null && nextLinkElement.isJsonPrimitive())
							nextLink = nextLinkElement.getAsString();

						if (batchResponseArray == null)
							batchResponseArray = new JsonArray();

						final JsonElement responseArrayElement = batchResponseObj.get("responses");
						if (responseArrayElement != null && responseArrayElement.isJsonArray()) {
							final JsonArray responseArray = responseArrayElement.getAsJsonArray();
							batchResponseArray.addAll(responseArray);
						}
					}
				}
			} catch (final IOException e) {
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

	private Map<String, Request> createBatchRequestsHashMap(final Response batchResponse) {
		if (batchResponse == null)
			return null;
		try {
			final Map<String, Request> batchRequestsHashMap = new LinkedHashMap<>();
			final JsonObject requestJSONObject = requestBodyToJSONObject(batchResponse.request());
			final JsonElement requestArrayElement = requestJSONObject.get("requests");
			if (requestArrayElement != null && requestArrayElement.isJsonArray()) {
				final JsonArray requestArray = requestArrayElement.getAsJsonArray();
				for (final JsonElement item : requestArray) {
					if(!item.isJsonObject())
						continue;
					final JsonObject requestObject = item.getAsJsonObject();

					final Request.Builder builder = new Request.Builder();

					final JsonElement urlElement = requestObject.get("url");
					if (urlElement != null && urlElement.isJsonPrimitive()) {
						final StringBuilder fullUrl = new StringBuilder(
								batchResponse.request().url().toString().replace("$batch", ""));
						fullUrl.append(urlElement.getAsString());
						builder.url(fullUrl.toString());
					}
					final JsonElement jsonHeadersElement = requestObject.get("headers");
					if (jsonHeadersElement != null && jsonHeadersElement.isJsonObject()) {
						final JsonObject jsonheaders = jsonHeadersElement.getAsJsonObject();
						for (final String key : jsonheaders.keySet()) {
							final JsonElement strvalueElement = jsonheaders.get(key);
							if (strvalueElement != null && strvalueElement.isJsonPrimitive()) {
								final String strvalue = strvalueElement.getAsString();
								for (final String value : strvalue.split("; ")) {
									builder.header(key, value);
								}
							}
						}
					}
					final JsonElement jsonBodyElement = requestObject.get("body");
					final JsonElement jsonMethodElement = requestObject.get("method");
					if (jsonBodyElement != null && jsonMethodElement != null
						&& jsonBodyElement.isJsonObject() && jsonMethodElement.isJsonPrimitive()) {
						final JsonObject JsonObject = jsonBodyElement.getAsJsonObject();
						final String bodyAsString = JsonObject.toString();
						final RequestBody requestBody = RequestBody
								.create(MediaType.parse("application/json; charset=utf-8"), bodyAsString);
						builder.method(jsonMethodElement.getAsString(), requestBody);
					} else if (jsonMethodElement != null) {
						builder.method(jsonMethodElement.getAsString(), null);
					}
					final JsonElement jsonIdElement = requestObject.get("id");
					if (jsonIdElement != null && jsonIdElement.isJsonPrimitive()) {
						batchRequestsHashMap.put(jsonIdElement.getAsString(), builder.build());
					}
				}
			}
			return batchRequestsHashMap;

		} catch (IOException | JsonParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	private JsonObject stringToJSONObject(final String input) {
		JsonObject JsonObject = null;
		try {
			if (input != null) {
				JsonObject = JsonParser.parseString(input).getAsJsonObject();
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return JsonObject;
	}

	private JsonObject requestBodyToJSONObject(final Request request) throws IOException, JsonParseException {
		if (request == null || request.body() == null)
			return null;
		final Request copy = request.newBuilder().build();
		final Buffer buffer = new Buffer();
		copy.body().writeTo(buffer);
		final String requestBody = buffer.readUtf8();
		final JsonObject JsonObject = JsonParser.parseString(requestBody).getAsJsonObject();
		return JsonObject;
	}
}
