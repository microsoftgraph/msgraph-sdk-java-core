package com.microsoft.graph.testModels;

import com.microsoft.kiota.serialization.AdditionalDataHolder;
import com.microsoft.kiota.serialization.Parsable;
import com.microsoft.kiota.serialization.ParseNode;
import com.microsoft.kiota.serialization.SerializationWriter;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class TestEmailAddress implements Parsable, AdditionalDataHolder {
    public String name;
    public String address;
    public HashMap<String, Object> additionalData;
    public String odataType;

    public TestEmailAddress() {
        this.odataType = "microsoft.graph.emailAddress";
        this.additionalData = new HashMap<>();
    }

    @Override
    public HashMap<String, Consumer<ParseNode>> getFieldDeserializers() {
        HashMap<String, Consumer<ParseNode>> props = new HashMap<>();
        props.put("@odata.type", (n) -> this.odataType = n.getStringValue());
        props.put("name", (n) -> this.name = n.getStringValue());
        props.put("address", (n) -> this.address = n.getStringValue());
        return props;
    }

    @Override
    public void serialize(SerializationWriter writer) {
        if (writer == null) {
            throw new IllegalArgumentException("The writer parameter cannot be null.");
        }
        writer.writeStringValue("name", name);
        writer.writeStringValue("address", address);
        writer.writeStringValue("@odata.type", odataType);
        writer.writeAdditionalData(additionalData);
    }

    public static TestEmailAddress createFromDiscriminatorValue(ParseNode parseNode) {
        if (parseNode == null) {
            throw new IllegalArgumentException("The parseNode parameter cannot be null.");
        }
        return new TestEmailAddress();
    }

    @Nonnull
    @Override
    public Map<String, Object> getAdditionalData() {
        return this.additionalData;
    }
}
