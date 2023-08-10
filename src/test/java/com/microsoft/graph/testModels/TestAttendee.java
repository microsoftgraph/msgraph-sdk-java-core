package com.microsoft.graph.testModels;

import com.microsoft.kiota.serialization.AdditionalDataHolder;
import com.microsoft.kiota.serialization.Parsable;
import com.microsoft.kiota.serialization.ParseNode;
import com.microsoft.kiota.serialization.SerializationWriter;

import jakarta.annotation.Nonnull;
import java.util.Map;

public class TestAttendee extends TestRecipient implements Parsable, AdditionalDataHolder {
    public TestAttendee() {
        this.ODataType = "microsoft.graph.attendee";
    }

    @Override
    public void serialize(@Nonnull SerializationWriter writer) {
        super.serialize(writer);
        writer.writeAdditionalData(this.additionalData);
    }

    public static TestAttendee createFromDiscriminatorValue(ParseNode parseNode) {
        if (parseNode == null) {
            throw new IllegalArgumentException("The parseNode parameter cannot be null.");
        }
        return new TestAttendee();
    }

    @Nonnull
    @Override
    public Map<String, Object> getAdditionalData() {
        return this.additionalData;
    }
}
