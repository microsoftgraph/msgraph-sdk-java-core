package com.microsoft.graph.serializer;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.microsoft.graph.http.BaseCollectionResponse;
import com.microsoft.graph.logger.ILogger;
import com.microsoft.graph.models.MessageStub;
import com.microsoft.graph.models.MessagesCollectionResponseStub;
import com.microsoft.graph.models.ReactionStub;
import com.microsoft.graph.models.ReactionsStub;
import com.microsoft.graph.models.SubReactionStub1;
import com.microsoft.graph.models.SubReactionStub2;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

/**
 * Covers the scenario of objects not being properly deserialized
 * (@odata.type not taken into account):
 * https://github.com/microsoftgraph/msgraph-sdk-java-core/pull/249
 */
public class ODataTypeParametrizedIJsonBackedTypedAdapterTest {

    final ILogger logger = mock(ILogger.class);
    final Gson gsonInstance = GsonFactory.getGsonInstance(logger);

    @Test
    public void testDeserializationOfObjectWithODataTypeProperty() {
        // Given
        final String testJsonResponse =
            "{\"@odata.type\": \"#microsoft.graph.subReactionStub1\"}";

        // When
        ReactionStub reaction = gsonInstance.fromJson(testJsonResponse, ReactionStub.class);

        // Then
        assertTrue(reaction instanceof SubReactionStub1);
    }

    @Test
    public void testDeserializationOfPropertyWithODataTypeProperty() {
        // Given
        final String testJsonResponse =
            "{\"body\": \"message1\",\"reaction\": {\"@odata.type\": \"#microsoft.graph.subReactionStub2\"}}";

        // When
        MessageStub message = gsonInstance.fromJson(testJsonResponse, MessageStub.class);

        // Then
        assertTrue(message.reaction instanceof SubReactionStub2);
    }

    @Test
    public void testDeserializationOfCollectionPropertyContainingObjectsWithODataTypeProperty() {
        // Given
        final String testJsonResponse =
            "{ reactions : [" +
                "{\"@odata.type\": \"#microsoft.graph.subReactionStub1\"}," +
                "{\"@odata.type\": \"#microsoft.graph.subReactionStub2\"}," +
                "{\"@odata.type\": \"#microsoft.graph.subReactionStub1\"}," +
            "]}";

        // When
        ReactionsStub reactions = gsonInstance.fromJson(testJsonResponse, ReactionsStub.class);

        // Then
        assertNotNull(reactions.reactions);
        assertTrue(reactions.reactions.get(0) instanceof SubReactionStub1);
        assertTrue(reactions.reactions.get(1) instanceof SubReactionStub2);
        assertTrue(reactions.reactions.get(2) instanceof SubReactionStub1);
    }

    @Test
    public void testDeserializationOfNestedODataTypeAnnotatedObjects() {
        // Given
        final Type listType = new TypeToken<MessagesCollectionResponseStub>(){}.getType();
        final String testJsonResponse =
            "{\"value\": " +
            "  [" +
            "    {\"body\": \"message1\",\"reaction\": {\"@odata.type\": \"#microsoft.graph.subReactionStub1\"}}," +
            "    {\"body\": \"message2\",\"reaction\": {\"@odata.type\": \"#microsoft.graph.subReactionStub2\"}}" +
            "  ]" +
            "}";

        // When
        final BaseCollectionResponse<MessageStub> baseCollectionResponse = gsonInstance.fromJson(testJsonResponse, listType);

        // Then
        assertNotNull(baseCollectionResponse.value);

        final MessageStub messageStub1 = baseCollectionResponse.value.get(0);
        final MessageStub messageStub2 = baseCollectionResponse.value.get(1);

        assertTrue(messageStub1.reaction instanceof SubReactionStub1);
        assertTrue(messageStub2.reaction instanceof SubReactionStub2);
    }
}
