package com.microsoft.graph.Requests;

import com.microsoft.graph.content.BatchRequestBuilder;
import com.microsoft.kiota.RequestAdapter;

/**
 * The default client interface
 */
public interface IBaseClient {

    /**
     * Method to set the RequestAdapter property
     *
     * @param requestAdapter specifies the desired RequestAdapter
     */
    public void setRequestAdapter(RequestAdapter requestAdapter);

    /**
     * Returns the current RequestAdapter for sending requests
     *
     * @return the RequestAdapter currently in use
     */
    public RequestAdapter getRequestAdapter();

    /**
     * Gets the BatchRequestBuilder for use in Batch Requests
     *
     * @return the BatchRequestBuilder instance
     */
    public BatchRequestBuilder getBatchRequestBuilder();
}
