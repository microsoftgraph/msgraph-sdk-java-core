package com.microsoft.graph.requests.upload;

import com.fasterxml.jackson.core.JsonParseException;
import com.microsoft.graph.HttpResponseCode;
import com.microsoft.graph.exceptions.ErrorResponse;
import com.microsoft.graph.exceptions.Error;
import com.microsoft.graph.models.UploadResult;
import com.microsoft.graph.models.UploadSession;
import com.microsoft.kiota.serialization.*;
import okhttp3.Response;

import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class UploadResponseHandler<T extends Parsable> {

    private final ParseNodeFactory _parseNodeFactory;

    public UploadResponseHandler(@Nullable ParseNodeFactory parseNodeFactory) {
        this._parseNodeFactory = (parseNodeFactory == null) ? ParseNodeFactoryRegistry.defaultInstance : parseNodeFactory;
    }

    public <T extends Parsable> CompletableFuture<UploadResult<T>> handleResponse(@Nonnull final Response response, @Nonnull final ParsableFactory<T> factory) throws IOException, URISyntaxException {
        if (response == null) {
            //handle with error code
        }
        String contentType = response.body().contentType().type().toLowerCase(Locale.ROOT);
        try {
            try(InputStream responseStream = response.body().byteStream()) {
                if (!response.isSuccessful()) {
                    ParseNode jsonParseNode = _parseNodeFactory.getParseNode(contentType, responseStream);
                    ErrorResponse errorResponse = jsonParseNode.getObjectValue(ErrorResponse::createFromDiscriminatorValue);
                    Error error = errorResponse.getError();
                    String rawResponseBody = response.body().string();
                    //TODO: throw service exception, create service exception class
                }

                UploadResult uploadResult = new UploadResult<T>();

                if (response.code() == HttpResponseCode.HTTP_CREATED) {
                    if (response.body().contentLength() > 0) {
                        ParseNode jsonParseNode = _parseNodeFactory.getParseNode(contentType, responseStream);
                        uploadResult.itemResponse = jsonParseNode.getObjectValue(factory);
                    }
                    uploadResult.location = new URI(Objects.requireNonNull(response.headers().get("location")));
                } else {
                    ParseNode uploadSessionParseNode = _parseNodeFactory.getParseNode(contentType, responseStream);
                    UploadSession uploadSession = uploadSessionParseNode.getObjectValue(UploadSession::createFromDiscriminatorValue);
                    if (uploadSession.getNextExpectedRanges() != null) {
                        uploadResult.uploadSession = uploadSession;
                    } else {
                        responseStream.reset();
                        ParseNode objectParseNode = _parseNodeFactory.getParseNode(contentType, responseStream);
                        uploadResult.itemResponse = objectParseNode.getObjectValue(factory::create);
                    }
                }
                return CompletableFuture.completedFuture(uploadResult);
            }
        } catch (JsonParseException ex) {
            return new CompletableFuture<UploadResult<T>>() {{
                this.completeExceptionally(ex);
            }};
        }
    }
}


