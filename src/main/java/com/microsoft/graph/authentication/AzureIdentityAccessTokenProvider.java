package com.microsoft.graph.authentication;

import java.util.HashSet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.azure.core.credential.TokenCredential;
import com.microsoft.kiota.authentication.ObservabilityOptions;

/** AzureIdentityAccessTokenProvider wrapper from Kiota library with Microsoft Graph defaults. */
public class AzureIdentityAccessTokenProvider extends com.microsoft.kiota.authentication.AzureIdentityAccessTokenProvider {
    /**
     * Creates a new instance of AzureIdentityAccessTokenProvider.
     * @param tokenCredential The Azure.Identity.TokenCredential implementation to use.
     */
    public AzureIdentityAccessTokenProvider(@Nonnull TokenCredential tokenCredential) {
        this(tokenCredential, new String[] {}, null, new String[] {});
    }
    /** {@inheritDoc} */
    public AzureIdentityAccessTokenProvider(@Nonnull final TokenCredential tokenCredential, @Nonnull final String[] allowedHosts,
            @Nullable final ObservabilityOptions observabilityOptions, @Nonnull final String... scopes) {
        super(tokenCredential, allowedHosts, observabilityOptions, scopes);
        if (allowedHosts.length == 0) {
            final HashSet<String> allowedHostsSet = new HashSet<String>() {{
                this.add("graph.microsoft.com");
                this.add("graph.microsoft.us");
                this.add("dod-graph.microsoft.us");
                this.add("graph.microsoft.de");
                this.add("microsoftgraph.chinacloudapi.cn");
                this.add("canary.graph.microsoft.com");
            }};
            this.getAllowedHostsValidator().setAllowedHosts(allowedHostsSet);
        }
    }

}
