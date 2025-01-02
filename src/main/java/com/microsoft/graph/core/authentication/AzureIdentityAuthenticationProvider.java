package com.microsoft.graph.core.authentication;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import com.azure.core.credential.TokenCredential;
import com.microsoft.kiota.authentication.BaseBearerTokenAuthenticationProvider;
import com.microsoft.kiota.authentication.ObservabilityOptions;
/** Implementation of Authentication provider for Azure Identity with Microsoft Graph defaults */
public class AzureIdentityAuthenticationProvider extends BaseBearerTokenAuthenticationProvider {
    /**
     * Creates a new instance of AzureIdentityAuthenticationProvider.
     * @param tokenCredential The Azure.Identity.TokenCredential implementation to use.
     * @param allowedHosts The list of allowed hosts for which to request access tokens.
     * @param scopes The scopes to request access tokens for.
     */
    @SuppressWarnings("LambdaLast")
    public AzureIdentityAuthenticationProvider(@Nonnull final TokenCredential tokenCredential, @Nonnull final String[] allowedHosts, @Nonnull final String... scopes) {
        this(tokenCredential, allowedHosts, null, scopes);
    }
    /**
     * Creates a new instance of AzureIdentityAuthenticationProvider.
     * @param tokenCredential The Azure.Identity.TokenCredential implementation to use.
     * @param allowedHosts The list of allowed hosts for which to request access tokens.
     * @param observabilityOptions The observability options to use.
     * @param scopes The scopes to request access tokens for.
     */
    @SuppressWarnings("LambdaLast")
    public AzureIdentityAuthenticationProvider(@Nonnull final TokenCredential tokenCredential, @Nonnull final String[] allowedHosts, @Nullable final ObservabilityOptions observabilityOptions, @Nonnull final String... scopes) {
        this(tokenCredential, allowedHosts, observabilityOptions, true, scopes);
    }

    /**
     * Creates a new instance of AzureIdentityAuthenticationProvider.
     * @param tokenCredential The Azure.Identity.TokenCredential implementation to use.
     * @param allowedHosts The list of allowed hosts for which to request access tokens.
     * @param observabilityOptions The observability options to use.
     * @param isCaeEnabled Whether to enable Continuous Access Evaluation, defaults to true.
     * @param scopes The scopes to request access tokens for.
     */
    @SuppressWarnings("LambdaLast")
    public AzureIdentityAuthenticationProvider(@Nonnull final TokenCredential tokenCredential, @Nonnull final String[] allowedHosts, @Nullable final ObservabilityOptions observabilityOptions, final boolean isCaeEnabled, @Nonnull final String... scopes) {
        super(new AzureIdentityAccessTokenProvider(tokenCredential, allowedHosts, observabilityOptions, isCaeEnabled, scopes));
    }
}
