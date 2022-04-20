package com.microsoft.graph.authentication;

import com.azure.core.credential.AccessToken;
import com.azure.core.credential.TokenCredential;
import com.azure.core.credential.TokenRequestContext;

import javax.annotation.Nonnull;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * An implementation of the Authentication Provider with Azure-identity
 */
public class TokenCredentialAuthProvider extends BaseAuthenticationProvider {
    /** TokenCredential expected from user */
    private final TokenCredential tokenCredential;
    /** Context options which can be optionally set by the user */
    private final TokenRequestContext context;
    /** Custom hosts which can be optionally set by the user */
    private List<String> customHosts = null;
    /** Default scope to use when no scopes are provided */
    private static final String DEFAULT_GRAPH_SCOPE = "https://graph.microsoft.com/.default";

    /**
     * Creates an Authentication provider using a passed in TokenCredential
     *
     * @param tokenCredential Credential object inheriting the TokenCredential interface used to instantiate the Auth Provider
     */
    public TokenCredentialAuthProvider(@Nonnull final TokenCredential tokenCredential) {
        this(Collections.singletonList(DEFAULT_GRAPH_SCOPE), tokenCredential);
    }

    /**
     * Creates an Authentication provider using a TokenCredential and list of scopes
     *
     * @param tokenCredential Credential object inheriting the TokenCredential interface used to instantiate the Auth Provider
     * @param scopes Specified desired scopes of the Auth Provider
     */
    public TokenCredentialAuthProvider(@Nonnull final List<String> scopes, @Nonnull final TokenCredential tokenCredential) {
        if(scopes == null || scopes.isEmpty()) {
            throw new IllegalArgumentException("scopes parameter cannot be null or empty");
        }
        this.context = new TokenRequestContext();
        this.context.setScopes(scopes);
        this.tokenCredential = Objects.requireNonNull(tokenCredential, "tokenCredential parameter cannot be null.");
    }

    /**
     * Creates an Authentication provider using a TokenCredential and a list of custom hosts
     * @param tokenCredential Credential object inheriting the TokenCredential interface used to instantiate the Auth Provider.
     * @param customHosts the user defined custom hosts.
     */
    public TokenCredentialAuthProvider(@Nonnull final TokenCredential tokenCredential, @Nonnull final List<String> customHosts){
        this(Collections.singletonList(DEFAULT_GRAPH_SCOPE), tokenCredential);
        this.customHosts = customHosts;
    }

    /**
     * Creates an Authentication provider using a TokenCredential, a list of scopes, and a list of custom hosts.
     * @param scopes Specified desired scopes of the Auth Provider
     * @param tokenCredential Credential object inheriting the TokenCredential interface used to instantiate the Auth Provider
     * @param customHosts the user defined custom hosts.
     */
    public TokenCredentialAuthProvider(@Nonnull final List<String> scopes, @Nonnull final TokenCredential tokenCredential, @Nonnull final List<String> customHosts) {
        this(scopes, tokenCredential);
        this.customHosts = customHosts;
    }

    /**
     * Returns an AccessToken as a string
     *
     * @return String representing the retrieved AccessToken
     */
    @Nonnull
    public CompletableFuture<String> getAuthorizationTokenAsync(@Nonnull final URL requestUrl) {
        if(shouldAuthenticateRequestWithUrl(Objects.requireNonNull(requestUrl, "requestUrl parameter cannot be null"), customHosts))
            return this.tokenCredential
                        .getToken(this.context)
                        .toFuture()
                        .thenApply(AccessToken::getToken);
        else
            return CompletableFuture.completedFuture((String)null);
    }
}
