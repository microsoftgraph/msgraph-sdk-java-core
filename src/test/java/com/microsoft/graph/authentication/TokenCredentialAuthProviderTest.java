package com.microsoft.graph.authentication;

import com.azure.core.credential.TokenCredential;
import com.microsoft.graph.mocks.MockTokenCredential;

public class TokenCredentialAuthProviderTest {

    private MockTokenCredential mockCredential;

    public TokenCredentialAuthProviderTest() {
        mockCredential = new MockTokenCredential();
    }



}
