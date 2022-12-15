package com.microsoft.graph.requests.upload;

import com.microsoft.graph.models.IUploadSession;
import com.microsoft.kiota.HttpMethod;
import com.microsoft.kiota.RequestAdapter;
import com.microsoft.kiota.RequestInformation;

import java.util.concurrent.CompletableFuture;

public class UploadSessionRequestBuilder {

    private UploadResponseHandler responseHandler;
    private RequestAdapter requestAdapter;
    private String urlTemplate;

    public UploadSessionRequestBuilder(IUploadSession uploadSession, RequestAdapter requestAdapter) {
        this.responseHandler = new UploadResponseHandler(null);
        this.requestAdapter = requestAdapter;
        this.urlTemplate = uploadSession.getUploadUrl();
    }

    public CompletableFuture<IUploadSession> GetAsync() {
        RequestInformation requestInformation = CreateGetRequestInformation();
        requestInformation.addRequestOptions();
    }

    public RequestInformation CreateGetRequestInformation() {
        RequestInformation requestInformation = new RequestInformation() {{
            httpMethod = HttpMethod.GET;
            urlTemplate = this.urlTemplate;
        }};
        return requestInformation;
    }



}
