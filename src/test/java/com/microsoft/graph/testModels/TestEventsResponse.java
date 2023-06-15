package com.microsoft.graph.testModels;


import com.microsoft.kiota.serialization.AdditionalDataHolder;
import com.microsoft.kiota.serialization.Parsable;
import com.microsoft.kiota.serialization.ParseNode;
import com.microsoft.kiota.serialization.SerializationWriter;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class TestEventsResponse implements Parsable, AdditionalDataHolder {
    private Map<String, Object> additionalData;
    private String odataNextLink;
    private List<TestEventItem> value;

    public TestEventsResponse() {
        additionalData = new HashMap<>();
    }

    @Nonnull
    public Map<String, Object> getAdditionalData() {
        return additionalData;
    }

    public void setAdditionalData(Map<String, Object> additionalData) {
        this.additionalData = additionalData;
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
    @Nonnull
    public Map<String, Consumer<ParseNode>> getFieldDeserializers() {
        HashMap<String, Consumer<ParseNode>> fieldDeserializers = new HashMap<>();
        fieldDeserializers.put("@odata.nextLink", (n) -> setOdataNextLink(n.getStringValue()));
        fieldDeserializers.put("value", (n) -> setValue(n.getCollectionOfObjectValues(TestEventItem::createFromDiscriminatorValue)));
        return fieldDeserializers;
    }

    public void serialize(@Nonnull SerializationWriter writer) {
        Objects.requireNonNull(writer);
        writer.writeStringValue("@odata.nextLink", getOdataNextLink());
        writer.writeCollectionOfObjectValues("value", getValue());
        writer.writeAdditionalData(getAdditionalData());
    }

    public static TestEventsResponse createFromDiscriminatorValue(ParseNode parseNode) {
        Objects.requireNonNull(parseNode);
        return new TestEventsResponse();
    }
}
