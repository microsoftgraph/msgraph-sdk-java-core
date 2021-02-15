package com.microsoft.graph.http;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

}
