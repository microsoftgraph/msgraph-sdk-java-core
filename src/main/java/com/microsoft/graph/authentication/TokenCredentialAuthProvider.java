package com.microsoft.graph.authentication;

import com.azure.core.credential.AccessToken;
import com.azure.core.credential.TokenCredential;
import com.azure.core.credential.TokenRequestContext;
import com.microsoft.graph.exceptions.AuthenticationException;
import com.microsoft.graph.http.IHttpRequest;

import okhttp3.HttpUrl;
import okhttp3.Request;

import java.net.URL;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nonnull;

/**
 * An implementation of the Authentication Provider with Azure-identity
 */
public class TokenCredentialAuthProvider implements IAuthenticationProvider<Request> {

    /** TokenCredential expected from user */
    private final TokenCredential tokenCredential;
    /** Context options which can be optionally set by the user */
    private final TokenRequestContext context;
    /** maximum delay to wait for token obtention */
    private final Duration tokenBlockTimeout;
    /** Default scope to use when no scopes are provided */
    private static final String DEFAULT_GRAPH_SCOPE = "https://graph.microsoft.com/.default";
    /** Default timeout for token obtention, set to 10 minutes in case of interactive flows to give time to the end user */
    private static final long DEFAULT_TOKEN_TIMEOUT = 10;
    /**
     * Creates an Authentication provider using a passed in TokenCredential
     *
     * @param tokenCredential Credential object inheriting the TokenCredential interface used to instantiate the Auth Provider
     */
    public TokenCredentialAuthProvider(@Nonnull final TokenCredential tokenCredential) {
        this(Arrays.asList(new String[] {DEFAULT_GRAPH_SCOPE}), tokenCredential);
    }

    /**
     * Creates an Authentication provider using a TokenCredential and list of scopes with default timeout (10 minutes)
     *
     * @param tokenCredential Credential object inheriting the TokenCredential interface used to instantiate the Auth Provider
     * @param scopes Specified desired scopes of the Auth Provider
     */
    public TokenCredentialAuthProvider(@Nonnull final List<String> scopes, @Nonnull final TokenCredential tokenCredential) {
        this(scopes, Duration.ofMinutes(DEFAULT_TOKEN_TIMEOUT), tokenCredential);
    }

    /**
     * Creates an Authentication provider using a TokenCredential and list of scopes
     *
     * @param tokenCredential Credential object inheriting the TokenCredential interface used to instantiate the Auth Provider
     * @param scopes Specified desired scopes of the Auth Provider
     * @param tokenObtentionTimeout Maximum time to wait for token obtention. Default 10 minutes. Use lower value on application with stable connectivity and no user interactions.
     */
    public TokenCredentialAuthProvider(@Nonnull final List<String> scopes, @Nonnull final Duration tokenObtentionTimeout, @Nonnull final TokenCredential tokenCredential) {
        if(tokenCredential == null) {
            throw new IllegalArgumentException("tokenCredential parameter cannot be null.");
        }
        if(tokenObtentionTimeout == null) {
            throw new IllegalArgumentException("tokenObtentionTimout parameter cannot be null");
        }
        if(scopes == null || scopes.isEmpty()) {
            throw new IllegalArgumentException("scopes parameter cannot be null or empty");
        }
        this.context = new TokenRequestContext();
        this.context.setScopes(scopes);
        this.tokenCredential = tokenCredential;
        this.tokenBlockTimeout = tokenObtentionTimeout;
    }

    /**
     * Authenticates the request
     *
     * @param request the request to authenticate
     */
    @Override
    public void authenticateRequest(@Nonnull final IHttpRequest request) throws AuthenticationException {
        if(request == null) {
            throw new IllegalArgumentException("request parameter cannot be null.");
        }
        final URL requestUrl = request.getRequestUrl();
        if(requestUrl != null && ShouldAuthenticateRequest(requestUrl)) {
            request.addHeader(InternalAuthConstants.AUTHORIZATION_HEADER, InternalAuthConstants.BEARER + getAccessToken());
        }
    }

    /**
     * Authenticates the request
     *
     * @param request the request to authenticate
     * @return Request with Authorization header added to it
     */
    @Override
    @Nonnull
    public Request authenticateRequest(@Nonnull final Request request) throws AuthenticationException {
        if(request == null) {
            throw new IllegalArgumentException("request parameter cannot be null.");
        }
        final HttpUrl requestUrl = request.url();
        if(requestUrl != null && ShouldAuthenticateRequest(requestUrl.url())) {
            return request.newBuilder()
                    .addHeader(InternalAuthConstants.AUTHORIZATION_HEADER, InternalAuthConstants.BEARER + getAccessToken())
                    .build();
        } else {
            return request;
        }
    }

    private static final HashSet<String> validGraphHostNames = new HashSet<>(Arrays.asList("graph.microsoft.com", "graph.microsoft.us", "dod-graph.microsoft.us", "graph.microsoft.de", "microsoftgraph.chinacloudapi.cn"));
    private boolean ShouldAuthenticateRequest(@Nonnull final URL requestUrl) {
        if(requestUrl == null || !requestUrl.getProtocol().toLowerCase(Locale.ROOT).equals("https"))
            return false;
        final String hostName = requestUrl.getHost().toLowerCase(Locale.getDefault());
        return validGraphHostNames.contains(hostName);
    }

    /**
     * Returns an AccessToken as a string
     *
     * @return String representing the retrieved AccessToken
     */
    private String getAccessToken() throws AuthenticationException {
        try {
            final AccessToken token = this.tokenCredential.getToken(this.context).block(this.tokenBlockTimeout);
            return token.getToken();
        } catch (RuntimeException e) {
            throw new AuthenticationException("Authentication failed or timed out.", e);
        }
    }
}
