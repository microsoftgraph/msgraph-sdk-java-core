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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
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
import com.microsoft.graph.serializer.IJsonBackedObject;

/** Respresents the content of a JSON batch request */
public class BatchRequestContent {
    /** Steps part of the batch request */
    @Expose
    @Nullable
    @SerializedName("requests")
    public List<BatchRequestStep<?>> requests;

    /**
     * Adds a request as a step to the current batch. Defaults to GET if the HTTP method is not set in the request.
     * @param request the request to add as a step
     * @return the id of the step that was just added to the batch
     */
    @Nonnull
    public String addBatchRequestStep(@Nonnull final IHttpRequest request) {
        Objects.requireNonNull(request, "request parameter cannot be null");
        return addBatchRequestStep(request, request.getHttpMethod() == null ? HttpMethod.GET : request.getHttpMethod());
    }
    /**
     * Adds a request as a step to the current batch
     * @param request the request to add as a step
     * @param httpMethod the HttpMethod to execute the request with
     * @return the id of the step that was just added to the batch
     */
    @Nonnull
    public String addBatchRequestStep(@Nonnull final IHttpRequest request, @Nonnull final HttpMethod httpMethod) {
        return addBatchRequestStep(request, httpMethod, null);
    }
    /**
     * Adds a request as a step to the current batch
     * @param request the request to add as a step
     * @param httpMethod the HttpMethod to execute the request with
     * @param <T> the type of the request body
     * @param serializableBody the body of the request to be serialized
     * @return the id of the step that was just added to the batch
     */
    @Nonnull
    public <T> String addBatchRequestStep(@Nonnull final IHttpRequest request, @Nonnull final HttpMethod httpMethod, @Nullable final T serializableBody) {
        return addBatchRequestStep(request, httpMethod, serializableBody, (String[])null);
    }
    /**
     * Adds a request as a step to the current batch
     * @param request the request to add as a step
     * @param httpMethod the HttpMethod to execute the request with
     * @param <T> the type of the request body
     * @param serializableBody the body of the request to be serialized
     * @param dependsOnRequestsIds the ids of steps this request depends on
     * @return the id of the step that was just added to the batch
     */
    @Nonnull
    public <T> String addBatchRequestStep(@Nonnull final IHttpRequest request, @Nonnull final HttpMethod httpMethod, @Nullable final T serializableBody, @Nullable final String ...dependsOnRequestsIds) {
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
            method = httpMethod.toString().toUpperCase(Locale.getDefault());
            dependsOn = dependsOnRequestsIds != null && dependsOnRequestsIds.length > 0 ? new HashSet<>(Arrays.asList(dependsOnRequestsIds)) : null;
            id = getNextRequestId();
        }};

        if(!request.getHeaders().isEmpty()) {
            step.headers = new HashMap<>();
            for(final HeaderOption headerOption : request.getHeaders())
                step.headers.putIfAbsent(headerOption.getName().toLowerCase(Locale.getDefault()), headerOption.getValue().toString());
        }
        if(step.body != null && step.body instanceof IJsonBackedObject &&
            (step.headers == null || !step.headers.containsKey(contentTypeHeaderKey))) {
            if(step.headers == null)
                step.headers = new HashMap<>();
            step.headers.putIfAbsent(contentTypeHeaderKey, "application/json");
        }
        requests.add(step);
        return step.id;
    }
    private static final String contentTypeHeaderKey = "content-type";
    /**
     * Removes requests from the requests to be executed by the batch. Also removes any dependency references that might exist.
     * @param stepIds ids of steps to be removed.
     */
    public void removeBatchRequestStepWithId(@Nonnull final String ...stepIds) {
        if(requests == null) return;

        for(final String stepId : stepIds) {
            Objects.requireNonNull(stepId, "parameter stepIds cannot contain null values");
            requests.removeIf(x -> stepId.equals(x.id));
            for(final BatchRequestStep<?> step : requests) {
                if(step.dependsOn != null) {
                    step.dependsOn.removeIf(stepId::equals);
                    if(step.dependsOn.isEmpty())
                        step.dependsOn = null; // so we don't send dependsOn: [] over the wire
                }
            }
        }
    }
    /**
     * Gets a step by its step id
     * @param <T> the type of the request body
     * @param stepId the request step id returned from the add method
     * @return the request corresponding to the provided id or null
     */
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
    /** pattern to replace the protocol and host part of the request if specified */
    @Nonnull
    protected static final Pattern protocolAndHostReplacementPattern =
    Pattern.compile("(?i)^http[s]?:\\/\\/(?:graph|dod-graph|microsoftgraph)\\.(?:microsoft|chinacloudapi)\\.(?:com|cn|us|de)\\/(?:v1\\.0|beta)"); // (?i) case insensitive
    //https://docs.microsoft.com/en-us/graph/deployments#microsoft-graph-and-graph-explorer-service-root-endpoints
    /**
     * Generates a randomly available request id
     * @return a random request id
     */
    @Nonnull
    protected String getNextRequestId() {
        String requestId;
        do {
            requestId = Integer.toString(ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE));
        } while(getStepById(requestId) != null);
        return requestId;
    }
}
