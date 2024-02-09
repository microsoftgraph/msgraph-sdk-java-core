package com.microsoft.graph.core.testModels;

import com.microsoft.kiota.serialization.AdditionalDataHolder;
import com.microsoft.kiota.serialization.Parsable;
import com.microsoft.kiota.serialization.ParseNode;
import com.microsoft.kiota.serialization.SerializationWriter;

import java.util.*;
import java.util.function.Consumer;

public class TestEventsDeltaResponse extends BaseCollectionPaginationCountResponse {
    private String odataDeltaLink;
    public List<TestEventItem> value;

    public TestEventsDeltaResponse() {
        super();
    }

    public String getOdataDeltaLink() {
        return odataDeltaLink;
    }

    public void setOdataDeltaLink(String odataDeltaLink) {
        this.odataDeltaLink = odataDeltaLink;
    }

    public List<TestEventItem> getValue() {
        return value;
    }

    public void setValue(List<TestEventItem> value) {
        this.value = value;
    }

    public Map<String, Consumer<ParseNode>> getFieldDeserializers() {
        final HashMap<String, java.util.function.Consumer<ParseNode>> deserializerMap = new HashMap<String, java.util.function.Consumer<ParseNode>>(super.getFieldDeserializers());
        deserializerMap.put("value", (n) -> { this.setValue(n.getCollectionOfObjectValues(TestEventItem::createFromDiscriminatorValue)); });
        return deserializerMap;
    }

    public void serialize(SerializationWriter writer) {
        Objects.requireNonNull(writer);
        super.serialize(writer);
        writer.writeCollectionOfObjectValues("value", getValue());
    }

    public static TestEventsDeltaResponse createFromDiscriminatorValue(ParseNode parseNode) {
        if (parseNode == null) {
            throw new IllegalArgumentException("parseNode");
        }
        return new TestEventsDeltaResponse();
    }
}
