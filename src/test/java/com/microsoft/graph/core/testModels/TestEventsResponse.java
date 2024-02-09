package com.microsoft.graph.core.testModels;


import com.microsoft.kiota.serialization.AdditionalDataHolder;
import com.microsoft.kiota.serialization.Parsable;
import com.microsoft.kiota.serialization.ParseNode;
import com.microsoft.kiota.serialization.SerializationWriter;

import jakarta.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class TestEventsResponse extends BaseCollectionPaginationCountResponse {
    public List<TestEventItem> value;

    public TestEventsResponse() {
        super();
    }

    public List<TestEventItem> getValue() {
        return value;
    }

    public void setValue(List<TestEventItem> value) {
        this.value = value;
    }
    @Nonnull
    public Map<String, Consumer<ParseNode>> getFieldDeserializers() {
        final HashMap<String, java.util.function.Consumer<ParseNode>> deserializerMap = new HashMap<String, java.util.function.Consumer<ParseNode>>(super.getFieldDeserializers());
        deserializerMap.put("value", (n) -> { this.setValue(n.getCollectionOfObjectValues(TestEventItem::createFromDiscriminatorValue)); });
        return deserializerMap;
    }

    public void serialize(@Nonnull SerializationWriter writer) {
        Objects.requireNonNull(writer);
        super.serialize(writer);
        writer.writeCollectionOfObjectValues("value", getValue());
    }

    public static TestEventsResponse createFromDiscriminatorValue(ParseNode parseNode) {
        Objects.requireNonNull(parseNode);
        return new TestEventsResponse();
    }
}
