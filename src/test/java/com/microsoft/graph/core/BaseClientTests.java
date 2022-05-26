package com.microsoft.graph.core;

import static org.mockito.Mockito.mock;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okio.BufferedSink;
import okio.Okio;

import com.microsoft.graph.http.IHttpProvider;
import com.google.gson.JsonElement;
import com.microsoft.graph.http.CoreHttpProvider;
import com.microsoft.graph.http.CustomRequest;
import com.microsoft.graph.http.HttpMethod;
import com.microsoft.graph.logger.ILogger;
import com.microsoft.graph.serializer.DefaultSerializer;
import com.microsoft.graph.serializer.ISerializer;

/**
 * Test cases for {@see BaseClient}
 */
public class BaseClientTests {
	public static final String DEFAULT_GRAPH_ENDPOINT = "https://graph.microsoft.com/v1.0";
    private BaseClient<Request> baseClient;
    private IHttpProvider<Request> mHttpProvider;
    private ILogger mLogger;
    private ISerializer mSerializer;

	@BeforeEach
	public void setUp() {
        baseClient = new BaseClient<>();
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
        baseClient.setHttpProvider(new CoreHttpProvider(new DefaultSerializer(mLogger), mLogger, new OkHttpClient.Builder().build()));
        final CustomRequestBuilder<JsonElement> simpleRequestBuilder = baseClient.customRequest("");
        assertNotNull(simpleRequestBuilder);
        final CustomRequestBuilder<String> stringRequestBuilder = baseClient.customRequest("", String.class);
        assertNotNull(stringRequestBuilder);
        final CustomRequest<String> abs = stringRequestBuilder.buildRequest();
        abs.setHttpMethod(HttpMethod.POST);
        final Request nat = abs.getHttpRequest("somestring");
        assertEquals("\"somestring\"", getStringFromRequestBody(nat));
        assertEquals("application", nat.body().contentType().type());
        assertEquals("json", nat.body().contentType().subtype());
    }
    private String getStringFromRequestBody(Request request) {
        try {
            try(final ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                final BufferedSink buffer = Okio.buffer(Okio.sink(out));
                final RequestBody body = request.body();
                if(body != null)
                    body.writeTo(buffer);
                try(final ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray())) {
                    return CoreHttpProvider.streamToString(in);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }
}
