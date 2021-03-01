// ------------------------------------------------------------------------------
// Copyright (c) Microsoft Corporation.  All Rights Reserved.  Licensed under the MIT License.  See License in the project root for license information.
// ------------------------------------------------------------------------------

package com.microsoft.graph.tasks;

import com.microsoft.graph.core.ClientException;
import com.microsoft.graph.http.GraphServiceException;

import javax.annotation.Nullable;

import java.util.Objects;

import javax.annotation.Nonnull;

/**
 * Wrapper class for different upload response from server.
 */
class LargeFileUploadResponse<UploadType> {
    /**
     * The location header from the response if provided
     */
    private final String location;
    /**
     * The uploaded item response.
     */
    private final UploadType uploadedItem;

    /**
     * The next session response.
     */
    private final IUploadSession session;

    /**
     * The error happened during upload.
     */
    private final ClientException error;

    /**
     * Constructs response with the location header.
     *
     * @param location The location returned by the response
     */
    protected LargeFileUploadResponse(@Nullable final String location) {
        this.location = location;
        this.error = null;
        this.session = null;
        this.uploadedItem = null;
    }

    /**
     * Construct response with item created.
     *
     * @param uploaded The created item.
     */
    protected LargeFileUploadResponse(@Nullable final UploadType uploaded) {
        this.uploadedItem = uploaded;
        this.session = null;
        this.error = null;
        this.location = null;
    }

    /**
     * Construct response with next session.
     *
     * @param session The next session.
     */
    protected LargeFileUploadResponse(@Nullable final IUploadSession session) {
        this.session = session;
        this.uploadedItem = null;
        this.error = null;
        this.location = null;
    }

    /**
     * Construct response with error.
     *
     * @param error The error occurred during uploading.
     */
    protected LargeFileUploadResponse(@Nullable final ClientException error) {
        this.error = error;
        this.uploadedItem = null;
        this.session = null;
        this.location = null;
    }

    /**
     * Construct response with server exception.
     *
     * @param exception The exception received from server.
     */
    protected LargeFileUploadResponse(@Nonnull final GraphServiceException exception) {
        this(new ClientException(Objects
                                .requireNonNull(exception, "parameter exception cannot be null")
                                .getMessage(/* verbose */ true),
                                exception));
    }

    /**
     * Checks the large upload range is completed.
     *
     * @return true if current large upload range is completed.
     */
    public boolean chunkCompleted() {
        return this.uploadedItem != null || this.session != null;
    }

    /**
     * Checks the whole upload is completed.
     *
     * @return true if the response is an item.
     */
    public boolean uploadCompleted() {
        return this.uploadedItem != null || this.location != null;
    }

    /**
     * Checks if an error happened.
     *
     * @return true if current request has error.
     */
    public boolean hasError() {
        return this.error != null;
    }

    /**
     * Get the uploaded item.
     *
     * @return The item.
     */
    @Nullable
    public UploadType getItem() {
        return this.uploadedItem;
    }

    /**
     * Get the next session.
     *
     * @return The next session for uploading.
     */
    @Nullable
    public IUploadSession getSession() {
        return this.session;
    }

    /**
     * Get the error.
     *
     * @return The error.
     */
    @Nullable
    public ClientException getError() {
        return this.error;
    }
    /**
     * Get the location.
     * @return The location.
     */
    @Nullable
    public String getLocation () {
        return this.location;
    }
}
