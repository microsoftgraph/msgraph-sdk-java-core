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

public class ServiceException extends ApiException implements Parsable, AdditionalDataHolder {

    public Headers responseHeaders;
    public String rawResponseBody;
    public Map<String, Object> additionalData;

    public ServiceException(@Nonnull String message, @Nullable Throwable cause) {
        this(message, null, 0, null, cause);
    }
    public ServiceException(@Nonnull String message,
                            @Nullable Headers responseHeaders,
                            int statusCode,
                            @Nullable Throwable cause) {
        super(message, cause);
        this.responseHeaders = responseHeaders;
        this.responseStatusCode = statusCode;
    }
    public ServiceException(@Nonnull String message,
                            @Nullable Headers responseHeaders,
                            int statusCode,
                            @Nonnull String rawResponseBody,
                            @Nullable Throwable cause ) {
        this(message, responseHeaders, statusCode, cause);
        this.rawResponseBody = rawResponseBody;
    }
    public Headers getHeaders() {
        return this.responseHeaders;
    }
    public String getRawResponseBody() {
        return this.rawResponseBody;
    }
    public Boolean isMatch(String errorCode) {
        if(errorCode != null && !errorCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Parameter 'errorCode 'cannot be null or empty");
        }
        if(this.rawResponseBody.toLowerCase(Locale.ROOT).indexOf(errorCode.toLowerCase(Locale.ROOT)) >= 0) {
            return true;
        }
        if(super.getMessage().toLowerCase(Locale.ROOT).indexOf(errorCode.toLowerCase(Locale.ROOT)) >= 0) {
            return true;
        }
        return false;
    }
    @Override
    public String toString() {
        return String.format(Locale.US,"Status Code: %d \n %s", this.responseStatusCode, super.toString());
    }
    @NotNull
    @Override
    public Map<String, Object> getAdditionalData() {
        return this.additionalData;
    }
    @NotNull
    @Override
    public Map<String, Consumer<ParseNode>> getFieldDeserializers() {
        final ServiceException currentObject = this;
        return new HashMap<String, Consumer<ParseNode>>(2) {{
            this.put("statusCode", (n) -> {currentObject.responseStatusCode = n.getIntegerValue();} );
            this.put("rawResponseBody", (n) -> {currentObject.rawResponseBody = n.getStringValue();} );
        }};
    }
    @Override
    public void serialize(@NotNull SerializationWriter writer) {
        Objects.requireNonNull(writer, "Writer cannot be null");
        writer.writeIntegerValue("status code", this.responseStatusCode);
        writer.writeStringValue("rawResponseBody", this.rawResponseBody);
        writer.writeStringValue("message", super.getMessage());
        writer.writeAdditionalData(this.additionalData);
    }
}
