package com.microsoft.graph.mocks;

import java.net.URI;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.microsoft.graph.httpcore.HttpMethod;
import com.microsoft.graph.httpcore.IHttpRequest;
import com.microsoft.graph.httpcore.middlewareoption.IShouldRedirect;
import com.microsoft.graph.httpcore.middlewareoption.IShouldRetry;
import com.microsoft.graph.options.HeaderOption;
import com.microsoft.graph.options.Option;


/**
 * Dummy class implementing IHttpRequest used for testing
 */
public class MockIHttpRequest implements IHttpRequest {

    private final List<HeaderOption> headersOptions;
    private String requestUrl;

    public MockIHttpRequest(String requestUrl)
    {
        headersOptions = new ArrayList<>();
        this.requestUrl = requestUrl;
    }

    @Override
    public URL getRequestUrl() {
        URI baseUrl = URI.create(requestUrl);
        try {
            return new URL(baseUrl.toString());
        } catch (final MalformedURLException e) {
        }
        return null;
    }

    @Override
    public HttpMethod getHttpMethod() {
        return null;
    }

    @Override
    public List<HeaderOption> getHeaders() {
        return headersOptions;
    }

    @Override
    public List<Option> getOptions() {
        return null;
    }

    @Override
    public void addHeader(final String header, final String value) {
        headersOptions.add(new HeaderOption(header, value));
    }


    @Override
    public void setUseCaches(boolean useCaches) {

    }

    @Override
    public boolean getUseCaches() {
        return false;
    }

    @Override
    public void setMaxRedirects(int maxRedirects) {

    }

    @Override
    public int getMaxRedirects() {
        return 0;
    }

    @Override
    public void setShouldRedirect(IShouldRedirect shouldRedirect) {

    }

    @Override
    public IShouldRedirect getShouldRedirect() {
        return null;
    }

    @Override
    public void setShouldRetry(IShouldRetry shouldretry) {

    }

    @Override
    public IShouldRetry getShouldRetry() {
        return null;
    }

    @Override
    public void setMaxRetries(int maxRetries) {

    }

    @Override
    public int getMaxRetries() {
        return 0;
    }

    @Override
    public void setDelay(long delay) {

    }

    @Override
    public long getDelay() {
        return 0;
    }
}
