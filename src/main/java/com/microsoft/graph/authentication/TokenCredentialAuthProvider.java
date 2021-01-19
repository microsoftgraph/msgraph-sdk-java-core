package com.microsoft.graph.authentication;

import com.azure.core.credential.AccessToken;
import com.azure.core.credential.TokenCredential;
import com.azure.core.credential.TokenRequestContext;
import com.microsoft.graph.exceptions.AuthenticationException;
import com.microsoft.graph.exceptions.Error;
import com.microsoft.graph.exceptions.ErrorConstants;
import com.microsoft.graph.httpcore.IHttpRequest;
import okhttp3.Request;
import com.microsoft.graph.exceptions.ErrorConstants.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nonnull;

public class TokenCredentialAuthProvider implements IAuthenticationProvider<Request> {

    //TokenCredential expected from user
    private TokenCredential tokenCredential;
    //Context options which can be optionally set by the user
    private TokenRequestContext context;
    // maximum delay to wait for token obtention
    private Duration tokenBlockTimeout;


    //TODO: Upon further review from Peter, this should include a Null check in the case that this is the first request
    //There should be just one constructor as scopes should probably not be optional
    /**
     * Creates an Authentication provider using a passed in TokenCredential
     *
     * @param tokenCredential Credential object inheriting the TokenCredential interface used to instantiate the Auth Provider
     * @throws AuthenticationException exception occurs if the TokenCredential parameter is null
     */
    public TokenCredentialAuthProvider(@Nonnull final TokenCredential tokenCredential) throws AuthenticationException {
        if(tokenCredential == null) {
            throw new AuthenticationException(new Error(Codes.InvalidArgument,
                    String.format(Messages.NullParameter, "TokenCredential"))
                    ,new IllegalArgumentException());
        }

        this.tokenCredential = tokenCredential;
        this.context = new TokenRequestContext();
        this.tokenBlockTimeout = Duration.ofMinutes(10);
    }

    /**
     * Creates an Authentication provider using a TokenCredential and list of scopes
     *
     * @param tokenCredential Credential object inheriting the TokenCredential interface used to instantiate the Auth Provider
     * @param scopes Specified desired scopes of the Auth Provider
     * @throws AuthenticationException exception occurs if the TokenCredential parameter is null
     */
    public TokenCredentialAuthProvider(@Nonnull final List<String> scopes, @Nonnull final TokenCredential tokenCredential) throws AuthenticationException {
        this(tokenCredential);
        this.context.setScopes(scopes);
    }

    /**
     * Creates an Authentication provider using a TokenCredential and list of scopes
     *
     * @param tokenCredential Credential object inheriting the TokenCredential interface used to instantiate the Auth Provider
     * @param scopes Specified desired scopes of the Auth Provider
     * @param tokenObtentionTimeout Maximum time to wait for token obtention. Default 10 minutes. Use lower value on application with stable connectivity and no user interactions.
     * @throws AuthenticationException exception occurs if the TokenCredential parameter is null
     */
    public TokenCredentialAuthProvider(@Nonnull final List<String> scopes, @Nonnull final Duration tokenObtentionTimeout, @Nonnull final TokenCredential tokenCredential) throws AuthenticationException {
        this(tokenCredential);
        this.context.setScopes(scopes);
        this.tokenBlockTimeout = tokenObtentionTimeout;
    }

    /**
     * Authenticates the request
     *
     * @param request the request to authenticate
     */
    @Override
    public void authenticateRequest(@Nonnull final IHttpRequest request) throws AuthenticationException {
        if(ShouldAuthenticateRequest(request.getRequestUrl().toString())) {
            request.addHeader(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER + getAccessToken());
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
        if(ShouldAuthenticateRequest(request.url().toString())) {
            return request.newBuilder()
                    .addHeader(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER + getAccessToken())
                    .build();
        } else {
            return request;
        }
    }

    private static final HashSet<String> validGraphHostNames = new HashSet<>(Arrays.asList("graph.microsoft.com", "graph.microsoft.us", "dod-graph.microsoft.us", "graph.microsoft.de", "microsoftgraph.chinacloudapi.cn"));
    private boolean ShouldAuthenticateRequest(@Nonnull final String requestUrl) {
        try {
            final URL url = new URL(requestUrl);
            final String hostName = url.getHost().toLowerCase(Locale.getDefault());
            return validGraphHostNames.contains(hostName);
        } catch (MalformedURLException ex) {
            return false;
        }
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
            throw new AuthenticationException(new Error(ErrorConstants.Codes.GeneralException, ErrorConstants.Messages.AuthTimeOut), e);
        }
    }
}
