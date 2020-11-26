package com.microsoft.graph.httpcore;

import com.microsoft.graph.httpcore.middlewareoption.IShouldRedirect;
import com.microsoft.graph.httpcore.middlewareoption.IShouldRetry;
import com.microsoft.graph.options.HeaderOption;
import com.microsoft.graph.options.Option;

import java.net.URL;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * An HTTP request
 */
public interface IHttpRequest {

    /**
     * Gets the request URL
     *
     * @return the request URL
     */
    @Nullable
    URL getRequestUrl();

    /**
     * Gets the HTTP method
     *
     * @return the HTTP method
     */
    @Nullable
    HttpMethod getHttpMethod();

    /**
     * Gets the headers
     *
     * @return the headers
     */
    @Nullable
    List<HeaderOption> getHeaders();

    /**
     * Gets the options
     *
     * @return the options
     */
    @Nullable
    List<Option> getOptions();

    /**
     * Adds a header to this request
     *
     * @param header the name of the header
     * @param value  the value of the header
     */
    void addHeader(@Nonnull final String header, @Nonnull final String value);

    /**
     * Sets useCaches parameter to cache the response
     *
     * @param useCaches the value of useCaches
     */
    void setUseCaches(boolean useCaches);

    /**
     * Gets useCaches parameter
     *
     * @return the value of useCaches
     */
    boolean getUseCaches();

    /**
     * Sets the max redirects
     *
     * @param maxRedirects Max redirects that a request can take
     */
    void setMaxRedirects(int maxRedirects);

    /**
     * Gets the max redirects
     *
     * @return Max redirects that a request can take
     */
    int getMaxRedirects();

    /**
     * Sets the should redirect callback
     *
     * @param shouldRedirect Callback called before doing a redirect
     */
    void setShouldRedirect(@Nonnull final IShouldRedirect shouldRedirect);

    /**
     * Gets the should redirect callback
     *
     * @return Callback which is called before redirect
     */
    @Nullable
    IShouldRedirect getShouldRedirect();

    /**
     * Sets the should retry callback
     *
     * @param shouldretry The callback called before retry
     */
    void setShouldRetry(@Nonnull final IShouldRetry shouldretry);

    /**
     * Gets the should retry callback
     *
     * @return Callback called before retry
     */
    @Nullable
    IShouldRetry getShouldRetry();

    /**
     * Sets the max retries
     *
     * @param maxRetries Max retries for a request
     */
    void setMaxRetries(int maxRetries);

    /**
     * Gets max retries
     *
     * @return Max retries for a request
     */
    int getMaxRetries();

    /**
     * Sets the delay in seconds between retires
     *
     * @param delay Delay in seconds between retries
     */
    void setDelay(long delay);

    /**
     * Gets delay between retries
     *
     * @return Delay between retries in seconds
     */
    long getDelay();
}