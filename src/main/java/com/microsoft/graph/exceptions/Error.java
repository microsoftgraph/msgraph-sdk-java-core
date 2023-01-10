package com.microsoft.graph.exceptions;

import com.microsoft.kiota.serialization.AdditionalDataHolder;
import com.microsoft.kiota.serialization.Parsable;
import com.microsoft.kiota.serialization.ParseNode;
import com.microsoft.kiota.serialization.SerializationWriter;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Consumer;

public class Error implements Parsable, AdditionalDataHolder {
    private String code;
    private String message;
    private String target;
    private List<ErrorDetail> details;
    private Error innerError;
    private String throwSite;
    private String clientRequestId;
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
    public void setDetails(List<ErrorDetail> details) {
        this.details = new ArrayList(details);
    }
    public void setInnerError(Error error) {
        this.innerError = error;
    }
    protected void setThrowSite(String throwSite) {
        this.throwSite = throwSite;
    }
    protected void setClientRequestId(String clientRequestId) {
        this.clientRequestId = clientRequestId;
    }

    @Nonnull
    @Override
    public Map<String, Object> getAdditionalData() {
        return null;
    }

    @Nonnull
    @Override
    public Map<String, Consumer<ParseNode>> getFieldDeserializers() {
        final Error currentErr = this;
        return new HashMap<String, Consumer<ParseNode>>() {{
            this.put("code", (n) -> { currentErr.setCode(n.getStringValue()); });
            this.put("message", (n) -> { currentErr.setMessage(n.getStringValue()); });
            this.put("target", (n) -> { currentErr.setTarget(n.getStringValue()); });
            this.put("details", (n) -> { currentErr.setDetails(n.getCollectionOfObjectValues(ErrorDetail::createFromDiscriminatorValue)); });
            this.put("innerError", (n) -> { currentErr.setInnerError(n.getObjectValue(Error::createFromDiscriminatorValue)); });
        }};
    }

    @Override
    public void serialize(@Nonnull SerializationWriter writer) {
        Objects.requireNonNull(writer);
        writer.writeStringValue("code", this.code);
        writer.writeStringValue("message", this.message);
        writer.writeStringValue("target", this.target);
        writer.<ErrorDetail>writeCollectionOfObjectValues("details", this.details);
        writer.<Error>writeObjectValue("innerError", this.innerError);
        writer.writeAdditionalData(this.additionalData);
    }

    public static Error createFromDiscriminatorValue(@Nonnull ParseNode parseNode) {
        Objects.requireNonNull(parseNode);
        return new Error();
    }

    @Override
    public String toString() {
        //TODO: implement toString
        return "";
    }

}
