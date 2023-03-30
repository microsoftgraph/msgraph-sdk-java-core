package com.microsoft.graph.requests.upload;

import com.microsoft.graph.models.IUploadSession;
import com.microsoft.graph.models.UploadResult;
import com.microsoft.kiota.*;
import com.microsoft.kiota.serialization.Parsable;
import com.microsoft.kiota.serialization.ParsableFactory;
import okhttp3.Response;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class UploadSessionRequestBuilder<T extends Parsable> {

    private UploadResponseHandler responseHandler;
    private RequestAdapter requestAdapter;
    private String urlTemplate;
    private ParsableFactory<T> factory;

    public UploadSessionRequestBuilder(IUploadSession uploadSession, RequestAdapter requestAdapter, @Nonnull final ParsableFactory<T> factory) {
        this.responseHandler = new UploadResponseHandler(null);
        this.requestAdapter = requestAdapter;
        this.urlTemplate = uploadSession.getUploadUrl();
        this.factory = factory;
    }

    public RequestInformation CreateGetRequestInformation() {
        RequestInformation requestInformation = new RequestInformation() {{
            httpMethod = HttpMethod.GET;
            urlTemplate = this.urlTemplate;
        }};
        return requestInformation;
    }

    public CompletableFuture<IUploadSession> GetAsync() {
        RequestInformation requestInformation = CreateGetRequestInformation();
        NativeResponseHandler nativeResponseHandler = new NativeResponseHandler();
        requestInformation.setResponseHandler(nativeResponseHandler);
        return this.requestAdapter.sendPrimitiveAsync(requestInformation, Void.class, null)
            .thenCompose( i -> {
                CompletableFuture<UploadResult<T>> result = null;
                try {
                     return responseHandler.HandleResponse((Response) nativeResponseHandler.getValue(), factory);
                } catch (IOException ex) {
                    return new CompletableFuture<UploadResult>() {{
                        this.completeExceptionally(ex);
                    }};
                } catch (URISyntaxException ex) {
                    return new CompletableFuture<UploadResult>() {{
                        this.completeExceptionally(ex);
                    }};
                }
            }).thenCompose(result -> (CompletableFuture<IUploadSession>)result.UploadSession);
    }

    public RequestInformation CreateDeleteRequestInformation() {
        RequestInformation requestInformation = new RequestInformation() {{
            httpMethod = HttpMethod.DELETE;
            urlTemplate = this.urlTemplate;
        }};
        return requestInformation;
    }

    public CompletableFuture<Void> DeleteAsync() {
        RequestInformation requestInfo = this.CreateDeleteRequestInformation();
        return this.requestAdapter.sendPrimitiveAsync(requestInfo, Void.class, null);
    }
}
