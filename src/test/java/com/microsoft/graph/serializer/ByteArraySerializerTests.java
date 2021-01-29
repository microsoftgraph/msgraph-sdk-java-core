package com.microsoft.graph.serializer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

public class ByteArraySerializerTests {

	@Test
	public void testByteSerialization() throws Exception {
        String expectedString = "abcd";
        String serializeString = ByteArraySerializer.serialize(new byte[]{105,-73,29});
        assertEquals(expectedString, serializeString);
        assertFalse(ByteArraySerializer.serialize(new byte[]{1,2,3}).equals(ByteArraySerializer.serialize(new byte[]{1,2,3,4})));
    }

	@Test
    public void testStringDeserialization() throws Exception {
        byte[] deserializeBytes = ByteArraySerializer.deserialize("abcd");
        assertEquals(3, deserializeBytes.length);
    }

}
