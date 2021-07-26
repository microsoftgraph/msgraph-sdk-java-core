package com.microsoft.graph.serializer;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.microsoft.graph.logger.ILogger;
import com.microsoft.graph.models.ReactionStub;
import com.microsoft.graph.models.SubReactionStub1;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

public class DefaultSerializerTest {

    final ILogger logger = mock(ILogger.class);
    Gson gson = GsonFactory.getGsonInstance(logger);
    DefaultSerializer defaultSerializer = new DefaultSerializer(logger);

    @Test
    public void testDeserializationOfObjectWithODataTypeProperty() {
        // Given
        final String testJsonResponse =
            "{\"@odata.type\": \"#microsoft.graph.subReactionStub1\"}";

        // When
        ReactionStub reaction = defaultSerializer.deserializeObject(testJsonResponse, ReactionStub.class);

        // Then
        assertTrue(reaction instanceof SubReactionStub1);
        assertEquals("{\"@odata.type\":\"#microsoft.graph.subReactionStub1\"}", reaction.getRawObject());
        Mockito.verify(reaction.additionalDataManager()).setAdditionalData(gson.fromJson(testJsonResponse, JsonElement.class).getAsJsonObject());
    }

}
