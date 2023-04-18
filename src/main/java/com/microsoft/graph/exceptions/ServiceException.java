package com.microsoft.graph.exceptions;

import com.microsoft.kiota.ApiException;
import com.microsoft.kiota.serialization.AdditionalDataHolder;
import com.microsoft.kiota.serialization.Parsable;
import com.microsoft.kiota.serialization.ParseNode;
import com.microsoft.kiota.serialization.SerializationWriter;
import okhttp3.Headers;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Graph Service Exception
 */
public class ServiceException extends ApiException implements Parsable, AdditionalDataHolder {

    /** The response headers received. */
    private final transient Headers responseHeaders;
    /** The response body received. */
    private String rawResponseBody;
    /** The additional data found. */
    private final transient Map<String, Object> additionalData = new HashMap<>();

    /**
     * Creates a new service exception.
     * @param message The error message.
     */
    public ServiceException(@Nonnull final String message) {
        this(message, null, 0, null);
    }
    /**
     * Creates a new service exception.
     * @param message The error message.
     * @param cause The possible innerException.
     */
    public ServiceException(@Nonnull final String message, @Nonnull final Throwable cause) {
        this(message, cause, 0, null);
    }
    /**
     * Creates a new service exception.
     * @param message The error message.
     * @param cause The possible innerException.
     * @param statusCode The HTTP status code from the response.
     * @param responseHeaders The HTTP response headers from the response.
     */
    public ServiceException(@Nonnull final String message,
                            @Nullable final Throwable cause,
                            final int statusCode,
                            @Nullable final Headers responseHeaders) {
        this(message, cause, statusCode, responseHeaders, null);
    }
    /**
     * Creates a new service exception.
     * @param message The error message.
     * @param cause The possible innerException.
     * @param statusCode The HTTP status code from the response.
     * @param responseHeaders The HTTP response headers from the response.
     * @param rawResponseBody The raw JSON response body.
     */
    public ServiceException(@Nonnull final String message,
                            @Nullable final Throwable cause,
                            final int statusCode,
                            @Nullable final Headers responseHeaders,
                            @Nullable final String rawResponseBody){
        super(message);
        this.responseHeaders = responseHeaders;
        this.responseStatusCode = statusCode;
        this.rawResponseBody = rawResponseBody != null ? rawResponseBody : "";
        if(!Objects.isNull(cause)){
            this.initCause(cause);
        }
    }
    /**
     * Get the HTTP response headers from the response.
     * @return The HTTP response headers.
     */
    @Nullable
    public Headers getHeaders() {
        return this.responseHeaders;
    }
    /**
     * Get the raw response body from the response.
     * @return The raw response body.
     */
    @Nonnull
    public String getRawResponseBody() {
        return this.rawResponseBody;
    }
    /**
     * Set the raw response body.
     * @param rawResponseBody The response body to be set.
     */
    public void setRawResponseBody(@Nonnull String rawResponseBody) {
        this.rawResponseBody = rawResponseBody;
    }
    /**
     * Checks if a given error code has been returned by the response at any level in the error stack.
     * @param errorCode The error code to look for.
     * @return a boolean declaring whether the error code was found within the error stack.
     */
    public boolean isMatch(@Nonnull String errorCode) {
        if(errorCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Parameter 'errorCode 'cannot be null or empty");
        }
        return (this.rawResponseBody.toLowerCase(Locale.ROOT).contains(errorCode.toLowerCase(Locale.ROOT)))
            || (super.getMessage().toLowerCase(Locale.ROOT).contains(errorCode.toLowerCase(Locale.ROOT)));
    }
    /**
     * The Service Exception as a string.
     * @return Service exception response code and message as a string.
     */
    @Override
    public String toString() {
        return String.format(Locale.US,"Status Code: %d %n %s", this.responseStatusCode, super.toString());
    }
    /**
     * Stores additional data not described in the OpenAPI description found when deserializing. Can be used for serialization as well.
     * @return The additional data found.
     */
    @NotNull
    @Override
    public Map<String, Object> getAdditionalData() {
        return new HashMap<>(additionalData);
    }
    /**
     * Sets the additionalData property value. Stores additional data not described in the OpenAPI description found when deserializing. Can be used for serialization as well.
     * @param additionalData The AdditionalData to set.
     */
    public void setAdditionalData(@Nonnull final Map<String, Object> additionalData) {
        Map<String, Object> newData = new HashMap<>(additionalData);
        this.additionalData.putAll(newData);
    }
    /**
     * The deserialization information for the current model.
     * @return A hash map describing how to deserialize the current model fields.
     */
    @NotNull
    @Override
    public Map<String, Consumer<ParseNode>> getFieldDeserializers() {
        final ServiceException currentObj = this;
        HashMap<String, Consumer<ParseNode>> deserializers = new HashMap<>(2);
        deserializers.put("statusCode", n -> currentObj.responseStatusCode = n.getIntegerValue());
        deserializers.put("rawResponseBody", n -> currentObj.setRawResponseBody(n.getStringValue()));
        return deserializers;
    }
    /**
     * Serializes information the current object.
     * @param writer Serialization writer to use to serialize this model.
     */
    @Override
    public void serialize(@NotNull SerializationWriter writer) {
        Objects.requireNonNull(writer, "Writer cannot be null");
        writer.writeIntegerValue("status code", this.responseStatusCode);
        writer.writeStringValue("rawResponseBody", this.rawResponseBody);
        writer.writeStringValue("message", super.getMessage());
        writer.writeAdditionalData(this.additionalData);
    }
}
