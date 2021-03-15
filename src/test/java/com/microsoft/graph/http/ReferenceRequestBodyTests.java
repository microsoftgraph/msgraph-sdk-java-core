package com.microsoft.graph.http;

import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;

import com.google.gson.JsonObject;
import com.microsoft.graph.serializer.ISerializer;

public class ReferenceRequestBodyTests {

	@Test
	public void testRawObject() {
        ReferenceRequestBody body = new ReferenceRequestBody("");
        ISerializer serializer = mock(ISerializer.class);
        JsonObject jsonObject = new JsonObject();
        body.setRawObject(serializer,jsonObject);
    }

}
