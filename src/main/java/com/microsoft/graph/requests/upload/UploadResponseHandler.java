package com.microsoft.graph.requests.upload;

import com.google.common.io.ByteStreams;
import com.microsoft.graph.exceptions.ErrorConstants;
import com.microsoft.graph.exceptions.ServiceException;
import com.microsoft.graph.models.UploadResult;
import com.microsoft.graph.models.UploadSession;
import com.microsoft.kiota.serialization.*;
import okhttp3.Response;

import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class UploadResponseHandler {

    private final ParseNodeFactory _parseNodeFactory;

    public UploadResponseHandler(@Nullable ParseNodeFactory parseNodeFactory) {
        this._parseNodeFactory = (parseNodeFactory == null) ? ParseNodeFactoryRegistry.defaultInstance : parseNodeFactory;
    }

    @Nonnull
    public <T extends Parsable> CompletableFuture<UploadResult<T>> handleResponse(@Nonnull final Response response, @Nonnull final ParsableFactory<T> factory) {
        if (response.body() == null) {
            ServiceException ex = new ServiceException(ErrorConstants.Messages.NoResponseForUpload, null);
            return new CompletableFuture<UploadResult<T>>() {{
               completeExceptionally(ex);
            }};
        }
        String contentType[] = response.body().contentType().toString().split(";"); //contentType.toString() returns in format <mediaType>;<charset>, we only want the mediaType.
        try(final InputStream in = response.body().byteStream()){
            byte[] responseStream = ByteStreams.toByteArray(in);
            if(!response.isSuccessful()) {
                String rawResponseBody = new String(responseStream, 0, responseStream.length);
                ServiceException ex = new ServiceException(ErrorConstants.Codes.GeneralException, response.headers(), response.code(), rawResponseBody, null);
                return new CompletableFuture<UploadResult<T>>() {{
                    completeExceptionally(ex);
                }};
            }
            UploadResult<T> uploadResult = new UploadResult<>();
            if (response.code() == HttpURLConnection.HTTP_CREATED) {
                if (response.body().contentLength() > 0) {
                    ParseNode uploadTypeParseNode = _parseNodeFactory.getParseNode(contentType[0], new ByteArrayInputStream(responseStream));
                    uploadResult.itemResponse = uploadTypeParseNode.getObjectValue(factory);
                }
                if(!Objects.isNull(response.headers().get("location"))) {
                    uploadResult.location = new URI(response.headers().get("location"));
                }
            } else {
                ParseNode uploadSessionParseNode = _parseNodeFactory.getParseNode(contentType[0], new ByteArrayInputStream(responseStream));
                UploadSession uploadSession = uploadSessionParseNode.getObjectValue(UploadSession::createFromDiscriminatorValue);
                if (!uploadSession.getNextExpectedRanges().isEmpty()) {
                    uploadResult.uploadSession = uploadSession;
                } else {
                    ParseNode objectParseNode = _parseNodeFactory.getParseNode(contentType[0], new ByteArrayInputStream(responseStream));
                    uploadResult.itemResponse = objectParseNode.getObjectValue(factory);
                }
            }
            return CompletableFuture.completedFuture(uploadResult);
        }
        catch(IOException | URISyntaxException ex) {
            return new CompletableFuture<UploadResult<T>>() {{
                this.completeExceptionally(ex);
            }};
        }
    }
}


