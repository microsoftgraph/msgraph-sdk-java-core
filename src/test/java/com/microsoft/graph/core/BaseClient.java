package com.microsoft.graph.core;

import com.microsoft.graph.core.requests.BaseGraphRequestAdapter;
import com.microsoft.graph.core.requests.BatchRequestBuilder;
import com.microsoft.graph.core.requests.IBaseClient;
import com.microsoft.kiota.RequestAdapter;
import com.microsoft.kiota.authentication.AuthenticationProvider;

import jakarta.annotation.Nonnull;

/**
 * Default client implementation.
 */
public class BaseClient implements IBaseClient {

    private RequestAdapter requestAdapter;
    private BatchRequestBuilder batchRequestBuilder;

    /**
     * Constructor requiring only a RequestAdapter.
     *
     * @param requestAdapter the specified RequestAdapter used to complete requests.
     */
    public BaseClient(@Nonnull RequestAdapter requestAdapter) {
        setRequestAdapter(requestAdapter);
    }
    /**
     * Constructor requiring only an AuthenticationProvider.
     *
     * @param authenticationProvider the specified AuthenticationProvider for use in requests.
     */
    @SuppressWarnings("LambdaLast")
    public BaseClient(@Nonnull AuthenticationProvider authenticationProvider) {
        this(new BaseGraphRequestAdapter(authenticationProvider));
    }
    /**
     * Constructor requiring an AuthenticationProvider and Base URL.
     *
     * @param authenticationProvider the specified AuthenticationProvider for use in requests.
     * @param baseUrl the specified base URL for use in requests.
     */
    @SuppressWarnings("LambdaLast")
    public BaseClient(@Nonnull AuthenticationProvider authenticationProvider, @Nonnull String baseUrl) {
        this(new BaseGraphRequestAdapter(authenticationProvider, baseUrl));
    }

    /**
     * Method to set the RequestAdapter property
     * @param requestAdapter specifies the desired RequestAdapter
     */
    @Override
    public void setRequestAdapter(@Nonnull final RequestAdapter requestAdapter) {
        this.requestAdapter = requestAdapter;
    }

    /**
     * Returns the current RequestAdapter for sending requests
     * @return the RequestAdapter currently in use
     */
    @Nonnull
    @Override
    public RequestAdapter getRequestAdapter() {
        return this.requestAdapter;
    }

    /**
     * Gets the BatchRequestBuilder
     * @return the BatchRequestBuilder instance
     */
    @Override
    @Nonnull
    public BatchRequestBuilder getBatchRequestBuilder() {
        if(this.batchRequestBuilder == null) {
            this.batchRequestBuilder = new BatchRequestBuilder(getRequestAdapter());
        }
        return this.batchRequestBuilder;
    }
}
