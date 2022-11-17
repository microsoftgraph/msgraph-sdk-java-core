package com.microsoft.graph.requests;

//import com.microsoft.graph.content.BatchRequestBuilder;
import com.microsoft.kiota.RequestAdapter;

import javax.annotation.Nonnull;

/**
 * The default client interface
 */
public interface IBaseClient {

    /**
     * Method to set the RequestAdapter property
     *
     * @param requestAdapter specifies the desired RequestAdapter
     */
    public void setRequestAdapter(@Nonnull RequestAdapter requestAdapter);

    /**
     * Returns the current RequestAdapter for sending requests
     *
     * @return the RequestAdapter currently in use
     */
    @Nonnull
    public RequestAdapter getRequestAdapter();

    /**
     * Gets the BatchRequestBuilder for use in Batch Requests
     *
     * @return the BatchRequestBuilder instance
     */
    //public BatchRequestBuilder getBatchRequestBuilder();
}
