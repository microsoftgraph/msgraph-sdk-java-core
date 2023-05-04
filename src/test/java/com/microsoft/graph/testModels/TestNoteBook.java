package com.microsoft.graph.testModels;

import com.microsoft.kiota.serialization.AdditionalDataHolder;
import com.microsoft.kiota.serialization.Parsable;
import com.microsoft.kiota.serialization.ParseNode;
import com.microsoft.kiota.serialization.SerializationWriter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class TestNoteBook implements Parsable, AdditionalDataHolder {
    public String Id;
    public String ODataType;
    public String DisplayName;
    public HashMap<String, Object> AdditionalData;

    public TestNoteBook() {
        this.ODataType = "microsoft.graph.notebook";
        this.AdditionalData = new HashMap<>();
    }

    @Override
    public HashMap<String, Consumer<ParseNode>> getFieldDeserializers() {
        HashMap<String, Consumer<ParseNode>> props = new HashMap<>();
        props.put("@odata.type", (n) -> ODataType = n.getStringValue());
        props.put("id", (n) -> Id = n.getStringValue());
        props.put("displayName", (n) -> DisplayName = n.getStringValue());
        return props;
    }

    @Override
    public void serialize(SerializationWriter writer) {
        if (writer == null) {
            throw new IllegalArgumentException("The writer parameter cannot be null.");
        }
        writer.writeStringValue("@odata.type", ODataType);
        writer.writeStringValue("id", Id);
        writer.writeStringValue("displayName", DisplayName);
        writer.writeAdditionalData(AdditionalData);
    }

    public static TestNoteBook createFromDiscriminatorValue(ParseNode parseNode) {
        if (parseNode == null) {
            throw new IllegalArgumentException("The parseNode parameter cannot be null.");
        }
        return new TestNoteBook();
    }
    @NotNull
    @Override
    public Map<String, Object> getAdditionalData() {
        return this.AdditionalData;
    }
}
