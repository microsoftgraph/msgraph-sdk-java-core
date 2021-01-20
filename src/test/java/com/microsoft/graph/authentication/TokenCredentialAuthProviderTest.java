package com.microsoft.graph.authentication;

import com.azure.core.credential.TokenCredential;
import com.microsoft.graph.exceptions.AuthenticationException;
import com.microsoft.graph.http.IHttpRequest;
import com.microsoft.graph.mocks.MockTokenCredential;
import com.microsoft.graph.options.HeaderOption;

import okhttp3.Request;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class TokenCredentialAuthProviderTest {

    public final String testToken = "CredentialTestToken";

    @Test
    public void TokenCredentialAuthProviderTestICoreAuthentication() throws AuthenticationException {

        // Arrange
        final Request request = new Request.Builder().url("https://graph.microsoft.com").build();
        final TokenCredential mockCredential = MockTokenCredential.getMockTokenCredential();
        final TokenCredentialAuthProvider authProvider = new TokenCredentialAuthProvider(mockCredential);

        // Act
        Assert.assertNull(request.header(InternalAuthConstants.AUTHORIZATION_HEADER));
        final Request authenticatedRequest = authProvider.authenticateRequest(request);

        // Assert
        Assert.assertEquals(request.url(), authenticatedRequest.url());
        Assert.assertNotNull(authenticatedRequest.header(InternalAuthConstants.AUTHORIZATION_HEADER));
        assertEquals(InternalAuthConstants.BEARER + testToken, authenticatedRequest.header(InternalAuthConstants.AUTHORIZATION_HEADER));
    }

    @Test
    public void TokenCredentialAuthProviderTestIAuthentication() throws AuthenticationException, MalformedURLException {

        // Arrange
        final TokenCredential mockCredential = MockTokenCredential.getMockTokenCredential();
        final IHttpRequest request = mock(IHttpRequest.class);
        final List<HeaderOption> headersOptions = new ArrayList<>();
        when(request.getRequestUrl()).thenReturn(new URL("https://graph.microsoft.com"));
        doAnswer(new Answer<Void>() {

            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                headersOptions.add(new HeaderOption((String)invocation.getArguments()[0], (String)invocation.getArguments()[1]));
                return null;
            }

        }).when(request).addHeader(any(String.class), any(String.class));
        when(request.getHeaders()).thenReturn(headersOptions);
        final TokenCredentialAuthProvider authProvider = new TokenCredentialAuthProvider(mockCredential);

        //Act
        Assert.assertTrue(request.getHeaders().isEmpty());
        authProvider.authenticateRequest(request);
        Assert.assertFalse(request.getHeaders().isEmpty());

        //Assert
        Assert.assertTrue(request.getHeaders().get(0).getName().equals(InternalAuthConstants.AUTHORIZATION_HEADER));
        Assert.assertTrue(request.getHeaders().get(0).getValue().equals(InternalAuthConstants.BEARER + this.testToken));
    }
    @Test
    public void TokenCredentialAuthProviderDoesNotAddTokenOnInvalidDomainsTestICoreAuthentication() throws AuthenticationException {

        //Arrange
        final Request request = new Request.Builder().url("https://localhost").build();
        final TokenCredential mockCredential = MockTokenCredential.getMockTokenCredential();
        final TokenCredentialAuthProvider authProvider = new TokenCredentialAuthProvider(mockCredential);

        //Act
        Assert.assertNull(request.header(InternalAuthConstants.AUTHORIZATION_HEADER));
        final Request authenticatedRequest = authProvider.authenticateRequest(request);

        //Assert
        Assert.assertEquals(request.url(), authenticatedRequest.url());
        Assert.assertNull(authenticatedRequest.header(InternalAuthConstants.AUTHORIZATION_HEADER));
    }

    @Test
    public void TokenCredentialAuthProviderDoesNotAddTokenOnInvalidDomainsTestIAuthentication() throws AuthenticationException,
            MalformedURLException {

        //Arrange
        final TokenCredential mockCredential = MockTokenCredential.getMockTokenCredential();
        final IHttpRequest request = mock(IHttpRequest.class);
        when(request.getRequestUrl()).thenReturn(new URL("https://localhost"));
        final TokenCredentialAuthProvider authProvider = new TokenCredentialAuthProvider(mockCredential);

        //Act
        Assert.assertTrue(request.getHeaders().isEmpty());
        authProvider.authenticateRequest(request);

        //Assert
        Assert.assertTrue(request.getHeaders().isEmpty());
    }
}
