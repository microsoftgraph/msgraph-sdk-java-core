package com.microsoft.graph.httpcore.middlewareoption;

import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;

public interface IShouldRetry {
	boolean shouldRetry(HttpResponse response, int executionCount, HttpContext context);
}
