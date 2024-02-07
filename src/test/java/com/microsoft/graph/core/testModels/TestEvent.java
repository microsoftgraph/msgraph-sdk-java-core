package com.microsoft.graph.core.testModels;

import com.microsoft.kiota.serialization.AdditionalDataHolder;
import com.microsoft.kiota.serialization.Parsable;
import com.microsoft.kiota.serialization.ParseNode;
import com.microsoft.kiota.serialization.SerializationWriter;

import jakarta.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.function.Consumer;

public class TestEvent implements Parsable, AdditionalDataHolder {
    private String id;
    private String oDataType;
    private HashMap<String, Object> additionalData;
    private String subject;
    private TestItemBody body;
    private TestDateTimeTimeZone end;
    private TestDateTimeTimeZone start;
    private ArrayList<TestAttendee> attendees;

    public TestEvent() {
        this.oDataType = "microsoft.graph.event";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getODataType() {
        return oDataType;
    }

    public void setODataType(String oDataType) {
        this.oDataType = oDataType;
    }

    @Nonnull
    public HashMap<String, Object> getAdditionalData() {
        return additionalData;
    }

    public void setAdditionalData(HashMap<String, Object> additionalData) {
        this.additionalData = additionalData;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public TestItemBody getBody() {
        return body;
    }

    public void setBody(TestItemBody body) {
        this.body = body;
    }

    public TestDateTimeTimeZone getEnd() {
        return end;
    }

    public void setEnd(TestDateTimeTimeZone end) {
        this.end = end;
    }

    public TestDateTimeTimeZone getStart() {
        return start;
    }

    public void setStart(TestDateTimeTimeZone start) {
        this.start = start;
    }

    public ArrayList<TestAttendee> getAttendees() {
        return attendees;
    }

    public void setAttendees(ArrayList<TestAttendee> attendees) {
        this.attendees = attendees;
    }

    public HashMap<String, Consumer<ParseNode>> getFieldDeserializers() {
        HashMap<String, Consumer<ParseNode>> fieldDeserializers = new HashMap<>();
        fieldDeserializers.put("@odata.type", (n) -> { setODataType(n.getStringValue()); });
        fieldDeserializers.put("id", (n) -> { setId(n.getStringValue()); });
        fieldDeserializers.put("subject", (n) -> { setSubject(n.getStringValue()); });
        fieldDeserializers.put("body", (n) -> { setBody(n.getObjectValue(TestItemBody::createFromDiscriminatorValue)); });
        fieldDeserializers.put("end", (n) -> { setEnd(n.getObjectValue(TestDateTimeTimeZone::createFromDiscriminatorValue)); });
        fieldDeserializers.put("start", (n) -> { setStart(n.getObjectValue(TestDateTimeTimeZone::createFromDiscriminatorValue)); });
        fieldDeserializers.put("attendees", (n) -> { setAttendees((ArrayList<TestAttendee>) n.getCollectionOfObjectValues(TestAttendee::createFromDiscriminatorValue)); });
        return fieldDeserializers;
    }

    public void serialize(SerializationWriter writer) {
        Objects.requireNonNull(writer);
        writer.writeStringValue("@odata.type", getODataType());
        writer.writeStringValue("id", getId());
        writer.writeStringValue("subject", getSubject());
        writer.writeObjectValue("body", getBody());
        writer.writeObjectValue("end", getEnd());
        writer.writeObjectValue("start", getStart());
        writer.writeCollectionOfObjectValues("attendees", getAttendees());
        writer.writeAdditionalData(getAdditionalData());
    }

    public static TestEvent createFromDiscriminatorValue(ParseNode parseNode) {
        if (parseNode == null) {
            throw new IllegalArgumentException("The parseNode cannot be null.");
        }
        return new TestEvent();
    }
}
