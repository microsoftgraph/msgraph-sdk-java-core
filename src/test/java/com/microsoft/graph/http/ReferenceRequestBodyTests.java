package com.microsoft.graph.http;

import static org.junit.Assert.assertEquals;

import static org.mockito.Mockito.mock;

import org.junit.Test;

import com.google.gson.JsonObject;
import com.microsoft.graph.serializer.ISerializer;

public class ReferenceRequestBodyTests {

	@Test
	public void testRawObject() {
        ReferenceRequestBody body = new ReferenceRequestBody(null);
        ISerializer serializer = mock(ISerializer.class);
        JsonObject jsonObject = new JsonObject();
        body.setRawObject(serializer,jsonObject);
        assertEquals(serializer,body.getSerializer());
        assertEquals(jsonObject,body.getRawObject());
    }

}
