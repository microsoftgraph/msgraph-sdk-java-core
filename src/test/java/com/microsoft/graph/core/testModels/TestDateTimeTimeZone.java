package com.microsoft.graph.core.testModels;

import com.microsoft.kiota.serialization.AdditionalDataHolder;
import com.microsoft.kiota.serialization.Parsable;
import com.microsoft.kiota.serialization.ParseNode;
import com.microsoft.kiota.serialization.SerializationWriter;

import java.util.HashMap;
import java.util.function.Consumer;

public class TestDateTimeTimeZone implements Parsable, AdditionalDataHolder {
    private String dateTime;
    private String timeZone;
    private HashMap<String, Object> additionalData = new HashMap<>();
    private String oDataType;

    public TestDateTimeTimeZone() {
        this.oDataType = "microsoft.graph.dateTimeTimeZone";
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public HashMap<String, Object> getAdditionalData() {
        return additionalData;
    }

    public void setAdditionalData(HashMap<String, Object> additionalData) {
        this.additionalData = additionalData;
    }

    public String getODataType() {
        return oDataType;
    }

    public void setODataType(String oDataType) {
        this.oDataType = oDataType;
    }

    public HashMap<String, Consumer<ParseNode>> getFieldDeserializers() {
        HashMap<String, Consumer<ParseNode>> fieldDeserializers = new HashMap<>();
        fieldDeserializers.put("dateTime", (n) -> setDateTime(n.getStringValue()));
        fieldDeserializers.put("timeZone", (n) -> setTimeZone(n.getStringValue()));
        fieldDeserializers.put("@odata.type", (n) -> setODataType(n.getStringValue()));
        return fieldDeserializers;
    }

    public void serialize(SerializationWriter writer) {
        writer.writeStringValue("dateTime", dateTime);
        writer.writeStringValue("timeZone", timeZone);
        writer.writeStringValue("@odata.type", oDataType);
        writer.writeAdditionalData(additionalData);
    }

    public static TestDateTimeTimeZone createFromDiscriminatorValue(ParseNode parseNode) {
        return new TestDateTimeTimeZone();
    }
}

