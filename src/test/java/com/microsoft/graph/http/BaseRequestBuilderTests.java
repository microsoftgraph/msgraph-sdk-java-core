package com.microsoft.graph.http;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import okhttp3.Request;

/**
 * Test cases for {@see BaseRequestBuilder}
 */
public class BaseRequestBuilderTests {

    private String expectedRequestUrl = "https://a.b.c/";
    private BaseRequestBuilder<String> baseRequestBuilder;

    @BeforeEach
    public void setUp() throws Exception {
        baseRequestBuilder = new BaseRequestBuilder<>(expectedRequestUrl,null,null){};
    }

    @Test
    public void testNotNull() {
        assertNotNull(baseRequestBuilder);
    }

    @Test
    public void testRequestUrlAndOptions() {
        assertEquals(expectedRequestUrl, baseRequestBuilder.getRequestUrl());
        assertEquals(0, baseRequestBuilder.getOptions().size());
        assertEquals(expectedRequestUrl+"/d", baseRequestBuilder.getRequestUrlWithAdditionalSegment("d"));
        assertEquals(expectedRequestUrl + "('version=1.0')", baseRequestBuilder.getRequestUrlWithAdditionalParameter("version=1.0"));
    }
}
