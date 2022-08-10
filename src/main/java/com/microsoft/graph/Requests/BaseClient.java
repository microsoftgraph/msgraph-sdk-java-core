package com.microsoft.graph.Requests;

import com.microsoft.graph.content.BatchRequestBuilder;
import com.microsoft.kiota.RequestAdapter;
import com.microsoft.kiota.authentication.AuthenticationProvider;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import okhttp3.OkHttpClient;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Default client implementation.
 */
public class BaseClient implements IBaseClient{

    private RequestAdapter requestAdapter;
    /** RequestBuilder for completing Batch Requests */
    public BatchRequestBuilder batchRequestBuilder;

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
    public void setRequestAdapter(RequestAdapter requestAdapter) {
        this.requestAdapter = requestAdapter;
    }

    @Override
    public RequestAdapter getRequestAdapter() {
        return this.requestAdapter;
    }

    @Override
    public BatchRequestBuilder getBatchRequestBuilder() {
        //TODO: Refactor BatchRequestBuilder so that it accepts a request adapter as the param
        //return this.batchRequestBuilder != null ? this.batchRequestBuilder : new BatchRequestBuilder(this.requestAdapter)
        return this.batchRequestBuilder;
    }
}
