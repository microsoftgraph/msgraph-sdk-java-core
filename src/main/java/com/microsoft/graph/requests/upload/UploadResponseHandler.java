package com.microsoft.graph.requests.upload;

import com.google.common.io.ByteStreams;
import com.microsoft.graph.exceptions.ErrorConstants;
import com.microsoft.graph.exceptions.ServiceException;
import com.microsoft.graph.models.UploadResult;
import com.microsoft.graph.models.UploadSession;
import com.microsoft.kiota.serialization.*;
import okhttp3.Response;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

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
    public <T extends Parsable> CompletableFuture<UploadResult<T>> handleResponse(@Nonnull final Response response, @Nonnull final ParsableFactory<T> factory) {
        Objects.requireNonNull(response);
        Objects.requireNonNull(factory);
        if (Objects.isNull(response.body())) {
            ServiceException ex = new ServiceException(ErrorConstants.Messages.NO_RESPONSE_FOR_UPLOAD);
            CompletableFuture<UploadResult<T>> exceptionalResult = new CompletableFuture<>();
            exceptionalResult.completeExceptionally(ex);
            return exceptionalResult;
        }
        try(final InputStream in = Objects.requireNonNull(response.body()).byteStream()){
            String[] contentType = response.body().contentType().toString().split(";"); //contentType.toString() returns in format <mediaType>;<charset>, we only want the mediaType.
            byte[] responseStream = ByteStreams.toByteArray(in);
            if(!response.isSuccessful()) {
                String rawResponseBody = new String(responseStream, StandardCharsets.UTF_8);
                ServiceException ex = new ServiceException(ErrorConstants.Codes.GENERAL_EXCEPTION, null, response.code(), response.headers(), rawResponseBody);
                CompletableFuture<UploadResult<T>> exceptionalResult = new CompletableFuture<>();
                exceptionalResult.completeExceptionally(ex);
                return exceptionalResult;
            }
            UploadResult<T> uploadResult = new UploadResult<>();
            if (response.code() == HttpURLConnection.HTTP_CREATED) {
                if (Objects.requireNonNull(response.body()).contentLength() > 0) {
                    ParseNode uploadTypeParseNode = parseNodeFactory.getParseNode(contentType[0], new ByteArrayInputStream(responseStream));
                    uploadResult.itemResponse = uploadTypeParseNode.getObjectValue(factory);
                }
                if(!Objects.isNull(response.headers().get("location"))) {
                    uploadResult.location = new URI(Objects.requireNonNull(response.headers().get("location")));
                }
            } else {
                ParseNode uploadSessionParseNode = parseNodeFactory.getParseNode(contentType[0], new ByteArrayInputStream(responseStream));
                UploadSession uploadSession = uploadSessionParseNode.getObjectValue(UploadSession::createFromDiscriminatorValue);
                if (!uploadSession.getNextExpectedRanges().isEmpty()) {
                    uploadResult.uploadSession = uploadSession;
                } else {
                    ParseNode objectParseNode = parseNodeFactory.getParseNode(contentType[0], new ByteArrayInputStream(responseStream));
                    uploadResult.itemResponse = objectParseNode.getObjectValue(factory);
                }
            }
            return CompletableFuture.completedFuture(uploadResult);
        }
        catch(IOException | URISyntaxException ex) {
            CompletableFuture<UploadResult<T>> exceptionalResult = new CompletableFuture<>();
            exceptionalResult.completeExceptionally(ex);
            return exceptionalResult;
        }
    }
}


