package com.microsoft.graph.models;

import com.microsoft.kiota.serialization.Parsable;
import com.microsoft.kiota.serialization.ParseNode;
import com.microsoft.kiota.serialization.SerializationWriter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.function.Consumer;

public class UploadSession implements IUploadSession {

    private String uploadUrl;
    private List<String> nextExpectedRanges;
    private OffsetDateTime expirationDateTime;
    private Map<String, Object> additionalData;

    @Nonnull
    @Override
    public String getUploadUrl() {
        return this.uploadUrl;
    }
    public void setUploadUrl(@Nonnull String uploadUrl) {
        Objects.requireNonNull(uploadUrl, "Upload url cannot be null");
        this.uploadUrl = uploadUrl;
    }

    @Nonnull
    @Override
    public List<String> getNextExpectedRanges() {
        return this.nextExpectedRanges;
    }
    @Override
    public void setNextExpectedRanges(@Nonnull List<String> nextExpectedRanges) {
        Objects.requireNonNull(nextExpectedRanges, "Parameter nextExpectedRanges cannot be null");
        this.nextExpectedRanges.addAll(nextExpectedRanges);
    }

    @Nullable
    @Override
    public OffsetDateTime getExpirationDateTime() {
        return this.expirationDateTime;
    }
    @Override
    public void setExpirationDateTime(@Nullable OffsetDateTime expirationDateTime) {
        this.expirationDateTime = expirationDateTime;
    }

    @Nonnull
    @Override
    public Map<String, Object> getAdditionalData() {
        return this.additionalData;
    }

    public void setAdditionalData(@Nonnull Map<String, Object> additionalData) {
        Objects.requireNonNull(additionalData, "Parameter additionalData cannot be null");
        this.additionalData.putAll(additionalData);
    }

    @Nonnull
    @Override
    public Map<String, Consumer<ParseNode>> getFieldDeserializers() {
        final UploadSession currentObj = this;
        return new HashMap<String, Consumer<ParseNode>>(3){{
            this.put("expirationDateTime", (n) -> { currentObj.setExpirationDateTime(n.getOffsetDateTimeValue()); });
            this.put("nextExpectedRanges", (n) -> { currentObj.setNextExpectedRanges(n.getCollectionOfPrimitiveValues(String.class)); });
            this.put("uploadUrl", (n) -> { currentObj.setUploadUrl(n.getStringValue()); });
        }};
    }

    @Override
    public void serialize(@Nonnull SerializationWriter writer) {
        Objects.requireNonNull(writer, "Writer parameter cannot be null");
        writer.writeOffsetDateTimeValue("expirationDateTime", getExpirationDateTime());
        writer.writeCollectionOfPrimitiveValues("nextExpectedRanges", getNextExpectedRanges());
        writer.writeStringValue("uploadUrl", getUploadUrl());
        writer.writeAdditionalData(getAdditionalData());
    }

    public static UploadSession createFromDiscriminatorValue(ParseNode parseNode){
        Objects.requireNonNull(parseNode);
        return new UploadSession();
    }


}
