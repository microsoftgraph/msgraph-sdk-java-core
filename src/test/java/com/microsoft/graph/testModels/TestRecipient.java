package com.microsoft.graph.testModels;

import com.microsoft.kiota.serialization.AdditionalDataHolder;
import com.microsoft.kiota.serialization.Parsable;
import com.microsoft.kiota.serialization.ParseNode;
import com.microsoft.kiota.serialization.SerializationWriter;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import jakarta.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@SuppressFBWarnings
public class TestRecipient implements Parsable, AdditionalDataHolder {
    public TestEmailAddress EmailAddress;
    public HashMap<String, Object> additionalData;
    public String ODataType;

    public TestRecipient() {
        this.ODataType = "microsoft.graph.recipient";
    }

    @Override
    public HashMap<String, Consumer<ParseNode>> getFieldDeserializers() {
        HashMap<String, Consumer<ParseNode>> props = new HashMap<>();
        props.put("@odata.type", (n) ->  this.ODataType = n.getStringValue());
        props.put("emailAddress", (n) -> this.EmailAddress = n.getObjectValue(TestEmailAddress::createFromDiscriminatorValue));
        return props;
    }

    @Override
    public void serialize(SerializationWriter writer) {
        if (writer == null) {
            throw new IllegalArgumentException("The writer parameter cannot be null.");
        }
        writer.writeStringValue("@odata.type", ODataType);
        writer.writeObjectValue("emailAddress", EmailAddress);
        writer.writeAdditionalData(additionalData);
    }

    public static TestRecipient createFromParseNode(ParseNode parseNode) {
        if (parseNode == null) {
            throw new IllegalArgumentException("The parseNode parameter cannot be null.");
        }
        ParseNode mappingValueNode = parseNode.getChildNode("@odata.type");
        String mappingValue = mappingValueNode.getStringValue();
        if (mappingValue == null) {
            return new TestRecipient();
        }
        switch (mappingValue) {
            case "microsoft.graph.attendee":
                return new TestAttendee();
            default:
                return new TestRecipient();
        }
    }

    @Nonnull
    @Override
    public Map<String, Object> getAdditionalData() {
        return this.additionalData;
    }
}
