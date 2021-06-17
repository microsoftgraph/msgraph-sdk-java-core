package com.microsoft.graph.serializer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import com.microsoft.graph.logger.ILogger;

import org.junit.jupiter.api.Test;

class CollectionResponseOfPrimitivesTests {
    @Test
    void DeserializesCollectionOfStrings() {
        final var serializer = new DefaultSerializer(mock(ILogger.class));
        final var serializedValue = "{\"@odata.context\": \"https://graph.microsoft.com/v1.0/$metadata#Collection(Edm.String)\",\"value\": [\"b72e90c8-3d3a-457e-8ca0-0fdde204d320\"]}";
        final var result = serializer.deserializeObject(serializedValue, CollectionResponseOfPrimitives.class);
        assertNotNull(result);
        assertNotNull(result.value);
        assertNotNull(result.additionalDataManager());
        assertEquals("https://graph.microsoft.com/v1.0/$metadata#Collection(Edm.String)", result.additionalDataManager().get("@odata.context").getAsString());
        assertEquals(1, result.value.size());
    }
}
