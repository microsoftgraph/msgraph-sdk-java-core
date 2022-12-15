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

public class ErrorDetail implements Parsable, AdditionalDataHolder {

    private String code;
    private String message;
    private String target;
    private HashMap<String, Object> additionalData;

    public void setCode(String code) {
        this.code = code;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public void setTarget(String target) {
        this.target = target;
    }

    @Nonnull
    @Override
    public Map<String, Object> getAdditionalData() {
        return this.additionalData;
    }

    @Nonnull
    @Override
    public Map<String, Consumer<ParseNode>> getFieldDeserializers() {
        ErrorDetail currentObj = this;
        return new HashMap<String, Consumer<ParseNode>>(){{
            this.put("code", (n) -> { currentObj.setCode(n.getStringValue()); });
            this.put("message", (n) -> { currentObj.setMessage(n.getStringValue()); });
            this.put("target", (n) -> { currentObj.setTarget(n.getStringValue()); });
        }};
    }

    @Override
    public void serialize(@Nonnull SerializationWriter writer) {
        Objects.requireNonNull(writer);
        writer.writeStringValue("code", this.code);
        writer.writeStringValue("message", this.message);
        writer.writeStringValue("target", this.target);
        writer.writeAdditionalData(additionalData);
    }

    public static ErrorDetail createFromDiscriminatorValue(@Nonnull ParseNode parseNode) {
        Objects.requireNonNull(parseNode);
        return new ErrorDetail();
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        //TODO: build string
        return stringBuilder.toString();
    }
}
