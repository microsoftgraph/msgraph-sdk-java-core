package com.microsoft.graph.models;

import javax.annotation.Nullable;

import java.net.URI;

/**
 * Model containing the information from an upload response.
 * @param <T> The type of item contained in the response.
 */
public class UploadResult<T> {
    /**
     * Instantiates a new UploadResult.
     */
    public UploadResult() {}
    /** The UploadSession containing information about the created upload session. */
    @Nullable
    public IUploadSession uploadSession;
    /** The uploaded item, once upload has completed. */
    @Nullable
    public T itemResponse;
    /** The uploaded item location, once upload has completed. */
    @Nullable
    public URI location;
    /**
     * Status of the request.
     * @return A boolean dictating whether the upload has been fully completed.
     */
    public boolean isUploadSuccessful() {
        return (this.itemResponse != null) || (this.location != null);
    }
}
