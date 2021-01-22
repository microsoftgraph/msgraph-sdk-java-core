package com.microsoft.graph.authentication;

import com.azure.core.credential.TokenCredential;
import com.microsoft.graph.mocks.MockTokenCredential;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class TokenCredentialAuthProviderTest {

    public final String testToken = "CredentialTestToken";

    @Test
    public void providerAddsTokenOnValidHostName() throws MalformedURLException, InterruptedException,
            ExecutionException {
        // Arrange
        final URL url = new URL("https://graph.microsoft.com");
        final TokenCredential mockCredential = MockTokenCredential.getMockTokenCredential();
        final TokenCredentialAuthProvider authProvider = new TokenCredentialAuthProvider(mockCredential);

        // Act
        final String token = authProvider.getAuthorizationTokenAsync(url).get();

        // Assert
        assertEquals(token, testToken);
    }
    @Test
    public void providerDoesntAddTokenOnInvalidHostName() throws MalformedURLException, InterruptedException,
            ExecutionException {
        // Arrange
        final URL url = new URL("https://localhost");
        final TokenCredential mockCredential = MockTokenCredential.getMockTokenCredential();
        final TokenCredentialAuthProvider authProvider = new TokenCredentialAuthProvider(mockCredential);

        // Act
        final String token = authProvider.getAuthorizationTokenAsync(url).get();

        // Assert
        assertNull(token);
    }
    @Test
    public void providerDefensiveProgramming() {
        assertThrows("should throw on null credentials", IllegalArgumentException.class, () -> { new TokenCredentialAuthProvider(null); });
        assertThrows("should throw on null scopes", IllegalArgumentException.class, () -> { new TokenCredentialAuthProvider(null, mock(TokenCredential.class)); });
        assertThrows("should throw on empty scopes", IllegalArgumentException.class, () -> { new TokenCredentialAuthProvider(new ArrayList<String>(), mock(TokenCredential.class)); });
        final TokenCredentialAuthProvider provider = new TokenCredentialAuthProvider(mock(TokenCredential.class));
        assertThrows("should throw on null url", IllegalArgumentException.class, () -> { provider.getAuthorizationTokenAsync(null); });
    }
}
