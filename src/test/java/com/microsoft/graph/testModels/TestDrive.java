package com.microsoft.graph.testModels;

import com.microsoft.kiota.serialization.AdditionalDataHolder;
import com.microsoft.kiota.serialization.Parsable;
import com.microsoft.kiota.serialization.ParseNode;
import com.microsoft.kiota.serialization.SerializationWriter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class TestDrive implements Parsable, AdditionalDataHolder {
    public String Id;
    public String ODataType;
    public String Name;
    public HashMap<String, Object> additionalData;

    public TestDrive() {
        this.ODataType = "microsoft.graph.drive";
        this.additionalData = new HashMap<>();
    }

    @Override
    public HashMap<String, Consumer<ParseNode>> getFieldDeserializers() {
        HashMap<String, Consumer<ParseNode>> props = new HashMap<>();
        props.put("@odata.type", (n) ->  this.ODataType = n.getStringValue());
        props.put("id", (n) -> this.Id = n.getStringValue());
        props.put("name", (n) -> this.Name = n.getStringValue());
        return props;
    }

    @Override
    public void serialize(SerializationWriter writer) {
        if (writer == null) {
            throw new IllegalArgumentException("The writer parameter cannot be null.");
        }
        writer.writeStringValue("@odata.type", ODataType);
        writer.writeStringValue("id", Id);
        writer.writeStringValue("name", Name);
        writer.writeAdditionalData(additionalData);
    }

    @NotNull
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
