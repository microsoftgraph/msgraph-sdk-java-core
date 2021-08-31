package com.microsoft.graph.serializer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Valentin Popov valentin@archiva.ru on 31.08.2021.
 */
class DerivedClassIdentifierTest {

    @Test
    void oDataTypeToClassName() {
        Assertions.assertEquals("com.microsoft.graph.models.Message",
            DerivedClassIdentifier.oDataTypeToClassName("#Microsoft.Graph.Message"));
    }
}
