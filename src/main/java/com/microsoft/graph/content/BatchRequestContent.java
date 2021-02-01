// ------------------------------------------------------------------------------
// Copyright (c) 2021 Microsoft Corporation
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sub-license, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.
// ------------------------------------------------------------------------------

package com.microsoft.graph.content;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.microsoft.graph.http.HttpMethod;
import com.microsoft.graph.http.IHttpRequest;
import com.microsoft.graph.options.HeaderOption;

public class BatchRequestContent {
    @Expose
    @SerializedName("requests")
    public List<BatchRequestStep<?>> requests;

    @Nonnull
    public String addBatchRequestStep(@Nonnull final IHttpRequest request) {
        Objects.requireNonNull(request, "request parameter cannot be null");
        return addBatchRequestStep(request, request.getHttpMethod() == null ? HttpMethod.GET : request.getHttpMethod());
    }
    @Nonnull
    public String addBatchRequestStep(@Nonnull final IHttpRequest request, @Nonnull final HttpMethod httpMethod) {
        return addBatchRequestStep(request, httpMethod, null);
    }
    @Nonnull
    public <T> String addBatchRequestStep(@Nonnull final IHttpRequest request, @Nonnull final HttpMethod httpMethod, @Nullable final T serializableBody) {
        return addBatchRequestStep(request, httpMethod, serializableBody, null);
    }
    public <T> String addBatchRequestStep(@Nonnull final IHttpRequest request, @Nonnull final HttpMethod httpMethod, @Nullable final T serializableBody, @Nullable final HashSet<String> dependsOnRequestsIds) {
        Objects.requireNonNull(request, "request parameter cannot be null");
        Objects.requireNonNull(httpMethod, "httpMethod parameter cannot be null");
        if(dependsOnRequestsIds != null)
            for(final String id : dependsOnRequestsIds) {
                if(getStepById(id) == null)
                    throw new IllegalArgumentException("the current request depends on a inexisting request");
            }

        if(requests == null)
            requests = new ArrayList<>();

        final Matcher protocolAndHostReplacementMatcher = protocolAndHostReplacementPattern.matcher(request.getRequestUrl().toString());
        final BatchRequestStep<T> step = new BatchRequestStep<T>() {{
            url = protocolAndHostReplacementMatcher.replaceAll("");
            body = serializableBody;
            method = httpMethod;
            dependsOn = dependsOnRequestsIds;
            id = getNextRequestId();
        }};

        if(!request.getHeaders().isEmpty()) {
            step.headers = new HashMap<>();
            for(final HeaderOption headerOption : request.getHeaders())
                step.headers.putIfAbsent(headerOption.getName(), headerOption.getValue().toString());
        }
        requests.add(step);
        return step.id;
    }
    public void removeBatchRequestStepWithId(@Nonnull final String ...stepIds) {
        if(requests == null) return;

        for(final String stepId : stepIds) {
            Objects.requireNonNull(stepId, "parameter stepIds cannot contain null values");
            requests.removeIf(x -> x.id == stepId);
            for(final BatchRequestStep<?> step : requests) {
                if(step.dependsOn != null) {
                    step.dependsOn.removeIf(x -> x == stepId);
                    if(step.dependsOn.isEmpty())
                        step.dependsOn = null; // so we don't send dependsOn: [] over the wire
                }
            }
        }
    }
    @Nullable
    @SuppressWarnings("unchecked")
    public <T> BatchRequestStep<T> getStepById(@Nonnull final String stepId) {
        Objects.requireNonNull(stepId, "parameter stepId cannot be null");
        if(requests == null) return null;

        for(final BatchRequestStep<?> step : requests) {
            if(stepId.equals(step.id))
                return (BatchRequestStep<T>)step;
        }
        return null;
    }
    private static final Pattern protocolAndHostReplacementPattern = Pattern.compile("(?i)^http[s]?:\\/\\/graph\\.microsoft\\.com\\/(?>v1\\.0|beta)\\/?"); // (?i) case insensitive
    private String getNextRequestId() {
        String requestId;
        do {
            requestId = Integer.toString(ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE));
        } while(getStepById(requestId) != null);
        return requestId;
    }
}
