package com.microsoft.graph.requests;

import com.microsoft.kiota.RequestAdapter;
import com.microsoft.kiota.authentication.AuthenticationProvider;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import javax.annotation.Nonnull;

/**
 * Default client implementation.
 */
class BaseClient implements IBaseClient{

    private RequestAdapter requestAdapter;
    /** RequestBuilder for completing Batch Requests */
    //public BatchRequestBuilder batchRequestBuilder;

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

    @Override
    @SuppressFBWarnings //Suppressing warnings as we intend to expose the RequestAdapter.
    public void setRequestAdapter(@Nonnull final RequestAdapter requestAdapter) {
        this.requestAdapter = requestAdapter;
    }

    @Override
    @SuppressFBWarnings //Suppressing warnings as we intend to expose the RequestAdapter.
    public RequestAdapter getRequestAdapter() {
        return this.requestAdapter;
    }
//Keeping this commented out as we still have to refactor the BatchRequestBuilder.
//    @Override
//    public BatchRequestBuilder getBatchRequestBuilder() {
//        TODO: Refactor BatchRequestBuilder so that it accepts a request adapter as the param
//        return this.batchRequestBuilder != null ? this.batchRequestBuilder : new BatchRequestBuilder(this.requestAdapter)
//        return this.batchRequestBuilder;
//    }
}
