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

    private TokenCredential tokenCredential;
    private TokenRequestContext context;
    private AccessToken accessToken;

    public  TokenCredentialAuthProvider(TokenCredential tokenCredential) throws AuthenticationException {
        if(tokenCredential == null) {
            throw new AuthenticationException(new Error(Codes.InvalidArgument,
                    String.format(Messages.nullParameter, "TokenCredential"))
                    ,new IllegalArgumentException());
        }

        this.tokenCredential = tokenCredential;
        this.context = new TokenRequestContext();
    }

    public TokenCredentialAuthProvider(TokenCredential tokenCredential, List<String> scopes) throws AuthenticationException {
        this(tokenCredential);
        this.context.setScopes(scopes);
    }

    @Override
    public void authenticateRequest(IHttpRequest request) {
        request.addHeader(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER + getAccessToken());
    }

    @Override
    public Request authenticateRequest(Request request) {
        return request.newBuilder()
                    .addHeader(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER + getAccessToken())
                    .build();
    }

    String getAccessToken() {
        this.tokenCredential.getToken(this.context).doOnSuccess(token -> {
            this.accessToken = token;
        }).doOnError( exception -> {
            exception.printStackTrace();
        });
        return this.accessToken.getToken();
    }
}
