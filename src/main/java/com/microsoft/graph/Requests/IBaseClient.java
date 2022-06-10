package com.microsoft.graph.Requests;

import com.microsoft.graph.content.BatchRequestBuilder;
import com.microsoft.kiota.RequestAdapter;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings
public interface IBaseClient {

    public void setRequestAdapter(RequestAdapter requestAdapter);

    public RequestAdapter getRequestAdapter();

    public BatchRequestBuilder getBatchRequestBuilder();
}
