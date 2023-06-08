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
    public String id;
    public String odataType;
    public String displayName;
    public HashMap<String, Object> additionalData;

    public TestNoteBook() {
        this.odataType = "microsoft.graph.notebook";
        this.additionalData = new HashMap<>();
    }

    @Override
    public HashMap<String, Consumer<ParseNode>> getFieldDeserializers() {
        HashMap<String, Consumer<ParseNode>> props = new HashMap<>();
        props.put("@odata.type", (n) -> odataType = n.getStringValue());
        props.put("id", (n) -> id = n.getStringValue());
        props.put("displayName", (n) -> displayName = n.getStringValue());
        return props;
    }

    @Override
    public void serialize(SerializationWriter writer) {
        if (writer == null) {
            throw new IllegalArgumentException("The writer parameter cannot be null.");
        }
        writer.writeStringValue("@odata.type", odataType);
        writer.writeStringValue("id", id);
        writer.writeStringValue("displayName", displayName);
        writer.writeAdditionalData(additionalData);
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
        return this.additionalData;
    }
}
