package com.microsoft.graph.httpcore;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class TelemetryHandler implements Interceptor{
	
	private final String SDK_VERSION = "SdkVersion";
	private final String VERSION = "0.1.0-SNAPSHOT";
	private final String GRAPH_VERSION_PREFIX = "graph-java-core-v";

	@Override
	public Response intercept(Chain chain) throws IOException {
		Request request = chain.request();
		if(request.header(SDK_VERSION) != null)
			return chain.proceed(request);
		Request sdkVersionAddedRequest = request.newBuilder().addHeader(SDK_VERSION, GRAPH_VERSION_PREFIX + VERSION).build();
		return chain.proceed(sdkVersionAddedRequest);
	}

}
