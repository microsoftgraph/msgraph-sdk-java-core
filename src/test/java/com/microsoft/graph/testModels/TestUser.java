package com.microsoft.graph.testModels;

import com.microsoft.kiota.serialization.AdditionalDataHolder;
import com.microsoft.kiota.serialization.Parsable;
import com.microsoft.kiota.serialization.ParseNode;
import com.microsoft.kiota.serialization.SerializationWriter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class TestUser implements Parsable, AdditionalDataHolder {
    private String id;
    private String oDataType;
    private HashMap<String, Object> additionalData;
    private String givenName;
    private String displayName;
    private String state;
    private String surname;
    private List<TestEvent> eventDeltas;

    public TestUser() {
        this.oDataType = "microsoft.graph.user";
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

    public HashMap<String, Object> getAdditionalData() {
        return additionalData;
    }

    public void setAdditionalData(HashMap<String, Object> additionalData) {
        this.additionalData = additionalData;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public List<TestEvent> getEventDeltas() {
        return eventDeltas;
    }

    public void setEventDeltas(List<TestEvent> eventDeltas) {
        this.eventDeltas = eventDeltas;
    }

    public HashMap<String, Consumer<ParseNode>> getFieldDeserializers() {
        final HashMap<String, Consumer<ParseNode>> deserializerMap = new HashMap<String, Consumer<ParseNode>>();
        deserializerMap.put("id", (n) -> { setId(n.getStringValue()); });
        deserializerMap.put("@odata.type", (n) -> { setODataType(n.getStringValue()); });
        deserializerMap.put("givenName", (n) -> { setGivenName(n.getStringValue()); });
        deserializerMap.put("displayName", (n) -> { setDisplayName(n.getStringValue()); });
        deserializerMap.put("state", (n) -> { setState(n.getStringValue()); });
        deserializerMap.put("surname", (n) -> { setSurname(n.getStringValue()); });
        deserializerMap.put("eventDeltas", (n) -> {
            ArrayList<TestEvent> eventList = new ArrayList<>();
            for (TestEvent item : n.getCollectionOfObjectValues(TestEvent::createFromDiscriminatorValue)) {
                eventList.add(item);
            }
            setEventDeltas(eventList);
        });
        return deserializerMap;
    }

    public void serialize(SerializationWriter writer) {
        if (writer == null) {
            throw new IllegalArgumentException("writer cannot be null");
        }
        writer.writeStringValue("id", id);
        writer.writeStringValue("@odata.type", oDataType);
        writer.writeStringValue("givenName", givenName);
        writer.writeStringValue("displayName", displayName);
        writer.writeStringValue("state", state);
        writer.writeStringValue("surname", surname);
        writer.writeCollectionOfObjectValues("eventDeltas", eventDeltas);
        writer.writeAdditionalData(additionalData);
    }

    public static TestUser createFromDiscriminatorValue(ParseNode parseNode) {
        if (parseNode == null) {
            throw new IllegalArgumentException("parseNode cannot be null");
        }
        return new TestUser();
    }
}
