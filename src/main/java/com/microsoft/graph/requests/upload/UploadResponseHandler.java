package com.microsoft.graph.requests.upload;

import com.microsoft.graph.HttpResponseCode;
import com.microsoft.graph.exceptions.ErrorResponse;
import com.microsoft.graph.exceptions.Error;
import com.microsoft.graph.models.UploadResult;
import com.microsoft.kiota.serialization.*;
import okhttp3.Response;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class UploadResponseHandler<T extends Parsable> {

    private final T uploadType;
    private final ParseNodeFactory _parseNodeFactory;

    public UploadResponseHandler(@Nullable ParseNodeFactory parseNodeFactory) {
        this._parseNodeFactory = (parseNodeFactory == null) ? ParseNodeFactoryRegistry.defaultInstance : parseNodeFactory;
    }

    public <T extends Parsable> CompletableFuture<UploadResult<T>> HandleResponse(Response response) throws IOException, NoSuchMethodException, SecurityException {
        if(response.body() == null) {
            //handle with error code
        }

        InputStream responseStream = response.body().byteStream();

        if(!response.isSuccessful()) {
            ParseNode jsonParseNode = _parseNodeFactory.getParseNode(response.body().contentType().type().toLowerCase(Locale.ROOT), response.body().byteStream());
            ErrorResponse errorResponse = jsonParseNode.getObjectValue(ErrorResponse::createFromDiscriminatorValue);
            Error error = errorResponse.getError();
            String rawResponseBody = response.body().string();
            //TODO: throw service exception, create service exception class
        }

        UploadResult uploadResult = new UploadResult<T>();

        if(response.code() == HttpResponseCode.HTTP_CREATED)
        {
            if(response.body().contentLength() > 0) {
                ParseNode jsonParseNode = _parseNodeFactory.getParseNode(response.body().contentType().type().toLowerCase(Locale.ROOT), responseStream);
                uploadResult.ItemResponse = jsonParseNode.getObjectValue(this::create);
                //ParsableFactory<JsonParseNode> emptyParsable;
                //uploadResult.ItemResponse = create(jsonParseNode);

            }
        }
    }

    private T create(ParseNode parseNode) {
        Objects.requireNonNull(parseNode);
        String nodeType = parseNode.
        Class.forName("name").getDeclaredConstructor().newInstance();
        try {
            Class<T> inst = null;
            T obj = inst.getDeclaredConstructor().newInstance();
            return obj;
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
        return null;
    }

//    @Nonnull
//    private LargeFileUploadResponse<UploadType> parseJsonUploadResult(@Nonnull final ResponseBody responseBody, @Nonnull final ISerializer serializer, @Nonnull final ILogger logger) throws IOException {
//        try (final InputStream in = responseBody.byteStream()) {
//            final byte[] responseBytes = ByteStreams.toByteArray(in);
//            final IUploadSession session = serializer.deserializeObject(new ByteArrayInputStream(responseBytes), uploadSessionClass);
//
//            if (session == null || session.getNextExpectedRanges() == null) {
//                logger.logDebug("Upload session is completed (ODSP), uploaded item returned.");
//                final UploadType uploadedItem = serializer.deserializeObject(new ByteArrayInputStream(responseBytes), this.deserializeTypeClass);
//                return new LargeFileUploadResponse<>(uploadedItem);
//            } else {
//                logger.logDebug("Chunk bytes has been accepted by the server.");
//                return new LargeFileUploadResponse<>(session);
//            }
//        }
//    }
}

