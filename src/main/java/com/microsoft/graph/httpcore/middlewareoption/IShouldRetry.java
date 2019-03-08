package com.microsoft.graph.httpcore.middlewareoption;

import okhttp3.Request;
import okhttp3.Response;

public interface IShouldRetry {
	boolean shouldRetry(long delay, int executionCount, Request request,Response response);
}
