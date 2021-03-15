package com.microsoft.graph.authentication;

import com.azure.core.credential.TokenCredential;
import com.microsoft.graph.mocks.MockTokenCredential;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class TokenCredentialAuthProviderTest {

    private static final String testToken = "CredentialTestToken";

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
        assertThrows(NullPointerException.class, () -> { new TokenCredentialAuthProvider(null); }, "should throw on null credentials");
        assertThrows(IllegalArgumentException.class, () -> { new TokenCredentialAuthProvider(null, mock(TokenCredential.class)); }, "should throw on null scopes");
        assertThrows(IllegalArgumentException.class, () -> { new TokenCredentialAuthProvider(new ArrayList<String>(), mock(TokenCredential.class)); }, "should throw on empty scopes");
        final TokenCredentialAuthProvider provider = new TokenCredentialAuthProvider(mock(TokenCredential.class));
        assertThrows(NullPointerException.class, () -> { provider.getAuthorizationTokenAsync(null); }, "should throw on null url");
    }
}
