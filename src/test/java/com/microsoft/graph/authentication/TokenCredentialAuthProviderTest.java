package com.microsoft.graph.authentication;

import com.azure.core.credential.TokenCredential;
import com.microsoft.graph.exceptions.AuthenticationException;
import com.microsoft.graph.httpcore.IHttpRequest;
import com.microsoft.graph.mocks.MockIHttpRequest;
import com.microsoft.graph.mocks.MockTokenCredential;
import com.microsoft.graph.options.HeaderOption;
import okhttp3.Request;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class TokenCredentialAuthProviderTest {

    public final String testToken = "CredentialTestToken";

    @Test
    public void TokenCredentialAuthProviderTestICoreAuthentication() throws AuthenticationException {

        //Arange
        Request request = new Request.Builder().url("https://localhost").build();
        TokenCredential mockCredential = MockTokenCredential.getMockTokenCredential();
        TokenCredentialAuthProvider authProvider = new TokenCredentialAuthProvider(mockCredential);

        //Act
        Assert.assertNull(request.header(AuthConstants.AUTHORIZATION_HEADER));
        Request authenticatedRequest = authProvider.authenticateRequest(request);

        //Assert
        Assert.assertEquals(request.url(), authenticatedRequest.url());
        Assert.assertNotNull(authenticatedRequest.header(AuthConstants.AUTHORIZATION_HEADER));
        assertEquals(AuthConstants.BEARER + testToken, authenticatedRequest.header(AuthConstants.AUTHORIZATION_HEADER));
    }

    @Test
    public void TokenCredentialAuthProviderTestIAuthentication() throws AuthenticationException {
        TokenCredential mockCredential = MockTokenCredential.getMockTokenCredential();
        MockIHttpRequest request = new MockIHttpRequest("https://localhost");
        TokenCredentialAuthProvider authProvider = new TokenCredentialAuthProvider(mockCredential);

        Assert.assertFalse(request.getHeaders().contains(new HeaderOption(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER + this.testToken)));
        authProvider.authenticateRequest(request);

        Assert.assertTrue(request.getHeaders().contains(new HeaderOption(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER + this.testToken)));
    }




}
