package com.microsoft.graph.httpcore;

import java.io.IOException;

import com.microsoft.graph.httpcore.middlewareoption.TelemetryOptions;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class TelemetryHandler implements Interceptor{

	public static final String SDK_VERSION = "SdkVersion";
	public static final String VERSION = "v1.0.2";
	public static final String GRAPH_VERSION_PREFIX = "graph-java-core";
	public static final String JAVA_VERSION_PREFIX = "java";
	public static final String CLIENT_REQUEST_ID = "client-request-id";

	@Override
	public Response intercept(Chain chain) throws IOException {
		Request request = chain.request();
		Request.Builder telemetryAddedBuilder = request.newBuilder();

		TelemetryOptions telemetryOptions = request.tag(TelemetryOptions.class);
		if(telemetryOptions == null)
			telemetryOptions = new TelemetryOptions();

		String featureUsage = "(featureUsage=" + telemetryOptions.getFeatureUsage() + ")";
		String javaVersion = System.getProperty("java.version");
		String sdkversion_value = GRAPH_VERSION_PREFIX + "/" + VERSION + " " + featureUsage + " " + JAVA_VERSION_PREFIX + "/" + javaVersion;
		telemetryAddedBuilder.addHeader(SDK_VERSION, sdkversion_value);

		if(request.header(CLIENT_REQUEST_ID) == null) {
			telemetryAddedBuilder.addHeader(CLIENT_REQUEST_ID, telemetryOptions.getClientRequestId());
		}

		return chain.proceed(telemetryAddedBuilder.build());
	}

}
