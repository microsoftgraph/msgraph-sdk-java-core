package com.microsoft.graph.concurrency;

import java.util.List;

import javax.annotation.Nullable;

/**
 * The interface for the Upload Session.
 */
public interface IUploadSession {

    /**
     * Gets the Upload Url.
     * The URL endpoint that accepts PUT requests for byte ranges of the file.
     * @return the upload Url
     */
    @Nullable
    String getUploadUrl();
    /**
     * Gets the Next Expected Ranges.
     * A collection of byte ranges that the server is missing for the file. These ranges are zero indexed and of the format 'start-end' (e.g. '0-26' to indicate the first 27 bytes of the file). When uploading files as Outlook attachments, instead of a collection of ranges, this property always indicates a single value '{start}', the location in the file where the next upload should begin.
     * @return the Next Expected Ranges.
     */
    @Nullable
    List<String> getNextExpectedRanges();
}
