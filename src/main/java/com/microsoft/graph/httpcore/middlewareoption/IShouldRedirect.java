package com.microsoft.graph.httpcore.middlewareoption;

import okhttp3.Response;

public interface IShouldRedirect {
	boolean shouldRedirect(final Response response);
}
