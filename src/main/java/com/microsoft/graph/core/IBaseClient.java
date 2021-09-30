// ------------------------------------------------------------------------------
// Copyright (c) 2017 Microsoft Corporation
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

package com.microsoft.graph.core;

import javax.annotation.Nullable;
import javax.annotation.Nonnull;

import com.google.gson.JsonElement;
import com.microsoft.graph.content.BatchRequestBuilder;
import com.microsoft.graph.http.IHttpProvider;
import com.microsoft.graph.logger.ILogger;
import com.microsoft.graph.serializer.ISerializer;

/**
 * A client that communications with an OData service
 * @param <nativeRequestType> type of a request for the native http client
 */
public interface IBaseClient<nativeRequestType> {
    /**
     * Gets the service root
     *
     * @return the service root
     */
    @Nonnull
    String getServiceRoot();

    /**
     * Sets the service root
     *
     * @param value the service root
     */
    //void setServiceRoot(@Nonnull final String value);

    /**
     * Gets the HTTP provider
     *
     * @return the HTTP provider
     */
    @Nullable
    IHttpProvider<nativeRequestType> getHttpProvider();

    /**
     * Gets the logger
     *
     * @return the logger
     */
    @Nullable
    ILogger getLogger();

    /**
     * Gets the serializer
     *
     * @return the serializer
     */
    @Nullable
    ISerializer getSerializer();

    /**
     * Gets a builder to execute a custom request
     *
     * @return the custom request builder
     * @param url the url to send the request to
     * @param responseType the class to deserialize the response to
     * @param <T> the type to deserialize the response to
     */
    @Nonnull
    <T> CustomRequestBuilder<T> customRequest(@Nonnull final String url, @Nonnull final Class<T> responseType);

    /**
     * Gets a builder to execute a custom request with a generic JSONObject response
     *
     * @return the custom request builder
     * @param url the url to send the request to
     */
    @Nonnull
    CustomRequestBuilder<JsonElement> customRequest(@Nonnull final String url);

    /**
     * Get the batch request builder.
     * @return a request builder to execute a batch.
     */
    @Nonnull
    BatchRequestBuilder batch();

    /**
     * Gets the service SDK version if the service SDK is in use, null otherwise
     * @return the service SDK version if the service SDK is in use, null otherwise
     */
    @Nullable
    String getServiceSDKVersion();
}
