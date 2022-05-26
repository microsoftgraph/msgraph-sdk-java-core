package com.microsoft.graph.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ClientExceptionTests {

    private ClientException clientException;
    private String expectMessage = "This is test exception message";

	@BeforeEach
	public void setUp() {
        clientException = new ClientException(expectMessage, null);
	}

	@Test
	public void testNotNull() {
        assertNotNull(clientException);
    }

	@Test
    public void testClientException() {
        assertEquals(expectMessage, clientException.getMessage());
    }

}
