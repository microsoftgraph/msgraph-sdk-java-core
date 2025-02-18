package com.microsoft.graph.core.testModels;

import com.microsoft.kiota.serialization.AdditionalDataHolder;
import com.microsoft.kiota.serialization.Parsable;
import com.microsoft.kiota.serialization.ParseNode;
import com.microsoft.kiota.serialization.SerializationWriter;
import jakarta.annotation.Nonnull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.function.Consumer;

public class TestChatMessage implements Parsable, AdditionalDataHolder {

    private HashMap<String, Object> additionalData;
    private String etag;

    public TestChatMessage() {
    }

    public String getETag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    @Nonnull
    public HashMap<String, Object> getAdditionalData() {
        return additionalData;
    }

    public void setAdditionalData(HashMap<String, Object> additionalData) {
        this.additionalData = additionalData;
    }

    public HashMap<String, Consumer<ParseNode>> getFieldDeserializers() {
        HashMap<String, Consumer<ParseNode>> fieldDeserializers = new HashMap<>();
        fieldDeserializers.put("etag", (n) -> { setEtag(n.getStringValue()); });
        return fieldDeserializers;
    }

    public void serialize(SerializationWriter writer) {
        Objects.requireNonNull(writer);
        writer.writeStringValue("etag", getETag());
        writer.writeAdditionalData(getAdditionalData());
    }

    public static TestChatMessage createFromDiscriminatorValue(ParseNode parseNode) {
        if (parseNode == null) {
            throw new IllegalArgumentException("The parseNode cannot be null.");
        }
        return new TestChatMessage();
    }
}
