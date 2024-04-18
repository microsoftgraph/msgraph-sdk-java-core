package com.microsoft.graph.core.requests.upload;

import com.microsoft.graph.core.ErrorConstants;
import com.microsoft.graph.core.models.UploadResult;
import com.microsoft.graph.core.models.UploadSession;
import com.microsoft.kiota.ApiException;
import com.microsoft.kiota.ApiExceptionBuilder;
import com.microsoft.kiota.http.HeadersCompatibility;
import com.microsoft.kiota.serialization.*;
import okhttp3.Response;
import okhttp3.ResponseBody;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;

/**
 * The request handler for upload requests.
 */
public class UploadResponseHandler {

    private final ParseNodeFactory parseNodeFactory;
    /**
     * UploadResponseHandler constructor.
     */
    public UploadResponseHandler() {
        this(null);
    }
    /**
     * UploadResponseHandler constructor.
     * @param parseNodeFactory The ParseNodeFactory to use for response parsing.
     */
    public UploadResponseHandler(@Nullable ParseNodeFactory parseNodeFactory) {
        this.parseNodeFactory = (parseNodeFactory == null) ? ParseNodeFactoryRegistry.defaultInstance : parseNodeFactory;
    }
    /**
     * Process the raw HTTP response from an upload request.
     * @param response The HTTP response returned from the upload request.
     * @param factory The ParsableFactory defining the instantiation of the object being uploaded.
     * @param <T> The type of the object being uploaded.
     * @return An UploadResult model containing the information from the server resulting from the upload request.
     */
    @Nonnull
    public <T extends Parsable> UploadResult<T> handleResponse(@Nonnull final Response response, @Nonnull final ParsableFactory<T> factory) {
        Objects.requireNonNull(response);
        Objects.requireNonNull(factory);
        try (final ResponseBody body = response.body()) {
            UploadResult<T> uploadResult = new UploadResult<>();
            if (Objects.isNull(body)) {
                if (response.code() == HttpURLConnection.HTTP_CREATED) {
                    final String location = response.headers().get("location");
                    if(!Objects.isNull(location) && !location.isEmpty()) {
                        uploadResult.location = new URI(location);
                        return uploadResult;
                    }
                }
                throw new ApiException(ErrorConstants.Messages.NO_RESPONSE_FOR_UPLOAD);
            }
            try(final InputStream in = body.byteStream()){
                final String contentType = body.contentType().toString().split(";")[0]; //contentType.toString() returns in format <mediaType>;<charset>, we only want the mediaType.
                if(!response.isSuccessful()) {
                    throw new ApiExceptionBuilder()
                            .withMessage(ErrorConstants.Codes.GENERAL_EXCEPTION)
                            .withResponseStatusCode(response.code())
                            .withResponseHeaders(HeadersCompatibility.getResponseHeaders(response.headers()))
                            .build();
                }
                if (response.code() == HttpURLConnection.HTTP_CREATED) {
                    if (body.contentLength() > 0) {
                        final ParseNode uploadTypeParseNode = parseNodeFactory.getParseNode(contentType, in);
                        uploadResult.itemResponse = uploadTypeParseNode.getObjectValue(factory);
                    }
                } else {
                    final ParseNode parseNode = parseNodeFactory.getParseNode(contentType, in);
                    final UploadSession uploadSession = parseNode.getObjectValue(UploadSession::createFromDiscriminatorValue);
                    final List<String> nextExpectedRanges = uploadSession.getNextExpectedRanges();
                    if (!(nextExpectedRanges == null || nextExpectedRanges.isEmpty())) {
                        uploadResult.uploadSession = uploadSession;
                    } else {
                        uploadResult.itemResponse = parseNode.getObjectValue(factory);
                    }
                }
                return uploadResult;
            }
        }
        catch(IOException | URISyntaxException ex) {
            throw new RuntimeException(ex);
        }
    }
}


