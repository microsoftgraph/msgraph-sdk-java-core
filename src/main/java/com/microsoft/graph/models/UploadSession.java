package com.microsoft.graph.models;

import com.microsoft.kiota.serialization.ParseNode;
import com.microsoft.kiota.serialization.SerializationWriter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.function.Consumer;

public class UploadSession implements IUploadSession {

    private String UploadUrl;
    private List<String> NextExpectedRanges;
    private OffsetDateTime ExpirationDateTime;
    private Map<String, Object> AdditionalData;

    @Nonnull
    @Override
    public String getUploadUrl() {
        return this.UploadUrl;
    }
    public void setUploadUrl(@Nonnull String uploadUrl) {
        Objects.requireNonNull(uploadUrl, "Upload url cannot be null");
        this.UploadUrl = uploadUrl;
    }

    @Nonnull
    @Override
    public List<String> getNextExpectedRanges() {
        return this.NextExpectedRanges;
    }
    public void setNextExpectedRanges(@Nonnull List<String> nextExpectedRanges) {
        Objects.requireNonNull(nextExpectedRanges, "Parameter nextExpectedRanges cannot be null");
        this.NextExpectedRanges.addAll(nextExpectedRanges);
    }

    @Nullable
    @Override
    public OffsetDateTime getExpirationDateTime() {
        return this.ExpirationDateTime;
    }
    public void setExpirationDateTime(@Nullable OffsetDateTime expirationDateTime) {
        this.ExpirationDateTime = expirationDateTime;
    }

    @NotNull
    @Override
    public Map<String, Object> getAdditionalData() {
        return this.AdditionalData;
    }

    public void setAdditionalData(@Nonnull Map<String, Object> additionalData) {
        Objects.requireNonNull(additionalData, "Parameter additionalData cannot be null");
        this.AdditionalData.putAll(additionalData);
    }

    @NotNull
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
    public void serialize(@NotNull SerializationWriter writer) {
        Objects.requireNonNull(writer, "Writer parameter cannot be null");
        writer.writeOffsetDateTimeValue("expirationDateTime", getExpirationDateTime());
        writer.writeCollectionOfPrimitiveValues("nextExpectedRanges", getNextExpectedRanges());
        writer.writeStringValue("uploadUrl", getUploadUrl());
        writer.writeAdditionalData(getAdditionalData());
    }


}
