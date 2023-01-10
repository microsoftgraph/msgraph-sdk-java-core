package com.microsoft.graph.exceptions;

import com.microsoft.kiota.serialization.AdditionalDataHolder;
import com.microsoft.kiota.serialization.Parsable;
import com.microsoft.kiota.serialization.ParseNode;
import com.microsoft.kiota.serialization.SerializationWriter;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class ErrorResponse implements Parsable, AdditionalDataHolder {

    private Error error;
    private HashMap<String, Object> additionalData;

    public void setError(Error error) {
        this.error = error;
    }
    public Error getError() {
        return this.error;
    }

    @Nonnull
    @Override
    public Map<String, Object> getAdditionalData() {
        return null;
    }

    @Nonnull
    @Override
    public Map<String, Consumer<ParseNode>> getFieldDeserializers() {
        ErrorResponse currentObj = this;
        return new HashMap<String, Consumer<ParseNode>>(){{
         this.put("error", (n) -> { currentObj.setError(n.getObjectValue(Error::createFromDiscriminatorValue)); });
        }};
    }

    @Override
    public void serialize(@Nonnull SerializationWriter writer) {
        Objects.requireNonNull(writer);
        writer.writeObjectValue("error", this.error);
        writer.writeAdditionalData(this.additionalData);
    }

    public static ErrorResponse createFromDiscriminatorValue(@Nonnull ParseNode parseNode) {
        Objects.requireNonNull(parseNode);
        return new ErrorResponse();
    }
}
