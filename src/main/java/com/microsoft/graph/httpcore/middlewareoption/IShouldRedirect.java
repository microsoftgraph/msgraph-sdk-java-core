package com.microsoft.graph.httpcore.middlewareoption;

import javax.annotation.Nonnull;

import okhttp3.Response;

public interface IShouldRedirect {
	boolean shouldRedirect(@Nonnull final Response response);
}
