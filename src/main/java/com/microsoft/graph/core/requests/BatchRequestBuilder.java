package com.microsoft.graph.core.requests;

import com.microsoft.graph.core.CoreConstants;
import com.microsoft.graph.core.content.BatchRequestContent;
import com.microsoft.graph.core.content.BatchRequestContentCollection;
import com.microsoft.graph.core.content.BatchResponseContent;
import com.microsoft.graph.core.content.BatchResponseContentCollection;
import com.microsoft.graph.core.ErrorConstants;
import com.microsoft.kiota.HttpMethod;
import com.microsoft.kiota.NativeResponseHandler;
import com.microsoft.kiota.RequestAdapter;
import com.microsoft.kiota.RequestInformation;
import com.microsoft.kiota.serialization.Parsable;
import com.microsoft.kiota.serialization.ParsableFactory;
import okhttp3.Response;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A request builder for creating batch requests.
 */
public class BatchRequestBuilder {
    private final RequestAdapter requestAdapter;
    /**
     * Instantiates a new BatchRequestBuilder.
     * @param requestAdapter the adapter to use to build requests.
     */
    public BatchRequestBuilder(@Nonnull RequestAdapter requestAdapter) {
        this.requestAdapter = Objects.requireNonNull(requestAdapter, ErrorConstants.Messages.NULL_PARAMETER + "requestAdapter");
    }
    /**
     * Posts a batch request.
     * @param requestContent the batch request content.
     * @param errorMappings the error mappings to use when parsing the response.
     * @return the batch response content.
     * @throws IOException if there was an error writing the request content.
     */
    @Nonnull
    public BatchResponseContent post(@Nonnull BatchRequestContent requestContent, @Nullable Map<String, ParsableFactory<? extends Parsable>> errorMappings) throws IOException {
        Objects.requireNonNull(requestContent, ErrorConstants.Messages.NULL_PARAMETER + "requestContent");
        RequestInformation requestInfo = toPostRequestInformation(requestContent);
        NativeResponseHandler nativeResponseHandler = new NativeResponseHandler();
        requestInfo.setResponseHandler(nativeResponseHandler);
        requestAdapter.sendPrimitive(requestInfo, errorMappings == null ? null : new HashMap<>(errorMappings) ,InputStream.class);
        return new BatchResponseContent((Response) nativeResponseHandler.getValue(), errorMappings);
    }
    /**
     * Posts a BatchRequestContentCollection.
     * @param batchRequestContentCollection the BatchRequestContentCollection to post.
     * @param errorMappings the error mappings to use when parsing the response.
     * @return the BatchResponseContentCollection.
     * @throws IOException if there was an error writing the request content.
     */
    @Nonnull
    public BatchResponseContentCollection post(@Nonnull BatchRequestContentCollection batchRequestContentCollection, @Nullable Map<String, ParsableFactory<? extends Parsable>> errorMappings) throws IOException {
        BatchResponseContentCollection collection = new BatchResponseContentCollection();
        List<BatchRequestContent> requests = batchRequestContentCollection.getBatchRequestsForExecution();
        for (BatchRequestContent request : requests) {
            BatchResponseContent responseContent = post(request, errorMappings);
            collection.addBatchResponse(request.getBatchRequestSteps().keySet(), responseContent);
        }
        return collection;
    }
    /**
     * Creates the request information for a batch request.
     * @param requestContent the batch request content.
     * @return the request information.
     * @throws IOException if there was an error writing the request content.
     */
    @Nonnull
    public RequestInformation toPostRequestInformation(@Nonnull BatchRequestContent requestContent) throws IOException {
        Objects.requireNonNull(requestContent, ErrorConstants.Messages.NULL_PARAMETER + "requestContent");
        RequestInformation requestInfo = new RequestInformation();
        requestInfo.httpMethod = HttpMethod.POST;
        requestInfo.urlTemplate = "{+baseurl}/$batch";
        requestInfo.content = requestContent.getBatchRequestContent();
        requestInfo.headers.add("Content-Type", CoreConstants.MimeTypeNames.APPLICATION_JSON);
        return requestInfo;
    }
    /**
     * Gets the request adapter.
     * @return the request adapter.
     */
    @Nonnull
    public RequestAdapter getRequestAdapter() {
        return requestAdapter;
    }
}
