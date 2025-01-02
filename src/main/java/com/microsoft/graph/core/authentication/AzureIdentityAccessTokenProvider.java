package com.microsoft.graph.core.authentication;

import java.util.HashSet;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import com.azure.core.credential.TokenCredential;
import com.microsoft.kiota.authentication.ObservabilityOptions;

/** AzureIdentityAccessTokenProvider wrapper from Kiota library with Microsoft Graph defaults. */
public class AzureIdentityAccessTokenProvider extends com.microsoft.kiota.authentication.AzureIdentityAccessTokenProvider {
    /**
     * Creates a new instance of AzureIdentityAccessTokenProvider.
     * @param tokenCredential The Azure.Identity.TokenCredential implementation to use.
     */
    public AzureIdentityAccessTokenProvider(@Nonnull TokenCredential tokenCredential) {
        this(tokenCredential, new String[] {}, null);
    }
    /**
     * Creates a new instance of AzureIdentityAccessTokenProvider.
     * @param tokenCredential The Azure.Identity.TokenCredential implementation to use.
     * @param allowedHosts The list of allowed hosts for which to request access tokens.
     * @param scopes The scopes to request access tokens for.
     * @param observabilityOptions The observability options to use.
     */
    @SuppressWarnings("LambdaLast")
    public AzureIdentityAccessTokenProvider(@Nonnull final TokenCredential tokenCredential, @Nonnull final String[] allowedHosts,
            @Nullable final ObservabilityOptions observabilityOptions, @Nonnull final String... scopes) {
        this(tokenCredential, allowedHosts, observabilityOptions, true, scopes);
    }

    /**
     * Creates a new instance of AzureIdentityAccessTokenProvider.
     * @param tokenCredential The Azure.Identity.TokenCredential implementation to use.
     * @param allowedHosts The list of allowed hosts for which to request access tokens.
     * @param scopes The scopes to request access tokens for.
     * @param observabilityOptions The observability options to use.
     * @param isCaeEnabled Whether to enable Continuous Access Evaluation, defaults to true.
     */
    @SuppressWarnings("LambdaLast")
    public AzureIdentityAccessTokenProvider(@Nonnull final TokenCredential tokenCredential, @Nonnull final String[] allowedHosts,
            @Nullable final ObservabilityOptions observabilityOptions, final boolean isCaeEnabled, @Nonnull final String... scopes) {
        super(tokenCredential, allowedHosts, observabilityOptions, isCaeEnabled, scopes);
        if (allowedHosts == null || allowedHosts.length == 0) {
            final HashSet<String> allowedHostsSet = new HashSet<String>();
            allowedHostsSet.add("graph.microsoft.com");
            allowedHostsSet.add("graph.microsoft.us");
            allowedHostsSet.add("dod-graph.microsoft.us");
            allowedHostsSet.add("graph.microsoft.de");
            allowedHostsSet.add("microsoftgraph.chinacloudapi.cn");
            allowedHostsSet.add("canary.graph.microsoft.com");
            this.getAllowedHostsValidator().setAllowedHosts(allowedHostsSet);
        }
    }

}
