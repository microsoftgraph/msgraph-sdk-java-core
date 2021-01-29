package com.microsoft.graph.http;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import com.microsoft.graph.core.GraphErrorCodes;
import com.microsoft.graph.logger.ILogger;
import com.microsoft.graph.logger.LoggerLevel;
import com.microsoft.graph.options.HeaderOption;
import com.microsoft.graph.serializer.DefaultSerializer;
import com.microsoft.graph.serializer.ISerializer;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
            final IHttpRequest mRequest = mock(IHttpRequest.class);
            when(mRequest.getRequestUrl()).thenReturn(new URL("https://graph.microsoft.com/v1.0/me"));
            when(mRequest.getHttpMethod()).thenReturn(HttpMethod.GET);
            mProvider.send(mRequest, Object.class, null);
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
        when(logger.getLoggingLevel()).thenReturn(LoggerLevel.DEBUG);
        final ISerializer serializer = mock(ISerializer.class);
        when(serializer.deserializeObject(anyString(), any())).thenReturn(toSerialize);

        mProvider = new CoreHttpProvider(serializer,
                logger,
                new OkHttpClient.Builder().build());

        try {
            final IHttpRequest mRequest = mock(IHttpRequest.class);
            when(mRequest.getRequestUrl()).thenReturn(new URL("https://graph.microsoft.com/v1.0/me"));
            when(mRequest.getHttpMethod()).thenReturn(HttpMethod.GET);
            mProvider.send(mRequest, Object.class, null);
            fail("Expected exception in previous statement");
        } catch (final GraphServiceException e) {
        	assertFalse(e.getMessage().indexOf("truncated") > 0);
        	assertTrue(e.getMessage().indexOf("The raw request was invalid") < 0);
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
    private void setCoreHttpProvider(final Object toSerialize) throws IOException {
        final OkHttpClient mClient = mock(OkHttpClient.class);
        final Call mCall = mock(Call.class);
        when(mClient.newCall(any(Request.class))).thenReturn(mCall);
        when(mCall.execute()).thenReturn(new Response
                                                .Builder()
                                                .code(503)
                                                .message("Service Unavailable")
                                                .protocol(Protocol.HTTP_1_1)
                                                .request(new Request.Builder().url("https://graph.microsoft.com/v1.0/me").build())
                                                .addHeader("Content-type", "application/json")
                                                .body(ResponseBody.create(new GsonBuilder().setPrettyPrinting().create().toJson(toSerialize),
                                                                        MediaType.parse("application/json")))
                                                .build());
        mProvider = new CoreHttpProvider(new DefaultSerializer(mock(ILogger.class)),
                mock(ILogger.class),
                mClient);
    }

}
