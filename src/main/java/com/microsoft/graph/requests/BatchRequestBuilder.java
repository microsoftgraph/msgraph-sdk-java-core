package com.microsoft.graph.requests;

import com.microsoft.graph.CoreConstants;
import com.microsoft.graph.content.BatchRequestContent;
import com.microsoft.graph.content.BatchRequestContentCollection;
import com.microsoft.graph.content.BatchResponseContent;
import com.microsoft.graph.content.BatchResponseContentCollection;
import com.microsoft.graph.exceptions.ErrorConstants;
import com.microsoft.kiota.HttpMethod;
import com.microsoft.kiota.NativeResponseHandler;
import com.microsoft.kiota.RequestAdapter;
import com.microsoft.kiota.RequestInformation;
import com.microsoft.kiota.serialization.Parsable;
import com.microsoft.kiota.serialization.ParsableFactory;
import okhttp3.Response;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * A request builder for creating batch requests.
 */
public class BatchRequestBuilder {
    private String urlTemplate = "{+baseurl}/$batch";
    private RequestAdapter requestAdapter;
    /**
     * Instantiates a new BatchRequestBuilder.
     * @param requestAdapter the adapter to use to build requests.
     */
    public BatchRequestBuilder(@Nonnull RequestAdapter requestAdapter) {
        this.requestAdapter = Objects.requireNonNull(requestAdapter, String.format(Locale.US, ErrorConstants.Messages.NULL_PARAMETER, "requestAdapter"));
    }
    /**
     * Posts a batch request.
     * @param requestContent the batch request content.
     * @param errorMappings the error mappings to use when parsing the response.
     * @return the batch response content.
     */
    @Nonnull
    public CompletableFuture<BatchResponseContent> postAsync(@Nonnull BatchRequestContent requestContent, @Nullable HashMap<String, ParsableFactory<? extends Parsable>> errorMappings) {
        Objects.requireNonNull(requestContent, String.format(Locale.US, ErrorConstants.Messages.NULL_PARAMETER, "requestContent"));
        RequestInformation requestInfo = toPostRequestInformationAsync(requestContent).join();
        NativeResponseHandler nativeResponseHandler = new NativeResponseHandler();
        requestInfo.setResponseHandler(nativeResponseHandler);
        return requestAdapter.sendPrimitiveAsync(requestInfo, InputStream.class, errorMappings)
            .thenCompose(i -> CompletableFuture.completedFuture(new BatchResponseContent((Response) nativeResponseHandler.getValue(), errorMappings)));
    }
    /**
     * Posts a BatchRequestContentCollection.
     * @param batchRequestContentCollection the BatchRequestContentCollection to post.
     * @param errorMappings the error mappings to use when parsing the response.
     * @return the BatchResponseContentCollection.
     */
    @Nonnull
    public CompletableFuture<BatchResponseContentCollection> postAsync(@Nonnull BatchRequestContentCollection batchRequestContentCollection, @Nullable HashMap<String, ParsableFactory<? extends Parsable>> errorMappings) {
        BatchResponseContentCollection collection = new BatchResponseContentCollection();
        List<BatchRequestContent> requests = batchRequestContentCollection.getBatchRequestsForExecution();
        for (BatchRequestContent request : requests) {
            BatchResponseContent responseContent = postAsync(request, errorMappings).join();
            collection.addBatchResponse(request.getBatchRequestSteps().keySet(), responseContent);
        }
        return CompletableFuture.completedFuture(collection);
    }
    /**
     * Creates the request information for a batch request.
     * @param requestContent the batch request content.
     * @return the request information.
     */
    @Nonnull
    public CompletableFuture<RequestInformation> toPostRequestInformationAsync(@Nonnull BatchRequestContent requestContent) {
        Objects.requireNonNull(requestContent, String.format(Locale.US, ErrorConstants.Messages.NULL_PARAMETER, "requestContent"));
        RequestInformation requestInfo = new RequestInformation();
        requestInfo.httpMethod = HttpMethod.POST;
        requestInfo.urlTemplate = urlTemplate;
        requestInfo.content = requestContent.getBatchRequestContentAsync().join();
        requestInfo.headers.add("Content-Type", CoreConstants.MimeTypeNames.APPLICATION_JSON);
        return CompletableFuture.completedFuture(requestInfo);
    }
    /**
     * Gets the request adapter.
     * @return the request adapter.
     */
    @Nonnull
    public RequestAdapter getRequestAdapter() {
        return requestAdapter;
    }
    /**
     * Sets the request adapter.
     * @param requestAdapter the request adapter.
     */
    public void setRequestAdapter(@Nonnull RequestAdapter requestAdapter) {
        this.requestAdapter = requestAdapter;
    }
}
