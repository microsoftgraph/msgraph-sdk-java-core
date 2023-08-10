package com.microsoft.graph.testModels;

import com.microsoft.kiota.serialization.AdditionalDataHolder;
import com.microsoft.kiota.serialization.Parsable;
import com.microsoft.kiota.serialization.ParseNode;
import com.microsoft.kiota.serialization.SerializationWriter;

import jakarta.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class TestRecipient implements Parsable, AdditionalDataHolder {
    public TestEmailAddress emailAddress;
    public HashMap<String, Object> additionalData;
    public String ODataType;

    public TestRecipient() {
        this.ODataType = "microsoft.graph.recipient";
    }

    @Override
    public HashMap<String, Consumer<ParseNode>> getFieldDeserializers() {
        HashMap<String, Consumer<ParseNode>> props = new HashMap<>();
        props.put("@odata.type", (n) ->  this.ODataType = n.getStringValue());
        props.put("emailAddress", (n) -> this.emailAddress = n.getObjectValue(TestEmailAddress::createFromDiscriminatorValue));
        return props;
    }

    @Override
    public void serialize(@Nonnull SerializationWriter writer) {
        writer.writeStringValue("@odata.type", ODataType);
        writer.writeObjectValue("emailAddress", emailAddress);
        writer.writeAdditionalData(additionalData);
    }

    public static TestRecipient createFromParseNode(ParseNode parseNode) {
        if (parseNode == null) {
            throw new IllegalArgumentException("The parseNode parameter cannot be null.");
        }
        ParseNode mappingValueNode = parseNode.getChildNode("@odata.type");
        assert mappingValueNode != null;
        String mappingValue = mappingValueNode.getStringValue();
        if (mappingValue == null) {
            return new TestRecipient();
        }
        if (mappingValue.equals("microsoft.graph.attendee")) {
            return new TestAttendee();
        }
        return new TestRecipient();
    }

    @Nonnull
    @Override
    public Map<String, Object> getAdditionalData() {
        return this.additionalData;
    }
}
