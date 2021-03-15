package com.microsoft.graph.http;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

public class GraphFatalServiceExceptionTests {

	@Test
	public void testGraphFatalServiceException() {
        GraphFatalServiceException exception = new GraphFatalServiceException("GET", "https://graph.microsoft.com/v1.0/me",new ArrayList<String>(),null,401,"Unauthenticated",new ArrayList<String>(),null, false);
        String expectedMessage = "Unexpected exception returned from the service." +
                "GET https://graph.microsoft.com/v1.0/me\n" +
                "\n" +
                "\n" +
                "401 : Unauthenticated\n" +
                "[...]\n" +
                "\n" +
                "[Some information was truncated for brevity, enable debug logging for more details]";
        String message = exception.getMessage();
        assertEquals(expectedMessage, message);
    }

}
