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

package com.microsoft.graph.http;

import java.net.URL;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;
import javax.annotation.Nonnull;

import com.microsoft.graph.core.ClientException;
import com.microsoft.graph.core.IBaseClient;
import com.microsoft.graph.httpcore.middlewareoption.IShouldRedirect;
import com.microsoft.graph.httpcore.middlewareoption.IShouldRetry;
import com.microsoft.graph.options.FunctionOption;
import com.microsoft.graph.options.HeaderOption;
import com.microsoft.graph.options.Option;
import com.microsoft.graph.options.QueryOption;

/**
 * A request against a collection
 *
 * @param <T> the type of the object in the collection
 * @param <T2> the response collection type
 * @param <T3> the collection page type
 */
public abstract class BaseCollectionRequest<T, T2 extends ICollectionResponse<T>,
                                            T3 extends BaseCollectionPage<T, ? extends BaseRequestBuilder<T>>> implements IHttpRequest {

    /**
     * The base request for this collection request
     */
    private final BaseRequest<T2> baseRequest;

    /**
     * The class for the response collection
     */
    protected final Class<T2> responseCollectionClass;

    /**
     * The class for the collection page
     */
    private final Class<T3> collectionPageClass;

    private final Class<? extends BaseCollectionRequestBuilder<T, ? extends BaseRequestBuilder<T>, T2, T3, ? extends BaseCollectionRequest<T, T2, T3>>> collRequestBuilderClass;


    /**
     * Create the collection request
     *
     * @param requestUrl          the URL to make the request against
     * @param client              the client which can issue the request
     * @param options             the options for this request
     * @param responseCollectionClass       the class for the response collection
     * @param collectionPageClass the class for the collection page
     * @param collectionRequestBuilderClass the class for the collection request builder
     */
    public BaseCollectionRequest(@Nonnull final String requestUrl,
                                 @Nonnull final IBaseClient<?> client,
                                 @Nullable final List<? extends Option> options,
                                 @Nonnull final Class<T2> responseCollectionClass,
                                 @Nonnull final Class<T3> collectionPageClass,
                                 @Nonnull final Class<? extends BaseCollectionRequestBuilder<T, ? extends BaseRequestBuilder<T>, T2, T3, ? extends BaseCollectionRequest<T, T2, T3>>> collectionRequestBuilderClass) {
        this.responseCollectionClass = Objects.requireNonNull(responseCollectionClass, "parameter responseCollectionClass cannot be null");
        this.collectionPageClass = Objects.requireNonNull(collectionPageClass, "parameter collectionPageClass cannot be null");
        this.collRequestBuilderClass = Objects.requireNonNull(collectionRequestBuilderClass, "parameter collectionRequestBuilderClass cannot be null");
        baseRequest = new BaseRequest<T2>(requestUrl, client, options, responseCollectionClass) {};
    }

    /**
     * Send this request
     *
     * @return the response object
     * @throws ClientException an exception occurs if there was an error while the request was sent
     */
    @Nullable
    protected T2 send() throws ClientException {
        baseRequest.setHttpMethod(HttpMethod.GET);
        return baseRequest.getClient().getHttpProvider().send(this, responseCollectionClass, null);
    }
    /**
     * Send this request
     *
     * @return the response object
     * @throws ClientException an exception occurs if there was an error while the request was sent
     */
    @Nullable
    protected java.util.concurrent.CompletableFuture<T2> sendAsync() throws ClientException {
        baseRequest.setHttpMethod(HttpMethod.GET);
        return baseRequest.getClient().getHttpProvider().sendAsync(this, responseCollectionClass, null);
    }

    /**
     * Deserializes the collection from the response object
     *
     * @param response the collection response
     * @return the collection page
     */
    @Nullable
    public T3 buildFromResponse(@Nonnull final T2 response) {
        Objects.requireNonNull(response, "parameter response cannot be null");
        try {
            final Object builder = response.nextLink() == null ? null : this.collRequestBuilderClass
                    .getConstructor(String.class, IBaseClient.class, java.util.List.class)
                    .newInstance(response.nextLink(), getBaseRequest().getClient(), Collections.emptyList());
            final T3 page = (T3)this.collectionPageClass.getConstructor(response.getClass(), this.collRequestBuilderClass).newInstance(response, builder);
            return page;
        } catch(IllegalArgumentException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | SecurityException ex) {
            throw new ClientException("Could not find the required class", ex);
        }
    }

    /**
     * Gets the request URL
     *
     * @return the request URL
     */
    @Override
    @Nonnull
    public URL getRequestUrl() {
        return baseRequest.getRequestUrl();
    }

    /**
     * Gets the HTTP method
     *
     * @return the HTTP method
     */
    @Override
    @Nullable
    public HttpMethod getHttpMethod() {
        return baseRequest.getHttpMethod();
    }

    /**
     * Gets the headers
     *
     * @return the headers
     */
    @Override
    @Nullable
    public List<HeaderOption> getHeaders() {
        return baseRequest.getHeaders();
    }

    /**
     * Adds a header to this request
     *
     * @param header the name of the header
     * @param value  the value of the header
     */
    @Override
    public void addHeader(@Nonnull final String header, @Nullable final String value) {
        Objects.requireNonNull(header, "parameter header cannot be null");
        baseRequest.addHeader(header, value);
    }

    /**
     * Sets useCaches parameter to cache the response
     *
     * @param useCaches the value of useCaches
     */
    @Override
    public void setUseCaches(boolean useCaches) {
        baseRequest.setUseCaches(useCaches);
    }

    /**
     * Gets useCaches parameter
     *
     * @return the value of useCaches
     */
    @Override
    public boolean getUseCaches() {
        return baseRequest.getUseCaches();
    }

    /**
     * Gets the full list of options for this request
     *
     * @return the full list of options for this request
     */
    @Nullable
    public List<Option> getOptions() {
        return baseRequest.getOptions();
    }

    /**
     * Adds a query option
     *
     * @param option the query option to add
     */
    public void addQueryOption(@Nonnull final QueryOption option) {
        Objects.requireNonNull(option, "parameter option cannot be null");
        baseRequest.getQueryOptions().add(option);
    }

    /**
     * Sets the expand clause for the request
     *
     * @param value the expand clause
     */
    protected void addExpandOption(@Nonnull final String value) {
        Objects.requireNonNull(value, "parameter value cannot be null");
        addQueryOption(new QueryOption("$expand", value));
    }

    /**
     * Sets the filter clause for the request
     *
     * @param value the filter clause
     */
    protected void addFilterOption(@Nonnull final String value) {
        Objects.requireNonNull(value, "parameter value cannot be null");
        addQueryOption(new QueryOption("$filter", value));
    }

    /**
     * Sets the order by clause for the request
     *
     * @param value the order by clause
     */
    protected void addOrderByOption(@Nonnull final String value) {
        Objects.requireNonNull(value, "parameter value cannot be null");
        addQueryOption(new QueryOption("$orderby", value));
    }

    /**
     * Sets the select clause for the request
     *
     * @param value the select clause
     */
    protected void addSelectOption(@Nonnull final String value) {
        Objects.requireNonNull(value, "parameter value cannot be null");
        addQueryOption(new QueryOption("$select", value));
    }

    /**
     * Sets the top value for the request
     *
     * @param value the max number of items to return
     */
    protected void addTopOption(final int value) {
        addQueryOption(new QueryOption("$top", String.valueOf(value)));
    }

    /**
     * Sets the skip value for the request
     *
     * @param value of the number of items to skip
     */
    protected void addSkipOption(final int value) {
        addQueryOption(new QueryOption("$skip", String.valueOf(value)));
    }


    /**
     * Add Skip token for pagination
     * @param skipToken - Token for pagination
     */
    protected void addSkipTokenOption(@Nonnull final String skipToken) {
    	Objects.requireNonNull(skipToken, "parameter skipToken cannot be null");
        addQueryOption(new QueryOption("$skiptoken", skipToken));
    }

    /**
     * Adds the count query string value for the request
     * @param value - Wheter to return the count or not
     */
    protected void addCountOption(final boolean value) {
        addQueryOption(new QueryOption("$count", String.valueOf(value)));
    }

    /**
     * Adds a query option
     *
     * @param option the query option to add
     */
    public void addFunctionOption(@Nonnull final FunctionOption option) {
        Objects.requireNonNull(option, "parameter option cannot be null");
        baseRequest.getFunctionOptions().add(option);
    }

    /**
     * Gets the base request for this collection request
     *
     * @return the base request for this collection request
     */
    @Nonnull
    public BaseRequest<T2> getBaseRequest() {
        return baseRequest;
    }

    /**
     * Gets the class for the collection page
     *
     * @return the class for the collection page
     */
    @Nonnull
    public Class<T3> getCollectionPageClass() {
        return collectionPageClass;
    }

    /**
     * Sets the max redirects
     *
     * @param maxRedirects Max redirects that a request can take
     */
    public void setMaxRedirects(int maxRedirects) {
    	baseRequest.setMaxRedirects(maxRedirects);
    }

    /**
     * Gets the max redirects
     *
     * @return Max redirects that a request can take
     */
    public int getMaxRedirects() {
    	return baseRequest.getMaxRedirects();
    }

    /**
     * Sets the should redirect callback
     *
     * @param shouldRedirect Callback called before doing a redirect
     */
    public void setShouldRedirect(@Nonnull IShouldRedirect shouldRedirect) {
    	Objects.requireNonNull(shouldRedirect, "parameter shouldRedirect cannot be null");
        baseRequest.setShouldRedirect(shouldRedirect);
    }

    /**
     * Gets the should redirect callback
     *
     * @return Callback which is called before redirect
     */
    @Nonnull
    public IShouldRedirect getShouldRedirect() {
    	return baseRequest.getShouldRedirect();
    }

    /**
     * Sets the should retry callback
     *
     * @param shouldretry The callback called before retry
     */
    public void setShouldRetry(@Nonnull IShouldRetry shouldretry) {
    	Objects.requireNonNull(shouldretry, "parameter shouldretry cannot be null");
        baseRequest.setShouldRetry(shouldretry);
    }

    /**
     * Gets the should retry callback
     *
     * @return Callback called before retry
     */
    @Nonnull
    public IShouldRetry getShouldRetry() {
    	return baseRequest.getShouldRetry();
    }

    /**
     * Sets the max retries
     *
     * @param maxRetries Max retries for a request
     */
    public void setMaxRetries(int maxRetries) {
    	baseRequest.setMaxRetries(maxRetries);
    }

    /**
     * Gets max retries
     *
     * @return Max retries for a request
     */
    public int getMaxRetries() {
    	return baseRequest.getMaxRetries();
    }

    /**
     * Sets the delay in seconds between retires
     *
     * @param delay Delay in seconds between retries
     */
    public void setDelay(long delay) {
    	baseRequest.setDelay(delay);
    }

    /**
     * Gets delay between retries
     *
     * @return Delay between retries in seconds
     */
    public long getDelay() {
    	return baseRequest.getDelay();
    }

    /**
     * Sets the HTTP method and returns the current request
     *
     * @param httpMethod the HTTP method
     * @return the current request
     */
    @Nullable
    public IHttpRequest withHttpMethod(@Nonnull final HttpMethod httpMethod) {
        Objects.requireNonNull(httpMethod, "parameter httpMethod cannot be null");
        baseRequest.setHttpMethod(httpMethod);
        return this;
    }

    /**
     * Returns the Request object to be executed
     * @param serializedObject the object to serialize at the body of the request
     * @param <requestBodyType> the type of the serialized object
     * @param <responseType> the type of the response
     * @param <nativeRequestType> type of a request for the native http client
     * @return the Request object to be executed
     */
    @Override
    @Nullable
    public <requestBodyType, responseType, nativeRequestType> nativeRequestType getHttpRequest(@Nullable final requestBodyType serializedObject) throws ClientException {
        return baseRequest.getHttpRequest(serializedObject);
    }
}
