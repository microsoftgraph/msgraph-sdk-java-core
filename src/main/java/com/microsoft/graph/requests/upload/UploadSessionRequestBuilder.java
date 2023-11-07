package com.microsoft.graph.requests.upload;

import com.microsoft.graph.models.IUploadSession;
import com.microsoft.graph.models.UploadResult;
import com.microsoft.kiota.*;
import com.microsoft.kiota.serialization.Parsable;
import com.microsoft.kiota.serialization.ParsableFactory;
import okhttp3.Response;

import jakarta.annotation.Nonnull;
import java.io.InputStream;
import java.util.Objects;

/**
 * UploadSessionRequestBuilder class to get and delete an UploadSession.
 * @param <T> The type of object being uploaded.
 */
public class UploadSessionRequestBuilder<T extends Parsable> {

    private final UploadResponseHandler responseHandler;
    private final RequestAdapter requestAdapter;
    private final String urlTemplate;
    private final ParsableFactory<T> factory;
    /**
     * Create a new UploadSessionRequest.
     * @param sessionUrl The uploadSession url to use in the request.
     * @param requestAdapter The RequestAdapted to execute the request.
     * @param factory The ParsableFactory defining the instantiation of the object being uploaded.
     */
    public UploadSessionRequestBuilder(@Nonnull String sessionUrl,
                                       @Nonnull final RequestAdapter requestAdapter,
                                       @Nonnull final ParsableFactory<T> factory) {
        this.responseHandler = new UploadResponseHandler();
        this.requestAdapter = Objects.requireNonNull(requestAdapter);
        if(Compatibility.isBlank(sessionUrl))
        {
            throw new IllegalArgumentException("sessionUrl cannot be null or empty");
        }
        this.urlTemplate = sessionUrl;
        this.factory = Objects.requireNonNull(factory);
    }
    /**
     * Gets the specified UploadSession.
     * @return the IUploadSession
     */
    @Nonnull
    public IUploadSession get() {
        RequestInformation requestInformation = toGetRequestInformation();
        NativeResponseHandler nativeResponseHandler = new NativeResponseHandler();
        requestInformation.setResponseHandler(nativeResponseHandler);
        requestAdapter.sendPrimitive(requestInformation, InputStream.class, null);
        UploadResult<T> result = responseHandler.handleResponse((Response) nativeResponseHandler.getValue(), factory);
        return result.uploadSession;
    }
    private RequestInformation toGetRequestInformation() {
        RequestInformation requestInformation = new RequestInformation();
        requestInformation.httpMethod = HttpMethod.GET;
        requestInformation.urlTemplate = this.urlTemplate;
        return requestInformation;
    }
    /**
     * Deletes the specified UploadSession.
     */
    public void delete() {
        RequestInformation requestInfo = this.toDeleteRequestInformation();
        this.requestAdapter.sendPrimitive(requestInfo, Void.class, null);
    }
    private RequestInformation toDeleteRequestInformation() {
        RequestInformation requestInformation = new RequestInformation();
        requestInformation.httpMethod = HttpMethod.DELETE;
        requestInformation.urlTemplate = this.urlTemplate;
        return requestInformation;
    }
}
