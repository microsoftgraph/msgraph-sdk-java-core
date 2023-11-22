package com.microsoft.graph.requests.upload;

import com.microsoft.graph.models.UploadResult;
import com.microsoft.kiota.*;
import com.microsoft.kiota.serialization.Parsable;
import com.microsoft.kiota.serialization.ParsableFactory;
import okhttp3.Response;

import jakarta.annotation.Nonnull;
import java.io.InputStream;
import java.util.Locale;
import java.util.Objects;

/**
 * Request for uploading a slice of a large file.
 * @param <T> The type of the object being uploaded.
 */
public class UploadSliceRequestBuilder<T extends Parsable> {

    private final UploadResponseHandler responseHandler;
    private final RequestAdapter requestAdapter;
    private final String urlTemplate;
    private final long rangeBegin;
    private final long rangeEnd;
    private final long totalSessionLength;
    private final long rangeLength;
    private final ParsableFactory<T> factory;

    /**
     * Request for uploading one slice of a session.
     * @param sessionUrl URL to upload the slice.
     * @param requestAdapter Request adapted used for uploading the slice.
     * @param rangeBegin Beginning of the range for this slice.
     * @param rangeEnd End of the range for this slice.
     * @param totalSessionLength Total session length. This MUST be consistent.
     * @param factory The ParsableFactory defining the instantiation of the object being uploaded.
     */
    public UploadSliceRequestBuilder(@Nonnull String sessionUrl,
                                     @Nonnull final RequestAdapter requestAdapter,
                                     long rangeBegin,
                                     long rangeEnd,
                                     long totalSessionLength,
                                     @Nonnull ParsableFactory<T> factory) {
        if(Compatibility.isBlank(sessionUrl))
        {
            throw new IllegalArgumentException("sessionUrl cannot be null or empty");
        }
        this.urlTemplate = sessionUrl;
        this.requestAdapter = Objects.requireNonNull(requestAdapter);
        this.factory = factory;
        this.rangeBegin = rangeBegin;
        this.rangeEnd = rangeEnd;
        this.rangeLength = (rangeEnd-rangeBegin+1);
        this.totalSessionLength = totalSessionLength;
        this.responseHandler = new UploadResponseHandler();
    }
    /**
     * Uploads the slice using PUT.
     * @param stream The stream of data to be uploaded.
     * @return The model containing the Upload information retrieved from the response.
     */
    @Nonnull
    public UploadResult<T> put(@Nonnull InputStream stream) {
        Objects.requireNonNull(stream);
        RequestInformation requestInformation = this.toPutRequestInformation(stream);
        NativeResponseHandler nativeResponseHandler = new NativeResponseHandler();
        requestInformation.setResponseHandler(nativeResponseHandler);
        requestAdapter.sendPrimitive(requestInformation,null, InputStream.class);
        return responseHandler.handleResponse((Response) nativeResponseHandler.getValue(), factory);
    }
    private RequestInformation toPutRequestInformation(InputStream stream) {
        Objects.requireNonNull(stream);
        RequestInformation  requestInfo = new RequestInformation();
        requestInfo.httpMethod = HttpMethod.PUT;
        requestInfo.urlTemplate = this.urlTemplate;
        requestInfo.setStreamContent(stream,"application/octet-stream");
        requestInfo.headers.add("Content-Range", String.format(Locale.US, "bytes %d-%d/%d", this.rangeBegin, this.rangeEnd, this.totalSessionLength));
        requestInfo.headers.add("Content-Length", ""+this.rangeLength);
        return requestInfo;
    }
    /**
     * Get the range of bytes for this slice.
     * @return the range of bytes in this slice.
     */
    public long getRangeLength() {
        return rangeLength;
    }
    /**
     * Get the starting byte position for this upload slice.
     * @return The byte position where this upload slice begins.
     */
    public long getRangeBegin() {
        return rangeBegin;
    }
    /**
     * Get the ending byte position for this upload slice.
     * @return the position where this upload slice ends.
     */
    public long getRangeEnd() {
        return rangeEnd;
    }
    /**
     * Get the total number of bytes being uploaded in the session.
     * @return The total number of bytes in this upload session.
     */
    public long getTotalSessionLength() {
        return totalSessionLength;
    }
}
