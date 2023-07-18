package com.microsoft.graph;

import okhttp3.Request;

import java.util.Map;

public class UrlReplacement {
    private UrlReplacement() {
        // Default constructor
    }
    public static Request replaceRequestUrl(Request request, Map<String, String> replacementPairs) {
        Request.Builder builder = request.newBuilder();
        String url = request.url().toString();
        for (Map.Entry<String, String> entry : replacementPairs.entrySet()) {
            url = url.replace(entry.getKey(), entry.getValue());
        }
        builder.url(url);
        return builder.build();
    }
}
