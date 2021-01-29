package com.microsoft.graph.http;

import static org.mockito.Mockito.mock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.gson.JsonObject;
import com.microsoft.graph.core.IBaseClient;
import com.microsoft.graph.serializer.ISerializer;

/**
 * Test cases for {@see BaseCollectionPage}
 */
public class BaseCollectionPageTests {

    private BaseRequestBuilder<String> mRequestBuilder;
    private static ArrayList<String> list;
    private BaseCollectionPage<String, BaseRequestBuilder<String>> baseCollectionPage;
    private String requestUrl = "https://a.b.c/";

    @BeforeEach
    public void setUp() throws Exception {
        list = new ArrayList<String>();
        list.add("Object1");
        list.add("Object2");
        list.add("Object3");
        IBaseClient mBaseClient = mock(IBaseClient.class);
        mRequestBuilder = new BaseRequestBuilder<String>(requestUrl, mBaseClient, null) {};
        baseCollectionPage = new BaseCollectionPage<String, BaseRequestBuilder<String>>(list, mRequestBuilder) {};
    }

    @Test
    public void testNotNull() {
        assertNotNull(baseCollectionPage);
    }

    @Test
    public void testCurrentPage() {
        assertEquals(3,baseCollectionPage.getCurrentPage().size());
        assertEquals("Object2", baseCollectionPage.getCurrentPage().get(1));
        Boolean success = false;
        try{
            baseCollectionPage.getCurrentPage().remove(1);
        }catch (UnsupportedOperationException uEx){
            success = true;
        }
        assertTrue(success);
    }

    @Test
    public void testNextPage() {
        assertEquals(mRequestBuilder, baseCollectionPage.getNextPage());
    }

    @Test
    public void testRawObject() {
        ISerializer serializer = mock(ISerializer.class);
        JsonObject jsonObject = new JsonObject();
        baseCollectionPage.setRawObject(serializer,jsonObject);
    }

}
