package com.microsoft.graph.models;

import com.microsoft.kiota.serialization.AdditionalDataHolder;
import com.microsoft.kiota.serialization.Parsable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * Interface defining and UploadSession
 */
public interface IUploadSession extends Parsable, AdditionalDataHolder {
    /**
     * Gets the Upload Url.
     * The URL endpoint that accepts PUT requests for byte ranges of the file.
     * @return the upload Url
     */
    @Nonnull
    String getUploadUrl();
    /**
     * Sets the Upload Url
     * @param url the upload Url for the session
     */
    void setUploadUrl(@Nonnull String url);
    /**
     * Gets the Next Expected Ranges.
     * A collection of byte ranges that the server is missing for the file. These ranges are zero indexed and of the format 'start-end' (e.g. '0-26' to indicate the first 27 bytes of the file). When uploading files as Outlook attachments, instead of a collection of ranges, this property always indicates a single value '{start}', the location in the file where the next upload should begin.
     * @return the Next Expected Ranges.
     */
    @Nonnull
    List<String> getNextExpectedRanges();
    /**
     * Sets the ranges that are yet to be uploaded.
     * @param nextExpectedRanges the byte ranges yet to be uploaded.
     */
    void setNextExpectedRanges(@Nonnull List<String> nextExpectedRanges);
    /**
     * Expiration date of the upload session
     * @return the expiration date.
     */
    @Nullable
    OffsetDateTime getExpirationDateTime();
    /**
     * Set the expiration date of the UploadSession
     * @param dateTime the expiration date of the UploadSession.
     */
    void setExpirationDateTime(@Nonnull OffsetDateTime dateTime);
}
