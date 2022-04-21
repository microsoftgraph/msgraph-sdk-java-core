package com.microsoft.graph.authentication;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.Test;

public class BaseAuthenticationProviderTest {
    final BaseAuthenticationProvider authProvider = new BaseAuthenticationProvider() {

        @Override
        public CompletableFuture<String> getAuthorizationTokenAsync(URL requestUrl) {
            return CompletableFuture.completedFuture((String)null);
        }
    };
    private static final HashSet<String> validGraphHostNames = new HashSet<>(Arrays.asList("graph.microsoft.com", "graph.microsoft.us", "dod-graph.microsoft.us", "graph.microsoft.de", "microsoftgraph.chinacloudapi.cn"));

    @Test
    public void providerAddsTokenOnAllNationalAndPublicClouds() throws MalformedURLException  {

        for(final String hostName : validGraphHostNames) {
            // Arrange
            final URL url = new URL("https://"+ hostName);
            // Act
            final boolean result = authProvider.shouldAuthenticateRequestWithUrl(url);

            // Assert
            assertTrue(result);
        }
    }
    @Test
    public void providerDoesNotAddTokenOnInvalidDomains() throws MalformedURLException {
        // Arrange
        final URL url = new URL("https://localhost");

        //Act
        final boolean result = authProvider.shouldAuthenticateRequestWithUrl(url);

        //Assert
        assertFalse(result);
    }
    @Test
    public void providerDoesNotAddTokenOnInvalidProtocols() throws MalformedURLException {
        //Arrange
        final URL url = new URL("http://graph.microsoft.com");

        //Act
        final boolean result = authProvider.shouldAuthenticateRequestWithUrl(url);

        //Assert
        assertFalse(result);
    }
    @Test
    public void providerDoesNotAddTokenOnNullUrls() throws MalformedURLException {
        //Arrange
        final URL url = (URL)null;

        //Act
        final boolean result = authProvider.shouldAuthenticateRequestWithUrl(url);

        //Assert
        assertFalse(result);
    }
    @Test
    public void providerAddsTokenToCustomHosts() throws MalformedURLException {
        //Arrange
        final URL url = new URL("https://localhost.com");
        authProvider.setCustomHosts(new String[]{"localHost.com"});

        //Act
        final boolean result = authProvider.shouldAuthenticateRequestWithUrl(url);

        //Assert
        assertTrue(result);
    }
}
