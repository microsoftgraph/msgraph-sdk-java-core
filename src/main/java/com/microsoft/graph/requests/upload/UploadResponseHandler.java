package com.microsoft.graph.requests.upload;

import com.microsoft.graph.exceptions.ErrorResponse;
import com.microsoft.graph.exceptions.Error;
import com.microsoft.graph.models.UploadResult;
import com.microsoft.graph.tasks.LargeFileUploadResult;
import com.microsoft.kiota.serialization.*;
import okhttp3.Response;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class UploadResponseHandler<T extends Parsable> {

    private final ParseNodeFactory _parseNodeFactory;

    public UploadResponseHandler(@Nullable ParseNodeFactory parseNodeFactory) {
        this._parseNodeFactory = (parseNodeFactory == null) ? ParseNodeFactoryRegistry.defaultInstance : parseNodeFactory;
    }

    public <T> CompletableFuture<UploadResult<T>> HandleResponse(Response response) throws IOException {
        if(response.body() == null) {
            //handle with error code
        }
        if(!response.isSuccessful()) {
            ParseNode jsonParseNode = _parseNodeFactory.getParseNode(response.body().contentType().type().toLowerCase(Locale.ROOT), response.body().byteStream());
            ErrorResponse errorResponse = jsonParseNode.<ErrorResponse>getObjectValue(ErrorResponse::createFromDiscriminatorValue);
            Error error = errorResponse.getError();
            String rawResponseBody = response.body().string();
        }


    }

}
