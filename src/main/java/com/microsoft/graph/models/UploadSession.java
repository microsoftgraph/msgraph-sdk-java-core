package com.microsoft.graph.models;

import com.microsoft.graph.exceptions.ErrorConstants;
import com.microsoft.kiota.serialization.ParseNode;
import com.microsoft.kiota.serialization.SerializationWriter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.function.Consumer;

/**
 * Concrete implementation of the IUploadSession interface.
 */
public class UploadSession implements IUploadSession {
    /** The URL for upload. */
    private String uploadUrl ="";
    /** The ranges yet to be uploaded to the server. */
    private List<String> nextExpectedRanges;
    /** Expiration date of the upload session. */
    private OffsetDateTime expirationDateTime;
    /** Stores additional data not described in the OpenAPI description found when deserializing. Can be used for serialization as well. */
    private Map<String, Object> additionalData = new HashMap<>();
    /**
     * Instantiates a new uploadSession and sets the default values.
     */
    @Nullable
    public UploadSession() {
        this.setAdditionalData(new HashMap<>());
    }
    /**
     * Get the upload url of the upload session.
     * @return The upload Url.
     */
    @Nonnull
    @Override
    public String getUploadUrl() {
        return this.uploadUrl;
    }
    /**
     * Set the upload url of the upload session.
     * @param uploadUrl The upload url for the session.
     */
    public void setUploadUrl(@Nonnull final String uploadUrl) {
        Objects.requireNonNull(uploadUrl, ErrorConstants.Messages.NULL_PARAMETER + "uploadUrl" );
        if(uploadUrl.isEmpty())
            throw new IllegalArgumentException("uploadUrl cannot be empty");
        this.uploadUrl = uploadUrl;
    }
    /**
     * Get the next upload byte ranges to be uploaded.
     * @return The byte ranges to be uploaded.
     */
    @Nonnull
    @Override
    public List<String> getNextExpectedRanges() {
        return new ArrayList<>(nextExpectedRanges);
    }
    /**
     * Set the byte ranges yet to be uploaded.
     * @param nextExpectedRanges The byte ranges yet to be uploaded.
     */
    @Override
    public void setNextExpectedRanges(@Nonnull final List<String> nextExpectedRanges) {
        Objects.requireNonNull(nextExpectedRanges, ErrorConstants.Messages.NULL_PARAMETER + "nextExpectedRanges");
        this.nextExpectedRanges = new ArrayList<>(nextExpectedRanges);
    }
    /**
     * Get the time at which the session expires.
     * @return The session expiration time.
     */
    @Nullable
    @Override
    public OffsetDateTime getExpirationDateTime() {
        return this.expirationDateTime;
    }
    /**
     * Set the time at which the session expires.
     * @param expirationDateTime The session expiration time.
     */
    @Override
    public void setExpirationDateTime(@Nullable final OffsetDateTime expirationDateTime) {
        this.expirationDateTime = expirationDateTime;
    }
    /**
     * Get the additional data found.
     * @return The additional data.
     */
    @Nonnull
    @Override
    public Map<String, Object> getAdditionalData() {
        return new HashMap<>(this.additionalData);
    }
    /**
     * Sets the additionalData property value. Stores additional data not described in the OpenAPI description found when deserializing. Can be used for serialization as well.
     * @param additionalData The AdditionalData to set.
     */
    public void setAdditionalData(@Nonnull final Map<String, Object> additionalData) {
        Objects.requireNonNull(additionalData, ErrorConstants.Messages.NULL_PARAMETER + "additionalData");
        this.additionalData = new HashMap<>(additionalData);
    }
    /**
     * The deserialization information for the current model.
     * @return A hash map describing how to deserialize the current model fields.
     */
    @Nonnull
    @Override
    public Map<String, Consumer<ParseNode>> getFieldDeserializers() {
        final UploadSession currentObj = this;
        HashMap<String, Consumer<ParseNode>> deserializers = new HashMap<>(3);
        deserializers.put("expirationDateTime", n -> currentObj.setExpirationDateTime(n.getOffsetDateTimeValue()));
        deserializers.put("nextExpectedRanges", n -> currentObj.setNextExpectedRanges(n.getCollectionOfPrimitiveValues(String.class)));
        deserializers.put("uploadUrl", n -> currentObj.setUploadUrl(n.getStringValue()));
        return deserializers;
    }
    /**
     * Serializes information the current object.
     * @param writer Serialization writer to use to serialize this model.
     */
    @Override
    public void serialize(@Nonnull final SerializationWriter writer) {
        Objects.requireNonNull(writer, ErrorConstants.Messages.NULL_PARAMETER + "writer");
        writer.writeOffsetDateTimeValue("expirationDateTime", getExpirationDateTime());
        writer.writeCollectionOfPrimitiveValues("nextExpectedRanges", getNextExpectedRanges());
        writer.writeStringValue("uploadUrl", getUploadUrl());
        writer.writeAdditionalData(getAdditionalData());
    }
    /**
     * Creates a new instance of the current model based on discriminator value.
     * @param parseNode The parse node to use to read the discriminator value and create the object.
     * @return an uploadSession
     */
    @Nonnull
    public static UploadSession createFromDiscriminatorValue(@Nonnull final ParseNode parseNode){
        Objects.requireNonNull(parseNode);
        return new UploadSession();
    }
}
