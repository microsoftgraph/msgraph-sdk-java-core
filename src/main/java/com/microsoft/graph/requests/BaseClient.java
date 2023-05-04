package com.microsoft.graph.requests;

import com.microsoft.kiota.RequestAdapter;
import com.microsoft.kiota.authentication.AuthenticationProvider;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

/**
 * Default client implementation.
 */
public class BaseClient implements IBaseClient{

    private RequestAdapter requestAdapter;

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
    public BaseClient(@Nonnull AuthenticationProvider authenticationProvider) {
        this(new BaseGraphRequestAdapter(authenticationProvider));
    }
    /**
     * Constructor requiring an AuthenticationProvider and Base URL.
     *
     * @param authenticationProvider the specified AuthenticationProvider for use in requests.
     * @param baseUrl the specified base URL for use in requests.
     */
    public BaseClient(@Nonnull AuthenticationProvider authenticationProvider, @Nonnull String baseUrl) {
        this(new BaseGraphRequestAdapter(authenticationProvider, baseUrl));
    }

    /**
     * Method to set the RequestAdapter property
     * @param requestAdapter specifies the desired RequestAdapter
     */
    @Override
    @SuppressFBWarnings //Suppressing warnings as we intend to expose the RequestAdapter.
    public void setRequestAdapter(@Nonnull final RequestAdapter requestAdapter) {
        this.requestAdapter = requestAdapter;
    }

    /**
     * Returns the current RequestAdapter for sending requests
     * @return the RequestAdapter currently in use
     */
    @NotNull
    @Override
    @SuppressFBWarnings //Suppressing warnings as we intend to expose the RequestAdapter.
    public RequestAdapter getRequestAdapter() {
        return this.requestAdapter;
    }

    /**
     * Gets the BatchRequestBuilder
     * @return the BatchRequestBuilder instance
     */
    @Override
    public BatchRequestBuilder getBatchRequestBuilder() {
        return new BatchRequestBuilder(this.requestAdapter);
    }
}
