package com.microsoft.graph.testModels;

import com.microsoft.kiota.serialization.AdditionalDataHolder;
import com.microsoft.kiota.serialization.Parsable;
import com.microsoft.kiota.serialization.ParseNode;
import com.microsoft.kiota.serialization.SerializationWriter;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class TestDriveItem implements Parsable, AdditionalDataHolder {

    public String id;
    public String oDataType = "#microsoft.graph.driveItem";
    public String name;
    public long size;
    public HashMap<String, Object> additionalData = new HashMap<>();

    public TestDriveItem() {}

    @Nonnull
    @Override
    public Map<String, Object> getAdditionalData() {
        return this.additionalData;
    }

    @Nonnull
    @Override
    public Map<String, Consumer<ParseNode>> getFieldDeserializers() {
        final HashMap<String, Consumer<ParseNode>> deserializerMap = new HashMap<String, Consumer<ParseNode>>();
        deserializerMap.put("id", (n)-> this.id = n.getStringValue());
        deserializerMap.put("@odata.type", (n)-> this.oDataType = n.getStringValue());
        deserializerMap.put("name", (n)-> this.name = n.getStringValue());
        deserializerMap.put("size", (n)-> this.size = n.getLongValue());
        return deserializerMap;
    }

    @Override
    public void serialize(@Nonnull SerializationWriter writer) {
        Objects.requireNonNull(writer);
        writer.writeStringValue("id", id);
        writer.writeStringValue("@odata.type", oDataType);
        writer.writeStringValue("name", name);
        writer.writeLongValue("size", size);
    }

    public static TestDriveItem createFromDiscriminatorValue(ParseNode parseNode) {
        Objects.requireNonNull(parseNode);
        return new TestDriveItem();
    }
}
