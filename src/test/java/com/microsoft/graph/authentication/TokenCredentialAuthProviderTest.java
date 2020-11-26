package com.microsoft.graph.authentication;

import com.azure.core.credential.TokenCredential;
import com.microsoft.graph.exceptions.AuthenticationException;
import com.microsoft.graph.mocks.MockIHttpRequest;
import com.microsoft.graph.mocks.MockTokenCredential;
import okhttp3.Request;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class TokenCredentialAuthProviderTest {

    public final String testToken = "CredentialTestToken";

    @Test
    public void TokenCredentialAuthProviderTestICoreAuthentication() throws AuthenticationException {

        //Arrange
        final Request request = new Request.Builder().url("https://graph.microsoft.com").build();
        final TokenCredential mockCredential = MockTokenCredential.getMockTokenCredential();
        final TokenCredentialAuthProvider authProvider = new TokenCredentialAuthProvider(mockCredential);

        //Act
        Assert.assertNull(request.header(AuthConstants.AUTHORIZATION_HEADER));
        final Request authenticatedRequest = authProvider.authenticateRequest(request);

        //Assert
        Assert.assertEquals(request.url(), authenticatedRequest.url());
        Assert.assertNotNull(authenticatedRequest.header(AuthConstants.AUTHORIZATION_HEADER));
        assertEquals(AuthConstants.BEARER + testToken, authenticatedRequest.header(AuthConstants.AUTHORIZATION_HEADER));
    }

    @Test
    public void TokenCredentialAuthProviderTestIAuthentication() throws AuthenticationException {

        //Arrange
        final TokenCredential mockCredential = MockTokenCredential.getMockTokenCredential();
        final MockIHttpRequest request = new MockIHttpRequest("https://graph.microsoft.com");
        final TokenCredentialAuthProvider authProvider = new TokenCredentialAuthProvider(mockCredential);

        //Act
        Assert.assertTrue(request.getHeaders().isEmpty());
        authProvider.authenticateRequest(request);

        //Assert
        Assert.assertTrue(request.getHeaders().get(0).getName().equals(AuthConstants.AUTHORIZATION_HEADER));
        Assert.assertTrue(request.getHeaders().get(0).getValue().equals(AuthConstants.BEARER + this.testToken));
    }
    @Test
    public void TokenCredentialAuthProviderDoesNotAddTokenOnInvalidDomainsTestICoreAuthentication() throws AuthenticationException {

        //Arrange
        final Request request = new Request.Builder().url("https://localhost").build();
        final TokenCredential mockCredential = MockTokenCredential.getMockTokenCredential();
        final TokenCredentialAuthProvider authProvider = new TokenCredentialAuthProvider(mockCredential);

        //Act
        Assert.assertNull(request.header(AuthConstants.AUTHORIZATION_HEADER));
        final Request authenticatedRequest = authProvider.authenticateRequest(request);

        //Assert
        Assert.assertEquals(request.url(), authenticatedRequest.url());
        Assert.assertNull(authenticatedRequest.header(AuthConstants.AUTHORIZATION_HEADER));
    }

    @Test
    public void TokenCredentialAuthProviderDoesNotAddTokenOnInvalidDomainsTestIAuthentication() throws AuthenticationException {

        //Arrange
        final TokenCredential mockCredential = MockTokenCredential.getMockTokenCredential();
        final MockIHttpRequest request = new MockIHttpRequest("https://localhost");
        final TokenCredentialAuthProvider authProvider = new TokenCredentialAuthProvider(mockCredential);

        //Act
        Assert.assertTrue(request.getHeaders().isEmpty());
        authProvider.authenticateRequest(request);

        //Assert
        Assert.assertTrue(request.getHeaders().isEmpty());
    }
}
