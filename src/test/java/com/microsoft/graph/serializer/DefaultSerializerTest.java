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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

public class DefaultSerializerTest {

    final ILogger logger = mock(ILogger.class);
    Gson gson = GsonFactory.getGsonInstance(logger);
    DefaultSerializer defaultSerializer = new DefaultSerializer(logger);
    DefaultSerializer defaultNullSerializer = new DefaultSerializer(true, logger);

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
        DefaultSerializer nonNullSerializer = new DefaultSerializer(logger);
        MessageStub message = nonNullSerializer.deserializeObject(testJsonResponse, MessageStub.class);

        // Then
        assertEquals("{}", nonNullSerializer.serializeObject(message));
    }

    @Test
    public void testDefaultNullSerializerDoesIncludeNullValues() {
        // Given
        final String testJsonResponse =
            "{\"@odata.type\": \"#microsoft.graph.messageStub\",\"body\":null}";

        // When
        DefaultSerializer nullSerializer = new DefaultSerializer(true, logger);
        MessageStub message = nullSerializer.deserializeObject(testJsonResponse, MessageStub.class);

        // Then
        assertEquals("{\"body\":null,\"reaction\":null}", nullSerializer.serializeObject(message));
    }

}
