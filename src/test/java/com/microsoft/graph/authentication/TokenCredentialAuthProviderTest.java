package com.microsoft.graph.authentication;

import com.azure.core.credential.TokenCredential;
import com.microsoft.graph.exceptions.AuthenticationException;
import com.microsoft.graph.mocks.MockTokenCredential;
import okhttp3.Request;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class TokenCredentialAuthProviderTest {

    public final String testToken = "CredentialTestToken";

    @Test
    public void TokenCredentialAuthProviderTestICoreAuthentication() throws AuthenticationException {

        Request request = new Request.Builder().url("https://graph.microsoft.com/v1.0/me/").build();
        TokenCredential mockCredential = new MockTokenCredential().getMockTokenCredential();

        TokenCredentialAuthProvider authProvider = new TokenCredentialAuthProvider(mockCredential);
        Request newRequest = authProvider.authenticateRequest(request);

        assertEquals(newRequest.toString(), request.newBuilder()
                .addHeader(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER + testToken)
                .build().toString());
        assertEquals(testToken, authProvider.getAccessToken());

    }

    @Test
    public void TokenCredentialAuthProviderTestIAuthentication() {

    }




}
