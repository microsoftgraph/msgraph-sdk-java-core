package com.microsoft.graph.httpcore.middlewareoption;

import okhttp3.Request;
import okhttp3.Response;

import javax.annotation.Nonnull;

public interface IShouldRetry {
	boolean shouldRetry(long delay, int executionCount, @Nonnull final Request request, @Nonnull final Response response);
}
