package com.microsoft.graph.Models;

import com.microsoft.kiota.serialization.AdditionalDataHolder;
import com.microsoft.kiota.serialization.Parsable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.OffsetDateTime;
import java.util.List;

public interface IUploadSession extends Parsable, AdditionalDataHolder {
    /**
     * Gets the Upload Url.
     * The URL endpoint that accepts PUT requests for byte ranges of the file.
     * @return the upload Url
     */
    @Nonnull
    String getUploadUrl();
    /**
     * Gets the Next Expected Ranges.
     * A collection of byte ranges that the server is missing for the file. These ranges are zero indexed and of the format 'start-end' (e.g. '0-26' to indicate the first 27 bytes of the file). When uploading files as Outlook attachments, instead of a collection of ranges, this property always indicates a single value '{start}', the location in the file where the next upload should begin.
     * @return the Next Expected Ranges.
     */
    @Nonnull
    List<String> getNextExpectedRanges();
    /**
     * Expiration date of the upload session
     * @return the expiration date.
     */
    @Nullable
    OffsetDateTime getExpirationDateTime();
}
