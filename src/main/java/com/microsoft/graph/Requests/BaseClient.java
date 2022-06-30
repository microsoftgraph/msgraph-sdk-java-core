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
        this.requestAdapter = requestAdapter;
    }

    /**BaseClient Constructors for use with baseUrl */
    BaseClient(@Nullable String baseUrl, @Nonnull AuthenticationProvider authenticationProvider) {
        this(new BaseGraphRequestAdapter(authenticationProvider, baseUrl));
    }

    BaseClient(@Nullable String baseUrl, @Nonnull OkHttpClient client) {
        this(baseUrl, client, null);
    }

    BaseClient(@Nullable String baseUrl, @Nonnull OkHttpClient client, @Nullable GraphClientOptions graphClientOptions) {
        this(new BaseGraphRequestAdapter(client, graphClientOptions, baseUrl));
    }

    /**BaseClient constructors for use with specific national cloud and version */
    BaseClient(@Nonnull BaseGraphRequestAdapter.Clouds nationalCloud, @Nonnull String version, @Nonnull AuthenticationProvider authenticationProvider){
        this(new BaseGraphRequestAdapter(authenticationProvider, nationalCloud, version));
    }

    BaseClient(@Nonnull BaseGraphRequestAdapter.Clouds nationalCloud, @Nonnull String version, @Nonnull OkHttpClient client){
        this(nationalCloud, version, client, null);
    }

    BaseClient(@Nonnull BaseGraphRequestAdapter.Clouds nationalCloud, @Nonnull String version, @Nonnull OkHttpClient client, @Nullable GraphClientOptions graphClientOptions) {
        this(new BaseGraphRequestAdapter(client, graphClientOptions, nationalCloud, version));
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
