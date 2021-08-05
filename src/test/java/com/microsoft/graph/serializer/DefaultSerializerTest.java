package com.microsoft.graph.serializer;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.microsoft.graph.logger.ILogger;
import com.microsoft.graph.models.MessageStub;
import com.microsoft.graph.models.ReactionStub;
import com.microsoft.graph.models.SubReactionStub1;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

    @Test
    public void testDefaultSerializerDoesNotIncludeNullValuesByDefault() {
        // Given
        final String testJsonResponse =
            "{\"@odata.type\": \"#microsoft.graph.messageStub\", \"body\": null}";

        // When
        final MessageStub message = defaultSerializer.deserializeObject(testJsonResponse, MessageStub.class);

        // Then
        assertNotNull(message);
        assertEquals("{}", defaultSerializer.serializeObject(message));
    }

    @Test
    public void testDefaultNullSerializerDoesIncludeNullValues() {
        // Given
        final String testJsonResponse =
            "{\"@odata.type\": \"#microsoft.graph.messageStub\",\"body\":null}";

        // When
        final DefaultSerializer nullSerializer = new DefaultSerializer(logger, true);
        final MessageStub message = nullSerializer.deserializeObject(testJsonResponse, MessageStub.class);

        // Then
        assertNotNull(message);
        assertEquals("{\"body\":null,\"reaction\":null}", nullSerializer.serializeObject(message));
    }

}
