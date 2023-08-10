package com.microsoft.graph.requests;

import com.microsoft.graph.exceptions.ErrorConstants;
import com.microsoft.graph.exceptions.ServiceException;
import com.microsoft.kiota.ApiException;
import com.microsoft.kiota.serialization.*;

import okhttp3.Response;
import okhttp3.ResponseBody;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.io.*;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

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
     * @return A CompletableFuture that represents the asynchronous operation and contains the deserialized response.
     * @param <NativeResponseType> The type of the native response object.
     * @param <ModelType> The type of the response model object.
     */
    @Nonnull
    @Override
    public <NativeResponseType, ModelType> CompletableFuture<ModelType> handleResponseAsync(@Nonnull NativeResponseType response, @Nullable HashMap<String, ParsableFactory<? extends Parsable>> errorMappings) {
        if(response instanceof Response && ((Response) response).body()!=null) {
            Response nativeResponse = (Response) response;
            ResponseBody body = nativeResponse.body();
            try(final InputStream in = body.byteStream()) {
                ParseNode parseNode = this.parseNodeFactory.getParseNode(body.contentType().type() + "/" + body.contentType().subtype(), in);
                body.close();
                if(nativeResponse.isSuccessful()) {
                    final ModelType result = (ModelType) parseNode.getObjectValue(this.factory); //We can be sure this is the correct type since return of this method is based on the type of the factory.
                    return CompletableFuture.completedFuture(result);
                } else {
                    handleFailedResponse(nativeResponse, errorMappings, parseNode);
                }
            }
            catch(ApiException | IOException ex) {
                CompletableFuture<ModelType> exceptionalResult = new CompletableFuture<>();
                exceptionalResult.completeExceptionally(ex);
                return exceptionalResult;
            }
        } else {
            throw new IllegalArgumentException("The provided response type is not supported by this response handler.");
        }
        return CompletableFuture.completedFuture(null);
    }

    private void handleFailedResponse(Response nativeResponse, HashMap<String, ParsableFactory<? extends Parsable>> errorMappings, ParseNode parseNode) throws ApiException {
        int statusCode = nativeResponse.code();
        String statusCodeString = String.valueOf(statusCode);
        if (errorMappings == null ||
            !errorMappings.containsKey(statusCodeString) ||
            !(statusCode >= 400 && statusCode <= 499 && errorMappings.containsKey("4XX")) &&
                !(statusCode >= 500 && statusCode <= 599 && errorMappings.containsKey("5XX"))) {
            throw new ServiceException(ErrorConstants.Codes.GENERAL_EXCEPTION, null, statusCode, nativeResponse.headers(), nativeResponse.toString());

        } else {
            Parsable result = parseNode.getObjectValue(errorMappings.get(statusCodeString));
            if (!(result instanceof Exception)) {
                throw new ApiException("The server returned an unexpected status code and the error registered for this code failed to deserialize: " + statusCodeString);
            }
        }
    }
}
