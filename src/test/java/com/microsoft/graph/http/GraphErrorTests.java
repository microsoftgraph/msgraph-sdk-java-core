package com.microsoft.graph.http;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.microsoft.graph.core.GraphErrorCodes;

public class GraphErrorTests {


	@Test
	public void testIsError(){
        String expectedMessage = "test error message";
        GraphError error = new GraphError();
        error.code = "accessDenied"; // the code prop is lower camel cased https://docs.microsoft.com/en-us/graph/errors#code-property
        error.message = expectedMessage;
        assertTrue(error.isError(GraphErrorCodes.ACCESS_DENIED));
        assertEquals(expectedMessage, error.message);
    }

	@Test
    public void testIsNotError() {
        GraphError error = new GraphError();
        error.code = GraphErrorCodes.ACCESS_DENIED.toString();
        assertFalse(error.isError(GraphErrorCodes.UNAUTHENTICATED));
    }

}
