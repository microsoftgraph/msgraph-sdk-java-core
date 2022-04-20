package com.microsoft.graph.authentication;

import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Provides basic common methods for all authentication providers
 */
public abstract class BaseAuthenticationProvider implements IAuthenticationProvider {
    private static final HashSet<String> validGraphHostNames = new HashSet<>(Arrays.asList("graph.microsoft.com", "graph.microsoft.us", "dod-graph.microsoft.us", "graph.microsoft.de", "microsoftgraph.chinacloudapi.cn", "canary.graph.microsoft.com"));
    /**
     * Determines whether a request should be authenticated or not based on it's url.
     * If you're implementing a custom provider, call that method first before getting the token
     * @param requestUrl request URL that is about to be executed
     * @return whether a token should be attached to this request
     */
    protected boolean shouldAuthenticateRequestWithUrl(@Nonnull final URL requestUrl, @Nullable final List<String> customHosts) {
        if (requestUrl == null || !requestUrl.getProtocol().toLowerCase(Locale.ROOT).equals("https"))
            return false;
        final String hostName = requestUrl.getHost().toLowerCase(Locale.ROOT);
        return customHosts == null ? (validGraphHostNames.contains(hostName)) : (customHosts.contains(hostName));
    }
}
