package com.microsoft.graph.mocks;

import com.azure.core.credential.AccessToken;
import com.azure.core.credential.TokenCredential;
import com.azure.core.credential.TokenRequestContext;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;


public class MockTokenCredential {

    public final static String testToken = "CredentialTestToken";

    public static TokenCredential getMockTokenCredential() {
        TokenCredential mockCredential = Mockito.mock(TokenCredential.class);
        TokenRequestContext context = new TokenRequestContext();
        Mono<AccessToken> token = Mono.just(new AccessToken(testToken, OffsetDateTime.now().plusMinutes(10)));
        Mockito.when(mockCredential.getToken(context).thenReturn(token));
        return mockCredential;
    }

}
