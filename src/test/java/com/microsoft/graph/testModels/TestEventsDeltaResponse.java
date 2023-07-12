package com.microsoft.graph.testModels;

import com.microsoft.kiota.serialization.AdditionalDataHolder;
import com.microsoft.kiota.serialization.Parsable;
import com.microsoft.kiota.serialization.ParseNode;
import com.microsoft.kiota.serialization.SerializationWriter;

import java.util.*;
import java.util.function.Consumer;

public class TestEventsDeltaResponse implements Parsable, AdditionalDataHolder {
    public Map<String, Object> additionalData;
    private String odataDeltaLink;
    private String odataNextLink;
    public List<TestEventItem> value;

    public TestEventsDeltaResponse() {
        additionalData = new HashMap<>();
    }

    public Map<String, Object> getAdditionalData() {
        return additionalData;
    }

    public void setAdditionalData(Map<String, Object> additionalData) {
        this.additionalData = additionalData;
    }

    public String getOdataDeltaLink() {
        return odataDeltaLink;
    }

    public void setOdataDeltaLink(String odataDeltaLink) {
        this.odataDeltaLink = odataDeltaLink;
    }

    public String getOdataNextLink() {
        return odataNextLink;
    }

    public void setOdataNextLink(String odataNextLink) {
        this.odataNextLink = odataNextLink;
    }

    public List<TestEventItem> getValue() {
        return value;
    }

    public void setValue(List<TestEventItem> value) {
        this.value = value;
    }

    public Map<String, Consumer<ParseNode>> getFieldDeserializers() {
        HashMap<String, Consumer<ParseNode>> fieldDeserializers = new HashMap<>();
        fieldDeserializers.put("@odata.deltaLink", (n) -> setOdataDeltaLink(n.getStringValue()));
        fieldDeserializers.put("@odata.nextLink", (n) -> setOdataNextLink(n.getStringValue()));
        fieldDeserializers.put("value", (n) -> setValue(n.getCollectionOfObjectValues(TestEventItem::createFromDiscriminatorValue)));
        return fieldDeserializers;
    }

    public void serialize(SerializationWriter writer) {
        if (writer == null) {
            throw new IllegalArgumentException("writer");
        }

        writer.writeStringValue("@odata.deltaLink", odataDeltaLink);
        writer.writeStringValue("@odata.nextLink", odataNextLink);
        writer.writeCollectionOfObjectValues("value", value);
        writer.writeAdditionalData(additionalData);
    }

    public static TestEventsDeltaResponse createFromDiscriminatorValue(ParseNode parseNode) {
        if (parseNode == null) {
            throw new IllegalArgumentException("parseNode");
        }
        return new TestEventsDeltaResponse();
    }
}
