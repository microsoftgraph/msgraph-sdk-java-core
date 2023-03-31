package com.microsoft.graph.requests.upload;

import com.microsoft.graph.models.UploadResult;
import com.microsoft.kiota.*;
import com.microsoft.kiota.serialization.Parsable;
import com.microsoft.kiota.serialization.ParsableFactory;
import okhttp3.Response;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class UploadSliceRequestBuilder<T extends Parsable> {

    private final UploadResponseHandler responseHandler;
    private final RequestAdapter requestAdapter;
    private final String urlTemplate;

    private final long rangeBegin;
    private final long rangeEnd;
    private final long totalSessionLength;

    private final int rangeLength;

    private final ParsableFactory<T> factory;

    public UploadSliceRequestBuilder(@Nonnull String sessionUrl,
                                     @Nonnull RequestAdapter requestAdapter,
                                     long rangeBegin,
                                     long rangeEnd,
                                     long totalSessionLength,
                                     @Nonnull ParsableFactory<T> factory) {
        this.urlTemplate = Objects.requireNonNull(sessionUrl);
        this.requestAdapter = Objects.requireNonNull(requestAdapter);
        this.factory = factory;
        this.rangeBegin = rangeBegin;
        this.rangeEnd = rangeEnd;
        this.rangeLength = (int) (rangeEnd-rangeBegin+1);
        this.totalSessionLength = totalSessionLength;
        this.responseHandler = new UploadResponseHandler(null);
    }
    public CompletableFuture<UploadResult<T>> putAsync(InputStream stream) {
        RequestInformation requestInformation = this.createPutRequestInformation(stream);
        NativeResponseHandler nativeResponseHandler = new NativeResponseHandler();
        requestInformation.setResponseHandler(nativeResponseHandler);
        return this.requestAdapter.sendPrimitiveAsync(requestInformation, Void.class, null)
            .thenCompose(i -> responseHandler.handleResponse((Response) nativeResponseHandler.getValue(), factory));
    }
    public RequestInformation createPutRequestInformation(InputStream stream) {
        Objects.requireNonNull(stream);
        RequestInformation  requestInfo = new RequestInformation();
        requestInfo.httpMethod = HttpMethod.PUT;
        requestInfo.urlTemplate = this.urlTemplate;
        requestInfo.setStreamContent(stream);
        requestInfo.headers.add("Content-Range", String.format(Locale.US, "bytes %d-%d/%d", this.rangeBegin, this.rangeEnd, this.totalSessionLength));
        requestInfo.headers.add("Content-Length", ""+this.rangeLength);
        return requestInfo;
    }

    public int getRangeLength() {
        return rangeLength;
    }

    public long getRangeBegin() {
        return rangeBegin;
    }

    public long getRangeEnd() {
        return rangeEnd;
    }

    public long getTotalSessionLength() {
        return totalSessionLength;
    }
}
