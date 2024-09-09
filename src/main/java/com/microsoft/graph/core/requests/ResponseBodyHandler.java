package com.microsoft.graph.core.requests;

import com.microsoft.graph.core.ErrorConstants;
import com.microsoft.kiota.ApiException;
import com.microsoft.kiota.ApiExceptionBuilder;
import com.microsoft.kiota.http.HeadersCompatibility;
import com.microsoft.kiota.serialization.*;

import okhttp3.Response;
import okhttp3.ResponseBody;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.io.*;
import java.util.HashMap;

/**
 * A response handler for deserializing responses to a ModelType. Particularly for Batch requests.
 * @param <T> the ModelType of the response body.
 */
public class ResponseBodyHandler<T extends Parsable> implements com.microsoft.kiota.ResponseHandler {
    private final ParseNodeFactory parseNodeFactory;
    private final ParsableFactory<T> factory;
    /**
     * Instantiates a new response handler.
     * @param parseNodeFactory the parse node factory to use when deserializing the response.
     * @param factory the factory to use when deserializing the response to a ModelType.
     */
    public ResponseBodyHandler(@Nullable ParseNodeFactory parseNodeFactory, @Nonnull ParsableFactory<T> factory) {
        this.parseNodeFactory = (parseNodeFactory == null) ? ParseNodeFactoryRegistry.defaultInstance : parseNodeFactory;
        this.factory = factory;
    }
    /**
     * Instantiates a new response handler.
     * @param factory the factory to use when deserializing the response to a ModelType.
     */
    public ResponseBodyHandler(@Nonnull ParsableFactory<T> factory) {
        this(null, factory);
    }
    /**
     * Handles the response and returns the deserialized response body as a ModelType.
     * @param response The native response object.
     * @param errorMappings the error mappings for the response to use when deserializing failed responses bodies. Where an error code like 401 applies specifically to that status code, a class code like 4XX applies to all status codes within the range if an the specific error code is not present.
     * @return the deserialized response.
     * @param <NativeResponseType> The type of the native response object.
     * @param <ModelType> The type of the response model object.
     */
    @Nullable
    @Override
    public <NativeResponseType, ModelType> ModelType handleResponse(@Nonnull NativeResponseType response, @Nullable HashMap<String, ParsableFactory<? extends Parsable>> errorMappings) {
        if(response instanceof Response && ((Response) response).body()!=null) {
            Response nativeResponse = (Response) response;
            ResponseBody body = nativeResponse.body();
            try(final InputStream in = body.byteStream()) {
                ParseNode parseNode = this.parseNodeFactory.getParseNode(body.contentType().type() + "/" + body.contentType().subtype(), in);
                body.close();
                if(nativeResponse.isSuccessful()) {
                    final ModelType result = (ModelType) parseNode.getObjectValue(this.factory); //We can be sure this is the correct type since return of this method is based on the type of the factory.
                    return result;
                } else {
                    handleFailedResponse(nativeResponse, errorMappings, parseNode);
                }
            }
            catch(IOException ex) {
                throw new RuntimeException(ex);
            }
        } else {
            throw new IllegalArgumentException("The provided response type is not supported by this response handler.");
        }
        return null;
    }

    private void handleFailedResponse(Response nativeResponse, HashMap<String, ParsableFactory<? extends Parsable>> errorMappings, ParseNode parseNode) {
        int statusCode = nativeResponse.code();
        String statusCodeString = String.valueOf(statusCode);
        if (errorMappings == null ||
            !errorMappings.containsKey(statusCodeString) &&
            !(statusCode >= 400 && statusCode <= 499 && errorMappings.containsKey("4XX")) &&
                !(statusCode >= 500 && statusCode <= 599 && errorMappings.containsKey("5XX"))) {
            throw new ApiExceptionBuilder()
                    .withMessage(ErrorConstants.Codes.GENERAL_EXCEPTION)
                    .withResponseStatusCode(statusCode)
                    .withResponseHeaders(HeadersCompatibility.getResponseHeaders(nativeResponse.headers()))
                    .build();
        } else {
            String statusCodePattern = statusCodeString;
            if (!errorMappings.containsKey(statusCodePattern)) {
                if (statusCode >= 400 && statusCode <= 499 && errorMappings.containsKey("4XX"))) {
                    statusCodePattern = "4XX";
                } else if (statusCode >= 500 && statusCode <= 599 && errorMappings.containsKey("5XX")) {
                    statusCodePattern = "5XX";
                }
            }
            Parsable result = parseNode.getObjectValue(errorMappings.get(statusCodePattern));
            if (!(result instanceof Exception)) {
                throw new ApiException("The server returned an unexpected status code and the error registered for this code failed to deserialize: " + statusCodeString);
            }
        }
    }
}
