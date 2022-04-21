package com.microsoft.graph.authentication;

import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;

import javax.annotation.Nonnull;

/**
 * Provides basic common methods for all authentication providers
 */
public abstract class BaseAuthenticationProvider implements IAuthenticationProvider {
    private static final HashSet<String> validGraphHostNames = new HashSet<>(Arrays.asList("graph.microsoft.com", "graph.microsoft.us", "dod-graph.microsoft.us", "graph.microsoft.de", "microsoftgraph.chinacloudapi.cn", "canary.graph.microsoft.com"));
    private HashSet<String> customHosts;

    /**
     * Allow the user to add custom hosts by passing in Array
     * @param customHosts custom hosts passed in by user.
     */
    public void setCustomHosts(String[] customHosts) {
        if(this.customHosts == null){
            this.customHosts = new HashSet<String>();
        }
        for(String host: customHosts){
            this.customHosts.add(host.toLowerCase(Locale.ROOT));
        }
    }
    /**
     * Get the custom hosts set by user.
     * @return the custom hosts set by user.
     */
    public HashSet<String> getCustomHosts(){
        return (HashSet<String>) this.customHosts.clone();
    }
    /**
     * Determines whether a request should be authenticated or not based on it's url.
     * If you're implementing a custom provider, call that method first before getting the token
     * @param requestUrl request URL that is about to be executed
     * @return whether a token should be attached to this request
     */
    protected boolean shouldAuthenticateRequestWithUrl(@Nonnull final URL requestUrl) {
        if (requestUrl == null || !requestUrl.getProtocol().toLowerCase(Locale.ROOT).equals("https"))
            return false;
        final String hostName = requestUrl.getHost().toLowerCase(Locale.ROOT);
        return customHosts == null ? (validGraphHostNames.contains(hostName)) : (customHosts.contains(hostName));
    }
}
