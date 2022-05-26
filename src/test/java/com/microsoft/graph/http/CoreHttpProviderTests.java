package com.microsoft.graph.http;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import com.microsoft.graph.core.GraphErrorCodes;
import com.microsoft.graph.core.IBaseClient;
import com.microsoft.graph.httpcore.middlewareoption.IShouldRedirect;
import com.microsoft.graph.httpcore.middlewareoption.IShouldRetry;
import com.microsoft.graph.httpcore.middlewareoption.RedirectOptions;
import com.microsoft.graph.httpcore.middlewareoption.RetryOptions;
import com.microsoft.graph.logger.ILogger;
import com.microsoft.graph.logger.LoggerLevel;
import com.microsoft.graph.options.HeaderOption;
import com.microsoft.graph.options.Option;
import com.microsoft.graph.serializer.DefaultSerializer;
import com.microsoft.graph.serializer.ISerializer;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import okio.Buffer;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.RequestBody;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CoreHttpProviderTests {

    private CoreHttpProvider mProvider;
    private Gson GSON = new GsonBuilder().create();

    @Test
    @SuppressFBWarnings
    void testErrorResponse() throws Exception {
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
    @SuppressFBWarnings
    void testVerboseErrorResponse() throws Exception {
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
    void testHasHeaderReturnsTrue() {
        HeaderOption h = new HeaderOption("name", "value");
        assertTrue(CoreHttpProvider.hasHeader(Arrays.asList(h), "name"));
    }

    @Test
    void testHasHeaderReturnsTrueWhenDifferentCase() {
        HeaderOption h = new HeaderOption("name", "value");
        assertTrue(CoreHttpProvider.hasHeader(Arrays.asList(h), "NAME"));
    }

    @Test
    void testHasHeaderReturnsFalse() {
        HeaderOption h = new HeaderOption("name", "value");
        assertFalse(CoreHttpProvider.hasHeader(Arrays.asList(h), "blah"));
    }

    @Test
    void testStreamToStringReturnsData() {
        String data = GSON.toJson(Maps.newHashMap(
                ImmutableMap.<String, String>builder()
                        .put("key", "value")
                        .build()));
        final InputStream inputStream = new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));

        String convertedData = CoreHttpProvider.streamToString(inputStream);
        assertEquals(data, convertedData);
    }

    @Test
    void testStreamToStringReturnsEmpty() {
        final InputStream inputStream = new ByteArrayInputStream(new byte[0]);

        String convertedData = CoreHttpProvider.streamToString(inputStream);
        assertEquals("", convertedData);
    }
    @Test
    void emptyPostContentTypeIsNotReset() {
        final String contentTypeValue = "application/json";
        final HeaderOption ctype = new HeaderOption("Content-Type", contentTypeValue);
        final ArrayList<Option> options = new ArrayList<>();
        options.add(ctype);
        final IHttpRequest absRequest = new BaseRequest<String>("https://localhost", mock(IBaseClient.class), options, String.class) {{
            this.setHttpMethod(HttpMethod.POST);
        }};
        final ISerializer serializer = mock(ISerializer.class);
        final ILogger logger = mock(ILogger.class);
        mProvider = new CoreHttpProvider(serializer,
                logger,
                new OkHttpClient.Builder().build());
        final Request request = mProvider.getHttpRequest(absRequest, String.class, null);
        assertEquals(contentTypeValue, request.body().contentType().toString());
    }
    @Test
    void emptyPostContentTypeIsNotSet() {
        final IHttpRequest absRequest = new BaseRequest<String>("https://localhost", mock(IBaseClient.class), Collections.emptyList(), String.class) {{
            this.setHttpMethod(HttpMethod.POST);
        }};
        final ISerializer serializer = mock(ISerializer.class);
        final ILogger logger = mock(ILogger.class);
        mProvider = new CoreHttpProvider(serializer,
                logger,
                new OkHttpClient.Builder().build());
        final Request request = mProvider.getHttpRequest(absRequest, String.class, null);
        assertNull(request.body().contentType());
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
    @Test
    void getHttpRequestDoesntSetRetryOrRedirectOptionsOnDefaultValues() throws MalformedURLException {
        final IHttpRequest absRequest = mock(IHttpRequest.class);
        when(absRequest.getRequestUrl()).thenReturn(new URL("https://graph.microsoft.com/v1.0/me"));
        when(absRequest.getHttpMethod()).thenReturn(HttpMethod.GET);
        final ISerializer serializer = mock(ISerializer.class);
        final ILogger logger = mock(ILogger.class);

        mProvider = new CoreHttpProvider(serializer,
                logger,
                new OkHttpClient.Builder().build());

        when(absRequest.getMaxRedirects()).thenReturn(RedirectOptions.DEFAULT_MAX_REDIRECTS);
        when(absRequest.getShouldRedirect()).thenReturn(RedirectOptions.DEFAULT_SHOULD_REDIRECT);
        Request request = mProvider.getHttpRequest(absRequest, String.class, null);
        RedirectOptions redirectOptions = request.tag(RedirectOptions.class);

        assertNull(redirectOptions);

        when(absRequest.getShouldRetry()).thenReturn(RetryOptions.DEFAULT_SHOULD_RETRY);
        when(absRequest.getMaxRetries()).thenReturn(RetryOptions.DEFAULT_MAX_RETRIES);
        when(absRequest.getDelay()).thenReturn(RetryOptions.DEFAULT_DELAY);

        request = mProvider.getHttpRequest(absRequest, String.class, null);
        RetryOptions retryOptions = request.tag(RetryOptions.class);

        assertNull(retryOptions);
    }

    @Test
    void getHttpRequestSetsRetryOrRedirectOptionsOnNonDefaultValues() throws MalformedURLException {
        final IHttpRequest absRequest = mock(IHttpRequest.class);
        when(absRequest.getRequestUrl()).thenReturn(new URL("https://graph.microsoft.com/v1.0/me"));
        when(absRequest.getHttpMethod()).thenReturn(HttpMethod.GET);
        final ISerializer serializer = mock(ISerializer.class);
        final ILogger logger = mock(ILogger.class);

        mProvider = new CoreHttpProvider(serializer,
                logger,
                new OkHttpClient.Builder().build());

        // testing all pairs to cover all branches
        when(absRequest.getMaxRedirects()).thenReturn(RedirectOptions.DEFAULT_MAX_REDIRECTS -1);
        when(absRequest.getShouldRedirect()).thenReturn(mock(IShouldRedirect.class));
        Request request = mProvider.getHttpRequest(absRequest, String.class, null);
        RedirectOptions redirectOptions = request.tag(RedirectOptions.class);

        assertNotNull(redirectOptions);

        when(absRequest.getMaxRedirects()).thenReturn(RedirectOptions.DEFAULT_MAX_REDIRECTS);
        when(absRequest.getShouldRedirect()).thenReturn(mock(IShouldRedirect.class));
        request = mProvider.getHttpRequest(absRequest, String.class, null);
        redirectOptions = request.tag(RedirectOptions.class);

        assertNotNull(redirectOptions);

        // testing all pairs to cover all branches
        when(absRequest.getShouldRetry()).thenReturn(mock(IShouldRetry.class));
        when(absRequest.getMaxRetries()).thenReturn(RetryOptions.DEFAULT_MAX_RETRIES-1);
        when(absRequest.getDelay()).thenReturn(RetryOptions.DEFAULT_DELAY-1);

        request = mProvider.getHttpRequest(absRequest, String.class, null);
        RetryOptions retryOptions = request.tag(RetryOptions.class);

        assertNotNull(retryOptions);

        when(absRequest.getShouldRetry()).thenReturn(mock(IShouldRetry.class));
        when(absRequest.getMaxRetries()).thenReturn(RetryOptions.DEFAULT_MAX_RETRIES);
        when(absRequest.getDelay()).thenReturn(RetryOptions.DEFAULT_DELAY-1);

        request = mProvider.getHttpRequest(absRequest, String.class, null);
        retryOptions = request.tag(RetryOptions.class);

        assertNotNull(retryOptions);

        when(absRequest.getShouldRetry()).thenReturn(mock(IShouldRetry.class));
        when(absRequest.getMaxRetries()).thenReturn(RetryOptions.DEFAULT_MAX_RETRIES);
        when(absRequest.getDelay()).thenReturn(RetryOptions.DEFAULT_DELAY);

        request = mProvider.getHttpRequest(absRequest, String.class, null);
        retryOptions = request.tag(RetryOptions.class);

        assertNotNull(retryOptions);
    }

    @Test
    void getHttpRequestWithTextPlainBodyDoesNotSerializeAsJson() throws IOException {
        final IHttpRequest absRequest = mock(IHttpRequest.class);
        when(absRequest.getRequestUrl()).thenReturn(new URL("https://graph.microsoft.com/v1.0/me"));
        when(absRequest.getHttpMethod()).thenReturn(HttpMethod.POST);
        final ISerializer serializer = mock(ISerializer.class);
        final ILogger logger = mock(ILogger.class);

        mProvider = new CoreHttpProvider(serializer,
            logger,
            new OkHttpClient.Builder().build());

        // GIVEN: A "text/plain" request body
        final HeaderOption option = new HeaderOption("Content-Type", "text/plain");
        when(absRequest.getHeaders()).thenReturn(Arrays.asList(option));
        final String expectedBody = "Plain String Body";

        //WHEN: getHttpRequest is called
        final Request request = mProvider.getHttpRequest(absRequest, String.class, expectedBody);

        // THEN: The serializer must not be called
        verify(serializer, never()).serializeObject(Mockito.any());

        // AND: We expect the request body to contain the plain String, not serialized as Json
        final Buffer buffer = new Buffer();
        final RequestBody requestBody = request.body();
        assertNotNull(requestBody);
        requestBody.writeTo(buffer);
        final String actualRequestBody = buffer.readUtf8();
        assertEquals(expectedBody, actualRequestBody);
    }
}
