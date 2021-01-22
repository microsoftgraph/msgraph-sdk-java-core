package com.microsoft.graph.authentication;

import com.azure.core.credential.TokenCredential;
import com.azure.core.credential.TokenRequestContext;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.annotation.Nonnull;

/**
 * An implementation of the Authentication Provider with Azure-identity
 */
public class TokenCredentialAuthProvider extends BaseAuthenticationProvider {
    /** TokenCredential expected from user */
    private final TokenCredential tokenCredential;
    /** Context options which can be optionally set by the user */
    private final TokenRequestContext context;
    /** Default scope to use when no scopes are provided */
    private static final String DEFAULT_GRAPH_SCOPE = "https://graph.microsoft.com/.default";
    /**
     * Creates an Authentication provider using a passed in TokenCredential
     *
     * @param tokenCredential Credential object inheriting the TokenCredential interface used to instantiate the Auth Provider
     */
    public TokenCredentialAuthProvider(@Nonnull final TokenCredential tokenCredential) {
        this(Arrays.asList(DEFAULT_GRAPH_SCOPE), tokenCredential);
    }

    /**
     * Creates an Authentication provider using a TokenCredential and list of scopes
     *
     * @param tokenCredential Credential object inheriting the TokenCredential interface used to instantiate the Auth Provider
     * @param scopes Specified desired scopes of the Auth Provider
     */
    public TokenCredentialAuthProvider(@Nonnull final List<String> scopes, @Nonnull final TokenCredential tokenCredential) {
        if(tokenCredential == null) {
            throw new IllegalArgumentException("tokenCredential parameter cannot be null.");
        }
        if(scopes == null || scopes.isEmpty()) {
            throw new IllegalArgumentException("scopes parameter cannot be null or empty");
        }
        this.context = new TokenRequestContext();
        this.context.setScopes(scopes);
        this.tokenCredential = tokenCredential;
    }

    /**
     * Returns an AccessToken as a string
     *
     * @return String representing the retrieved AccessToken
     */
    @Nonnull
    public CompletableFuture<String> getAuthorizationTokenAsync(@Nonnull final URL requestUrl) {
        if(requestUrl == null)
            throw new IllegalArgumentException("requestUrl parameter cannot be null");
        else if(shouldAuthenticateRequestWithUrl(requestUrl))
            return this.tokenCredential
                        .getToken(this.context)
                        .toFuture()
                        .thenApply(resp -> resp.getToken());
        else
            return CompletableFuture.completedFuture((String)null);
    }
}
