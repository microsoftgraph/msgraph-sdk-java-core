package com.microsoft.graph.http;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;

import com.google.gson.JsonObject;
import com.microsoft.graph.serializer.ISerializer;

public class GraphErrorResponseTests {

	@Test
	public void testSetRawObject() {
        JsonObject expectedJson = new JsonObject();
        GraphErrorResponse errorResponse = new GraphErrorResponse();
        errorResponse.setRawObject(mock(ISerializer.class),expectedJson);
        assertEquals(expectedJson, errorResponse.rawObject);
    }

    @Test
    public void testGraphErrorResponseCopy() {
        String expectedMessage0 = "test error message 0";
        String expectedErrorCode = "accessDenied";
        GraphError error = new GraphError();
        error.message = expectedMessage0;

        String expectedMessage1 = "test error message 1";
        GraphInnerError innerError = new GraphInnerError();
        innerError.debugMessage = expectedMessage1;

        String expectedMessage2 = "test error message 2";
        GraphInnerError innerError2 = new GraphInnerError();
        innerError2.debugMessage = expectedMessage2;

        String expectedMessage3 = "test error message 3";
        GraphInnerError innerError3 = new GraphInnerError();
        innerError3.debugMessage = expectedMessage3;

        //Set the errors in the following order
        //Error -> InnerError1 -> InnerError2 -> InnerError3
        innerError2.innererror = innerError3;
        innerError.innererror = innerError2;
        error.innererror = innerError;

	    JsonObject expectedJson = new JsonObject();
	    GraphErrorResponse errorResponse = new GraphErrorResponse();
	    errorResponse.setRawObject(mock(ISerializer.class),expectedJson);
        errorResponse.error = error;

        //Copy the errorResponse and its subsequent innerErrors
        GraphErrorResponse errorResponseCopy = errorResponse.copy();
        //Ensure subsequent innerErrors have the expected messages in the expected order.
        assertEquals(errorResponseCopy.error.message, expectedMessage0);
        assertEquals(errorResponseCopy.error.innererror.debugMessage, expectedMessage1);
        assertEquals(errorResponseCopy.error.innererror.innererror.debugMessage, expectedMessage2);
        assertEquals(errorResponseCopy.error.innererror.innererror.innererror.debugMessage, expectedMessage3);

        assertEquals(errorResponse.rawObject, errorResponseCopy.rawObject);
    }

    @Test
    void testGraphErrorResponseCopy2() {
        GraphErrorResponse errorResponse = new GraphErrorResponse();;

        //Copy the errorResponse and its subsequent innerErrors
        GraphErrorResponse errorResponseCopy = errorResponse.copy();

        //Ensure default null values are copied without issue.
        assertNull(errorResponseCopy.error);
        assertNull(errorResponseCopy.rawObject);

        assertEquals(errorResponse.rawObject, errorResponseCopy.rawObject);
    }
}
