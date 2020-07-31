package com.microsoft.graph.authentication;

import com.azure.core.credential.AccessToken;
import com.azure.core.credential.TokenCredential;
import com.azure.core.credential.TokenRequestContext;
import com.microsoft.graph.exceptions.AuthenticationException;
import com.microsoft.graph.exceptions.Error;
import com.microsoft.graph.httpcore.IHttpRequest;
import okhttp3.Request;
import com.microsoft.graph.exceptions.ErrorConstants.*;

import java.util.List;

public class TokenCredentialAuthProvider implements ICoreAuthenticationProvider , IAuthenticationProvider {

    //TokenCredential expected form user
    private TokenCredential tokenCredential;
    //Context options which can be optionally set by the user
    private TokenRequestContext context;
    //Access token to be retrieved
    private AccessToken accessToken;

    /**
     * Creates an Authentication provider using a passed in TokenCredential
     *
     * @param tokenCredential Credential object inheriting the TokenCredential interface used to instantiate the Auth Provider
     * @throws AuthenticationException exception occurs if the TokenCredential parameter is null
     */
    public  TokenCredentialAuthProvider(TokenCredential tokenCredential) throws AuthenticationException {
        if(tokenCredential == null) {
            throw new AuthenticationException(new Error(Codes.InvalidArgument,
                    String.format(Messages.NullParameter, "TokenCredential"))
                    ,new IllegalArgumentException());
        }

        this.tokenCredential = tokenCredential;
        this.context = new TokenRequestContext();
    }

    /**
     *Created an Authentication provider using a TokenCredential and list of scopes
     *
     * @param tokenCredential Credential object inheriting the TokenCredential interface used to instantiate the Auth Provider
     * @param scopes Specified desired scopes of the Auth Provider
     * @throws AuthenticationException exception occurs if the TokenCredential parameter is null
     */
    public TokenCredentialAuthProvider(TokenCredential tokenCredential, List<String> scopes) throws AuthenticationException {
        this(tokenCredential);
        this.context.setScopes(scopes);
    }

    /**
     * Authenticates the request
     *
     * @param request the request to authenticate
     */
    @Override
    public void authenticateRequest(IHttpRequest request) {
        request.addHeader(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER + getAccessToken());
    }

    /**
     * Authenticates the request
     *
     * @param request the request to authenticate
     * @return Request with Authorization header added to it
     */
    @Override
    public Request authenticateRequest(Request request) {
        return request.newBuilder()
                    .addHeader(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER + getAccessToken())
                    .build();
    }

    /**
     * Returns an AccessToken as a string
     *
     * @return String representing the retrieved AccessToken
     */
    String getAccessToken() {
        this.tokenCredential.getToken(this.context).doOnError(exception -> exception.printStackTrace())
                .subscribe(token -> {
            this.accessToken = token;
        });
        return this.accessToken.getToken();
    }
}
