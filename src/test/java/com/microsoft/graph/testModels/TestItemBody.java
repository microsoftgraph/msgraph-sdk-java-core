package com.microsoft.graph.testModels;

import com.microsoft.kiota.serialization.AdditionalDataHolder;
import com.microsoft.kiota.serialization.Parsable;
import com.microsoft.kiota.serialization.ParseNode;
import com.microsoft.kiota.serialization.SerializationWriter;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.util.HashMap;
import java.util.function.Consumer;

@SuppressFBWarnings
public class TestItemBody implements Parsable, AdditionalDataHolder {

    private TestBodyType contentType;
    private String content;
    private HashMap<String, Object> additionalData = new HashMap<>();
    private String oDataType;

    public TestItemBody() {
        this.oDataType = "microsoft.graph.itemBody";
    }

    public TestBodyType getContentType() {
        return contentType;
    }

    public void setContentType(TestBodyType contentType) {
        this.contentType = contentType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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
        return new HashMap<String, Consumer<ParseNode>>() {{
            put("@odata.type", (n) -> { oDataType = n.getStringValue(); });
            put("contentType", (n) -> { contentType = n.getEnumValue(TestBodyType.class); });
            put("content", (n) -> { content = n.getStringValue(); });
        }};
    }

    public void serialize(SerializationWriter writer) {
        if (writer == null) {
            throw new IllegalArgumentException("writer cannot be null");
        }
        writer.writeStringValue("@odata.type", oDataType);
        writer.writeEnumValue("contentType", contentType);
        writer.writeStringValue("content", content);
        writer.writeAdditionalData(additionalData);
    }

    public static TestItemBody createFromDiscriminatorValue(ParseNode parseNode) {
        if (parseNode == null) {
            throw new IllegalArgumentException("parseNode cannot be null");
        }
        return new TestItemBody();
    }
}
