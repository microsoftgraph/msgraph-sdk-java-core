package com.microsoft.graph.Requests;

import com.microsoft.graph.content.BatchRequestBuilder;
import com.microsoft.kiota.RequestAdapter;
import com.microsoft.kiota.authentication.AnonymousAuthenticationProvider;
import com.microsoft.kiota.authentication.AuthenticationProvider;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import okhttp3.OkHttpClient;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.net.URISyntaxException;

@SuppressFBWarnings
class BaseClient implements IBaseClient{

    private RequestAdapter requestAdapter;
    public BatchRequestBuilder batchRequestBuilder;

    BaseClient(@Nonnull RequestAdapter requestAdapter) {
        this.requestAdapter = requestAdapter;
    }

    BaseClient(@Nullable String baseUrl, @Nonnull AuthenticationProvider authenticationProvider) {
        this.requestAdapter = new BaseGraphRequestAdapter(authenticationProvider, null, null, null, null, baseUrl);
    }

    BaseClient(@Nullable String baseUrl, @Nonnull OkHttpClient client) {
        this.requestAdapter = new BaseGraphRequestAdapter(new AnonymousAuthenticationProvider(), null, null, client, null, baseUrl);
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
