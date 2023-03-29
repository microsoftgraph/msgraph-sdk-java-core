package com.microsoft.graph.requests.upload;

import com.microsoft.graph.models.UploadResult;
import com.microsoft.kiota.*;
import com.microsoft.kiota.serialization.Parsable;
import com.microsoft.kiota.serialization.ParsableFactory;
import okhttp3.Response;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class UploadSliceRequestBuilder<T extends Parsable> {

    private UploadResponseHandler responseHandler;
    private RequestAdapter requestAdapter;
    private String urlTemplate;

    private long rangeBegin;
    private long rangeEnd;

    private long totalSessionLength;

    private int rangeLength;

    private ParsableFactory<T> factory;

    public UploadSliceRequestBuilder(@Nonnull String sessionUrl, @Nonnull RequestAdapter requestAdapter, @Nonnull ParsableFactory<T> factory, long rangeBegin, long rangeEnd, long totalSessionLength ) {

        this.urlTemplate = Objects.requireNonNull(sessionUrl);
        this.requestAdapter = Objects.requireNonNull(requestAdapter);
        this.factory = Objects.requireNonNull(factory);
        this.rangeBegin = rangeBegin;
        this.rangeEnd = rangeEnd;
        this.totalSessionLength = totalSessionLength;
        this.responseHandler = new UploadResponseHandler(null);
    }

    public CompletableFuture<UploadResult<T>> PutAsync(InputStream stream) {
        RequestInformation requestInformation = this.CreatePutRequestInformation(stream);
        ResponseHandlerOption responseHandlerOption = new ResponseHandlerOption();
        NativeResponseHandler nativeResponseHandler = new NativeResponseHandler();
        responseHandlerOption.setResponseHandler(nativeResponseHandler);
        ArrayList<RequestOption> option = new ArrayList<>(Arrays.asList(responseHandlerOption));
        requestInformation.addRequestOptions(option);

        return this.requestAdapter.sendPrimitiveAsync(requestInformation, InputStream.class, null)
            .thenCompose(i -> {
               try {
                   return (CompletableFuture<UploadResult<T>>)responseHandler.HandleResponse((Response) nativeResponseHandler.getValue(), factory);
               } catch (IOException ex) {
                   return new CompletableFuture<UploadResult<T>>() {{
                       this.completeExceptionally(ex);
                   }};
               } catch (URISyntaxException ex) {
                   return new CompletableFuture<UploadResult<T>>() {{
                       this.completeExceptionally(ex);
                   }};
               }
            });
    }

    public RequestInformation CreatePutRequestInformation(InputStream stream) {
        Objects.requireNonNull(stream);
        RequestInformation  requestInfo = new RequestInformation() {{
            httpMethod = HttpMethod.PUT;
            urlTemplate = this.urlTemplate;
        }};
        requestInfo.setStreamContent(stream);
        requestInfo.headers.add("Content-Range", "bytes " + String.format("bytes %f-%f/%f", this.rangeBegin, this.rangeEnd, this.totalSessionLength));
        requestInfo.headers.add("Content-Length", ""+this.rangeLength);
        return requestInfo;
    }
}
