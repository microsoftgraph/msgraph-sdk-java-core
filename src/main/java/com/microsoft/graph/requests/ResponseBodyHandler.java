package com.microsoft.graph.requests;

import com.google.common.io.ByteStreams;
import com.microsoft.graph.exceptions.ErrorConstants;
import com.microsoft.graph.exceptions.ServiceException;
import com.microsoft.kiota.ApiException;
import com.microsoft.kiota.serialization.*;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import okhttp3.Response;
import okhttp3.ResponseBody;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * A response handler for deserializing responses to a ModelType. Particularly for Batch requests.
 * @param <T> the ModelType of the response body.
 */
public class ResponseBodyHandler<T extends Parsable> implements com.microsoft.kiota.ResponseHandler {
    private final ParseNodeFactory _parseNodeFactory;
    private final ParsableFactory<T> _factory;
    /**
     * Instantiates a new response handler.
     * @param parseNodeFactory the parse node factory to use when deserializing the response.
     * @param factory the factory to use when deserializing the response to a ModelType.
     */
    public ResponseBodyHandler(@Nullable ParseNodeFactory parseNodeFactory, @Nonnull ParsableFactory<T> factory) {
        this._parseNodeFactory = (parseNodeFactory == null) ? ParseNodeFactoryRegistry.defaultInstance : parseNodeFactory;
        this._factory = factory;
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
    @SuppressFBWarnings
    public <NativeResponseType, ModelType> CompletableFuture<ModelType> handleResponseAsync(@Nonnull NativeResponseType response, @Nullable HashMap<String, ParsableFactory<? extends Parsable>> errorMappings) {
        if(response instanceof Response && ((Response) response).body()!=null) {
            ResponseBody body = ((Response) response).body();
            if(validateSuccessfulResponse((Response)response, errorMappings).join()) {
                try(final InputStream in = body.byteStream()) {
                    String[] contentType = body.contentType().toString().split(";"); //contentType.toString() returns in format <mediaType>;<charset>, we only want the mediaType.
                    byte[] responseStream = ByteStreams.toByteArray(in);
                    ParseNode jsonParseNode = _parseNodeFactory.getParseNode(contentType[0], new ByteArrayInputStream(responseStream));
                    final ModelType result = (ModelType) jsonParseNode.getObjectValue(_factory); //We can be sure this is the correct type since return of this method is based on the type of the factory.
                    return CompletableFuture.completedFuture(result);
                } catch (IOException ex) {
                    CompletableFuture<ModelType> exceptionalResult = new CompletableFuture<>();
                    exceptionalResult.completeExceptionally(ex);
                    return exceptionalResult;
                }
            }
        } else {
            throw new IllegalArgumentException("The provided response type is not supported by this response handler.");
        }
        return CompletableFuture.completedFuture(null);
    }

    @SuppressFBWarnings
    private CompletableFuture<Boolean> validateSuccessfulResponse(Response responseMessage, HashMap<String, ParsableFactory<? extends Parsable>> errorMapping) {
        if (responseMessage.isSuccessful()) {
            return CompletableFuture.completedFuture(true);
        }
        int statusCode = responseMessage.code();
        String statusCodeString = String.valueOf(statusCode);
        try (final InputStream in = Objects.requireNonNull(responseMessage.body()).byteStream()) {
            String[] contentType = responseMessage.body().contentType().toString().split(";"); //contentType.toString() returns in format <mediaType>;<charset>, we only want the mediaType.
            byte[] responseStream = ByteStreams.toByteArray(in);
            String rawResponseBody = new String(responseStream, StandardCharsets.UTF_8);
            ParseNode node = _parseNodeFactory.getParseNode(contentType[0], new ByteArrayInputStream(responseStream));
            if (errorMapping == null ||
                !errorMapping.containsKey(statusCodeString) ||
                !(statusCode >= 400 && statusCode <= 499 && errorMapping.containsKey("4XX")) &&
                    !(statusCode >= 500 && statusCode <= 599 && errorMapping.containsKey("5XX"))) {
                ServiceException ex = new ServiceException(ErrorConstants.Codes.GENERAL_EXCEPTION, null, statusCode, responseMessage.headers(), rawResponseBody);
                CompletableFuture<Boolean> exceptionalResult = new CompletableFuture<>();
                exceptionalResult.completeExceptionally(ex);
                return exceptionalResult;
            } else {
                Parsable result = node.getObjectValue(errorMapping.get(statusCodeString));
                if (!(result instanceof Exception)) {
                    ApiException exception = new ApiException("The server returned an unexpected status code and the error registered for this code failed to deserialize: " + statusCodeString);
                    CompletableFuture<Boolean> exceptionalResult = new CompletableFuture<>();
                    exceptionalResult.completeExceptionally(exception);
                    return exceptionalResult;
                } else {
                    CompletableFuture<Boolean> exceptionalResult = new CompletableFuture<>();
                    exceptionalResult.completeExceptionally((Exception) result);
                    return exceptionalResult;
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
