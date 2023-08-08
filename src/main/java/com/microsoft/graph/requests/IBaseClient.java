package com.microsoft.graph.requests;

import com.microsoft.kiota.RequestAdapter;

import jakarta.annotation.Nonnull;

/**
 * The default client interface
 */
public interface IBaseClient {
    /**
     * Method to set the RequestAdapter property
     * @param requestAdapter specifies the desired RequestAdapter
     */
    void setRequestAdapter(@Nonnull RequestAdapter requestAdapter);
    /**
     * Returns the current RequestAdapter for sending requests
     * @return the RequestAdapter currently in use
     */
    @Nonnull
    RequestAdapter getRequestAdapter();
    /**
     * Gets the BatchRequestBuilder
     * @return the BatchRequestBuilder instance
     */
    @Nonnull
    BatchRequestBuilder getBatchRequestBuilder();
}
