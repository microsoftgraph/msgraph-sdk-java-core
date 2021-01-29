package com.microsoft.graph.http;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.google.gson.JsonObject;

public class GraphErrorResponseTests {

	@Test
	public void testSetRawObject() {
        JsonObject expectedJson = new JsonObject();
        GraphErrorResponse errorResponse = new GraphErrorResponse();
        errorResponse.setRawObject(null,expectedJson);
        assertEquals(expectedJson, errorResponse.rawObject);
    }

}
