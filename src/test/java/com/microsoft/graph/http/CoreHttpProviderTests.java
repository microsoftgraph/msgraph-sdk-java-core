package com.microsoft.graph.http;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.anyObject;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.Ignore;
import org.junit.Test;

import okhttp3.OkHttpClient;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.microsoft.graph.core.GraphErrorCodes;
import com.microsoft.graph.logger.ILogger;
import com.microsoft.graph.logger.LoggerLevel;
import com.microsoft.graph.options.HeaderOption;
import com.microsoft.graph.serializer.ISerializer;

@Ignore
public class CoreHttpProviderTests {

    private CoreHttpProvider mProvider;
    private Gson GSON = new GsonBuilder().create();

    @Test
    public void testErrorResponse() throws Exception {
        final GraphErrorCodes expectedErrorCode = GraphErrorCodes.INVALID_REQUEST;
        final String expectedMessage = "Test error!";
        final GraphErrorResponse toSerialize = new GraphErrorResponse();
        toSerialize.error = new GraphError();
        toSerialize.error.code = expectedErrorCode.toString();
        toSerialize.error.message = expectedMessage;
        toSerialize.error.innererror = null;

        setCoreHttpProvider(toSerialize);
        try {
            mProvider.send(mock(IHttpRequest.class), Object.class, null);
            fail("Expected exception in previous statement");
        } catch (final GraphServiceException e) {
        	assertTrue(e.getMessage().indexOf("truncated") > 0);
            assertEquals(expectedMessage, e.getServiceError().message);
        }
    }

    @Test
    public void testVerboseErrorResponse() throws Exception {
    	final GraphErrorCodes expectedErrorCode = GraphErrorCodes.INVALID_REQUEST;
        final String expectedMessage = "Test error!";
        final GraphErrorResponse toSerialize = new GraphErrorResponse();
        toSerialize.error = new GraphError();
        toSerialize.error.code = expectedErrorCode.toString();
        toSerialize.error.message = expectedMessage;
        toSerialize.error.innererror = null;
        final JsonObject raw = new JsonObject();
        raw.add("response", new JsonPrimitive("The raw request was invalid"));
        toSerialize.rawObject = raw;

        final ILogger logger = mock(ILogger.class);
        logger.setLoggingLevel(LoggerLevel.DEBUG);
        final ISerializer serializer = mock(ISerializer.class);
        when(serializer.deserializeObject(anyObject(), anyObject())).thenReturn(toSerialize);

        mProvider = new CoreHttpProvider(serializer,
                logger,
                new OkHttpClient.Builder().build());

        try {
            mProvider.send(mock(IHttpRequest.class), Object.class, null);
            fail("Expected exception in previous statement");
        } catch (final GraphServiceException e) {
        	assertFalse(e.getMessage().indexOf("truncated") > 0);
        	assertTrue(e.getMessage().indexOf("The raw request was invalid") > 0);
        }
    }

    @Test
    public void testHasHeaderReturnsTrue() {
        HeaderOption h = new HeaderOption("name", "value");
        assertTrue(CoreHttpProvider.hasHeader(Arrays.asList(h), "name"));
    }

    @Test
    public void testHasHeaderReturnsTrueWhenDifferentCase() {
        HeaderOption h = new HeaderOption("name", "value");
        assertTrue(CoreHttpProvider.hasHeader(Arrays.asList(h), "NAME"));
    }

    @Test
    public void testHasHeaderReturnsFalse() {
        HeaderOption h = new HeaderOption("name", "value");
        assertFalse(CoreHttpProvider.hasHeader(Arrays.asList(h), "blah"));
    }

    @Test
    public void testStreamToStringReturnsData() {
        String data = GSON.toJson(Maps.newHashMap(
                ImmutableMap.<String, String>builder()
                        .put("key", "value")
                        .build()));
        final InputStream inputStream = new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));

        String convertedData = CoreHttpProvider.streamToString(inputStream);
        assertEquals(data, convertedData);
    }

    @Test
    public void testStreamToStringReturnsEmpty() {
        final InputStream inputStream = new ByteArrayInputStream(new byte[0]);

        String convertedData = CoreHttpProvider.streamToString(inputStream);
        assertEquals("", convertedData);
    }


    /**
     * Configures the http provider for test cases
     * @param toSerialize The object to serialize
     */
    private void setCoreHttpProvider(final Object toSerialize) {
        final ISerializer serializer = mock(ISerializer.class);
        when(serializer.deserializeObject(anyObject(), anyObject())).thenReturn(toSerialize);
        mProvider = new CoreHttpProvider(serializer,
                mock(ILogger.class),
                new OkHttpClient.Builder().build());
    }

}
