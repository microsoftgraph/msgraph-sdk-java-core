package com.microsoft.graph.testModels;

import com.microsoft.kiota.serialization.AdditionalDataHolder;
import com.microsoft.kiota.serialization.Parsable;
import com.microsoft.kiota.serialization.ParseNode;
import com.microsoft.kiota.serialization.SerializationWriter;

import jakarta.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class TestDrive implements Parsable, AdditionalDataHolder {
    public String id;
    public String odataType;
    public String name;
    public HashMap<String, Object> additionalData;

    public TestDrive() {
        this.odataType = "microsoft.graph.drive";
        this.additionalData = new HashMap<>();
    }

    @Override
    public HashMap<String, Consumer<ParseNode>> getFieldDeserializers() {
        HashMap<String, Consumer<ParseNode>> props = new HashMap<>();
        props.put("@odata.type", (n) ->  this.odataType = n.getStringValue());
        props.put("id", (n) -> this.id = n.getStringValue());
        props.put("name", (n) -> this.name = n.getStringValue());
        return props;
    }

    @Override
    public void serialize(SerializationWriter writer) {
        if (writer == null) {
            throw new IllegalArgumentException("The writer parameter cannot be null.");
        }
        writer.writeStringValue("@odata.type", odataType);
        writer.writeStringValue("id", id);
        writer.writeStringValue("name", name);
        writer.writeAdditionalData(additionalData);
    }

    @Nonnull
    @Override
    public Map<String, Object> getAdditionalData() {
        return this.additionalData;
    }

    public static TestDrive createFromDiscriminatorValue(ParseNode parseNode) {
        if (parseNode == null) {
            throw new IllegalArgumentException("parseNode cannot be null");
        }
        return new TestDrive();
    }

}
