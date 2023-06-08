package com.microsoft.graph.testModels;

import com.microsoft.kiota.serialization.AdditionalDataHolder;
import com.microsoft.kiota.serialization.Parsable;
import com.microsoft.kiota.serialization.ParseNode;
import com.microsoft.kiota.serialization.SerializationWriter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class TestEmailAddress implements Parsable, AdditionalDataHolder {
    public String Name;
    public String Address;
    public HashMap<String, Object> additionalData;
    public String ODataType;

    public TestEmailAddress() {
        this.ODataType = "microsoft.graph.emailAddress";
        this.additionalData = new HashMap<>();
    }

    @Override
    public HashMap<String, Consumer<ParseNode>> getFieldDeserializers() {
        HashMap<String, Consumer<ParseNode>> props = new HashMap<>();
        props.put("@odata.type", (n) -> this.ODataType = n.getStringValue());
        props.put("name", (n) -> this.Name = n.getStringValue());
        props.put("address", (n) -> this.Address = n.getStringValue());
        return props;
    }

    @Override
    public void serialize(SerializationWriter writer) {
        if (writer == null) {
            throw new IllegalArgumentException("The writer parameter cannot be null.");
        }
        writer.writeStringValue("name", Name);
        writer.writeStringValue("address", Address);
        writer.writeStringValue("@odata.type", ODataType);
        writer.writeAdditionalData(additionalData);
    }

    public static TestEmailAddress createFromDiscriminatorValue(ParseNode parseNode) {
        if (parseNode == null) {
            throw new IllegalArgumentException("The parseNode parameter cannot be null.");
        }
        return new TestEmailAddress();
    }

    @NotNull
    @Override
    public Map<String, Object> getAdditionalData() {
        return this.additionalData;
    }
}
