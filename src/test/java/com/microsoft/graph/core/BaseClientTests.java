package com.microsoft.graph.core;

import static org.mockito.Mockito.mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import okhttp3.OkHttpClient;

import com.microsoft.graph.http.IHttpProvider;
import com.google.gson.JsonObject;
import com.microsoft.graph.http.CoreHttpProvider;
import com.microsoft.graph.logger.ILogger;
import com.microsoft.graph.serializer.ISerializer;

/**
 * Test cases for {@see BaseClient}
 */
public class BaseClientTests {
	public static final String DEFAULT_GRAPH_ENDPOINT = "https://graph.microsoft.com/v1.0";
    private BaseClient baseClient;
    private IHttpProvider mHttpProvider;
    private ILogger mLogger;
    private ISerializer mSerializer;

	@Before
	public void setUp() throws Exception {
        baseClient = new BaseClient();
        mLogger = mock(ILogger.class);
        mSerializer = mock(ISerializer.class);
        mHttpProvider = new CoreHttpProvider(mSerializer,
            mLogger,
            new OkHttpClient.Builder().build());
	}

	@Test
	public void testNotNull() {
        assertNotNull(baseClient);
        assertNotNull(mHttpProvider);
        assertNotNull(mLogger);
        assertNotNull(mSerializer);
    }

	@Test
    public void testEndPoint() {
        assertEquals(DEFAULT_GRAPH_ENDPOINT, baseClient.getServiceRoot());
        String expectedServiceRoot = "https://foo.bar";
        baseClient.setServiceRoot(expectedServiceRoot);
        assertEquals(expectedServiceRoot, baseClient.getServiceRoot());
    }

	@Test
    public void testHttpProvider() {
        assertNull(baseClient.getHttpProvider());
        baseClient.setHttpProvider(mHttpProvider);
        assertEquals(mHttpProvider, baseClient.getHttpProvider());
    }

	public void testLogger() {
        assertNull(baseClient.getLogger());
        baseClient.setLogger(mLogger);
        assertEquals(mLogger, baseClient.getLogger());
    }

    @Test
    public void testSerializer() {
        assertNull(baseClient.getSerializer());
        baseClient.setSerializer(mSerializer);
        assertEquals(mSerializer, baseClient.getSerializer());
    }
    @Test
    public void testCustomRequest() {
        final CustomRequestBuilder<JsonObject> simpleRequest = baseClient.customRequest("");
        assertNotNull(simpleRequest);
        final CustomRequestBuilder<String> stringRequest = baseClient.customRequest("", String.class);
        assertNotNull(stringRequest);
    }
}
