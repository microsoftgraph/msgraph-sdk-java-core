package com.microsoft.graph.mocks;

import com.azure.core.credential.AccessToken;
import com.azure.core.credential.TokenCredential;
import com.azure.core.credential.TokenRequestContext;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.time.OffsetDateTime;


public class MockTokenCredential {

    TokenCredential mockCredential;
    TokenRequestContext context;
    String tokenValue = "TestTokenCredential";

    public MockTokenCredential() {
        this.mockCredential = Mockito.mock(TokenCredential.class);
        this.context = Mockito.mock(TokenRequestContext.class);
        AccessToken token = new AccessToken("TestTokenCredential", OffsetDateTime.now().plusMinutes(10));

    }

}
