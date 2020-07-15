package com.microsoft.graph.authentication;

import com.azure.core.credential.TokenCredential;
import com.azure.core.credential.TokenRequestContext;
import com.azure.core.http.HttpRequest;
import com.microsoft.graph.httpcore.IHttpRequest;
import com.microsoft.graph.httpcore.ICoreAuthenticationProvider;
import okhttp3.Request;

import java.util.List;

public class TokenCredntialAuthProvider implements ICoreAuthenticationProvider , IAuthenticationProvider {

    private TokenCredential tokenCredential;
    private HttpRequest request;
    private TokenRequestContext context;

    public TokenCredntialAuthProvider(TokenCredential tokenCredential, List<String> scopes) {
        this.tokenCredential = tokenCredential;
        context = new TokenRequestContext().setScopes(scopes);
    }

    @Override
    public void authenticateRequest(IHttpRequest request) {
        String accessToken = tokenCredential.getToken(context).toString();
        request.addHeader(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER + accessToken);
    }

    @Override
    public Request authenticateRequest(Request request) {
        String accessToken = "";//getAccessToken();
        return request.newBuilder()
                .addHeader(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER + accessToken)
                .build();
    }

//    void getAccessToken(String credential) {
//        String accessToken = tokenCredential.getToken(context).toString();
//
//    }
}
