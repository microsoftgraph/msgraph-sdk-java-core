package com.microsoft.graph.requests.upload;

import com.microsoft.graph.models.IUploadSession;
import com.microsoft.kiota.*;
import com.microsoft.kiota.serialization.Parsable;
import com.microsoft.kiota.serialization.ParsableFactory;
import okhttp3.Response;

import javax.annotation.Nonnull;
import java.io.InputStream;
import java.util.concurrent.CompletableFuture;

public class UploadSessionRequestBuilder<T extends Parsable> {

    private final UploadResponseHandler responseHandler;
    private final RequestAdapter requestAdapter;
    private final String urlTemplate;
    private final ParsableFactory<T> factory;

    public UploadSessionRequestBuilder(@Nonnull IUploadSession uploadSession,
                                       @Nonnull RequestAdapter requestAdapter,
                                       @Nonnull final ParsableFactory<T> factory) {
        this.responseHandler = new UploadResponseHandler(null);
        this.requestAdapter = requestAdapter;
        this.urlTemplate = uploadSession.getUploadUrl();
        this.factory = factory;
    }

    public RequestInformation createGetRequestInformation() {
        RequestInformation requestInformation = new RequestInformation();
        requestInformation.httpMethod = HttpMethod.GET;
        requestInformation.urlTemplate = this.urlTemplate;
        return requestInformation;
    }

    public CompletableFuture<IUploadSession> getAsync() {
        RequestInformation requestInformation = createGetRequestInformation();
        NativeResponseHandler nativeResponseHandler = new NativeResponseHandler();
        requestInformation.setResponseHandler(nativeResponseHandler);
        return this.requestAdapter.sendPrimitiveAsync(requestInformation, InputStream.class, null)
            .thenCompose( i -> responseHandler.handleResponse((Response) nativeResponseHandler.getValue(), factory))
            .thenCompose(result -> CompletableFuture.completedFuture(result.uploadSession));
    }

    public RequestInformation createDeleteRequestInformation() {
        RequestInformation requestInformation = new RequestInformation();
        requestInformation.httpMethod = HttpMethod.DELETE;
        requestInformation.urlTemplate = this.urlTemplate;
        return requestInformation;
    }

    public CompletableFuture<Void> deleteAsync() {
        RequestInformation requestInfo = this.createDeleteRequestInformation();
        return this.requestAdapter.sendPrimitiveAsync(requestInfo, Void.class, null);
    }
}
