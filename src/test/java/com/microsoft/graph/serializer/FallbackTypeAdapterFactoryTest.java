package com.microsoft.graph.serializer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.microsoft.graph.http.BaseCollectionResponse;
import com.microsoft.graph.logger.ILogger;
import com.microsoft.graph.models.MessageStub;
import com.microsoft.graph.models.MessagesCollectionResponseStub;
import com.microsoft.graph.models.SubReactionStub1;
import com.microsoft.graph.models.SubReactionStub2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

public class FallbackTypeAdapterFactoryTest {

    /**
     * This test covers the scenario of objects not being
     * properly deserialized (@odata.type not taken into account):
     * https://github.com/microsoftgraph/msgraph-sdk-java-core/pull/249
     */
    @Test
    public void testDeserializationOfNestedODataTypeAnnotatedObjects() {
        final ILogger logger = mock(ILogger.class);
        final Gson gsonInstance = GsonFactory.getGsonInstance(logger);

        final Type listType = new TypeToken<MessagesCollectionResponseStub>(){}.getType();
        final String testJsonResponse =
            "{\"value\": " +
            "  [" +
            "    {\"name\": \"parent1\",\"reaction\": {\"@odata.type\": \"#microsoft.graph.subReactionStub1\"}}," +
            "    {\"name\": \"parent2\",\"reaction\": {\"@odata.type\": \"#microsoft.graph.subReactionStub2\"}}" +
            "  ]" +
            "}";

        final BaseCollectionResponse<MessageStub> baseCollectionResponse = gsonInstance.fromJson(testJsonResponse, listType);

        assertNotNull(baseCollectionResponse.value);

        final MessageStub messageStub1 = baseCollectionResponse.value.get(0);
        final MessageStub messageStub2 = baseCollectionResponse.value.get(1);

        assertTrue(messageStub1.reaction instanceof SubReactionStub1);
        assertTrue(messageStub2.reaction instanceof SubReactionStub2);
    }

}
