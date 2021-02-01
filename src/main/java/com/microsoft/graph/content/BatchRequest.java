// ------------------------------------------------------------------------------
// Copyright (c) 2021 Microsoft Corporation
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sub-license, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.
// ------------------------------------------------------------------------------

package com.microsoft.graph.content;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.microsoft.graph.core.ClientException;
import com.microsoft.graph.core.IBaseClient;
import com.microsoft.graph.http.BaseRequest;
import com.microsoft.graph.http.HttpMethod;
import com.microsoft.graph.options.Option;

/** Request for batch requests */
public class BatchRequest extends BaseRequest<BatchResponseContent> {

    /**
     * Instantiates a new batch request
     *
     * @param requestUrl the URL to send the request to
     * @param client the client to use to execute the request
     * @param options the options to apply to the request
     */
    public BatchRequest(@Nonnull final String requestUrl, @Nonnull final IBaseClient client, @Nonnull final List<? extends Option> options) {
        super(requestUrl, client, options, BatchResponseContent.class);
    }

    /**
     * Send this request
     *
     * @return the response object
     * @param content content of the batch request to execute
     * @throws ClientException an exception occurs if there was an error while the request was sent
     */
    @Nullable
    protected BatchResponseContent post(@Nonnull final BatchRequestContent content) throws ClientException {
        this.setHttpMethod(HttpMethod.POST);
        return this.getClient().getHttpProvider().send(this, BatchResponseContent.class, content);
    }
    /**
     * Send this request
     *
     * @return the response object
     * @param content content of the batch request to execute
     * @throws ClientException an exception occurs if there was an error while the request was sent
     */
    @Nullable
    protected java.util.concurrent.CompletableFuture<BatchResponseContent> postAsync(@Nonnull final BatchRequestContent content) throws ClientException {
        this.setHttpMethod(HttpMethod.POST);
        return this.getClient().getHttpProvider().sendAsync(this, BatchResponseContent.class, content);
    }
}
