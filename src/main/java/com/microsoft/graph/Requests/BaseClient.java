package com.microsoft.graph.Requests;

import com.microsoft.graph.content.BatchRequestBuilder;
import com.microsoft.kiota.RequestAdapter;
import com.microsoft.kiota.authentication.AuthenticationProvider;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import okhttp3.OkHttpClient;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressFBWarnings
class BaseClient implements IBaseClient{

    private RequestAdapter requestAdapter;
    public BatchRequestBuilder batchRequestBuilder;

    BaseClient(@Nonnull RequestAdapter requestAdapter) {
        setRequestAdapter(requestAdapter);
    }

    BaseClient(@Nonnull AuthenticationProvider authenticationProvider) {
        this(new BaseGraphRequestAdapter(authenticationProvider));
    }

    BaseClient(@Nonnull AuthenticationProvider authenticationProvider, @Nonnull String baseUrl) {
        this(new BaseGraphRequestAdapter(authenticationProvider, baseUrl));
    }

    BaseClient(@Nonnull AuthenticationProvider authenticationProvider, @Nullable BaseGraphRequestAdapter.Clouds nationalCloud, @Nullable String version){
        this(new BaseGraphRequestAdapter(authenticationProvider, nationalCloud, version));
    }

    BaseClient(@Nonnull AuthenticationProvider authenticationProvider, @Nonnull OkHttpClient client, @Nullable GraphClientOptions graphClientOptions) {
        this(new BaseGraphRequestAdapter(authenticationProvider,client, graphClientOptions));
    }

    BaseClient(@Nonnull AuthenticationProvider authenticationProvider, @Nonnull OkHttpClient client, @Nullable GraphClientOptions graphClientOptions, @Nonnull String baseUrl) {
        this(new BaseGraphRequestAdapter(authenticationProvider, client, graphClientOptions, baseUrl));
    }

    BaseClient(@Nonnull AuthenticationProvider authenticationProvider, @Nonnull OkHttpClient client, @Nullable GraphClientOptions graphClientOptions, @Nullable BaseGraphRequestAdapter.Clouds nationalCloud, @Nullable String version) {
        this(new BaseGraphRequestAdapter(authenticationProvider,client, graphClientOptions, nationalCloud, version));
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
