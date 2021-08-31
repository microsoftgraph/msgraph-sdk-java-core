package com.microsoft.graph.serializer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Created by Valentin Popov valentin@archiva.ru on 30.08.2021.
 */
class DefaultSerializerTest {

    @Test
    void oDataTypeToClassName() {
        Assertions.assertEquals("com.microsoft.graph.models.Message",
            DefaultSerializer.oDataTypeToClassName("#Microsoft.Graph.Message"));
    }
}
