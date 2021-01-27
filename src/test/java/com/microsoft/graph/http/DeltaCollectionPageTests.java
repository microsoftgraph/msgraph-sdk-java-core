package com.microsoft.graph.http;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import com.google.gson.JsonPrimitive;

import org.junit.Test;

public class DeltaCollectionPageTests {
    @SuppressWarnings("unchecked")
    @Test
    public void pageContainsDeltaLink() {
        final BaseRequestBuilder<String> mRequestBuilder = mock(BaseRequestBuilder.class);
        final BaseCollectionResponse<String> mResponse = new BaseCollectionResponse<String>() {};
        mResponse.additionalDataManager().put("@odata.deltaLink", new JsonPrimitive("https://somedeltalink"));
        final DeltaCollectionPage<String, BaseRequestBuilder<String>> tPage = new DeltaCollectionPage<>(mResponse, mRequestBuilder);
        assertEquals("https://somedeltalink", tPage.deltaLink);
    }
}
