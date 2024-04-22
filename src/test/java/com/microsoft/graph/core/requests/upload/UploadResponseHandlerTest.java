package com.microsoft.graph.core.requests.upload;

import com.microsoft.graph.core.CoreConstants;
import com.microsoft.graph.core.ErrorConstants;
import com.microsoft.graph.core.models.UploadResult;
import com.microsoft.graph.core.models.UploadSession;
import com.microsoft.graph.core.testModels.TestDriveItem;
import com.microsoft.kiota.ApiException;
import com.microsoft.kiota.serialization.JsonParseNodeFactory;
import com.microsoft.kiota.serialization.ParseNodeFactoryRegistry;
import okhttp3.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static com.microsoft.kiota.serialization.ParseNodeFactoryRegistry.defaultInstance;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.time.OffsetDateTime;

class UploadResponseHandlerTest {

    ParseNodeFactoryRegistry registry = defaultInstance;

    @Test
    void GetUploadItemOnCompletedUpload() throws IOException {
        registry.contentTypeAssociatedFactories.put(CoreConstants.MimeTypeNames.APPLICATION_JSON, new JsonParseNodeFactory());

        UploadResponseHandler responseHandler = new UploadResponseHandler(null);
        ResponseBody body = ResponseBody.create("{\n" +
                "   \"id\": \"912310013A123\",\n" +
                "   \"name\": \"largeFile.vhd\",\n" +
                "   \"size\": 33\n" +
                "}"
            , MediaType.parse(CoreConstants.MimeTypeNames.APPLICATION_JSON));
        Response response = new Response.Builder()
            .request(mock(Request.class))
            .protocol(mock(Protocol.class))
            .message("Created")
            .body(body)
            .code(HttpURLConnection.HTTP_CREATED)
            .build();
        OkHttpClient mockHttpClient = getMockClient(response);
        UploadResult<TestDriveItem> result = responseHandler
            .handleResponse(mockHttpClient.newCall(mock(Request.class)).execute(), TestDriveItem::createFromDiscriminatorValue);
        responseHandler.handleResponse(response, parseNode -> {return new TestDriveItem();});
        TestDriveItem item = result.itemResponse;
        assertTrue(result.isUploadSuccessful());
        assertNotNull(item);
        assertEquals("912310013A123", item.id);
        assertEquals("largeFile.vhd", item.name);
        assertEquals(33, item.size);
    }

    @Test
    void GetUploadItemOnCompletedUpdate() throws IOException {
        registry.contentTypeAssociatedFactories.put(CoreConstants.MimeTypeNames.APPLICATION_JSON, new JsonParseNodeFactory());

        UploadResponseHandler responseHandler = new UploadResponseHandler(null);
        ResponseBody body = ResponseBody.create("{\n" +
                "   \"id\": \"912310013A123\",\n" +
                "   \"name\": \"largeFile.vhd\",\n" +
                "   \"size\": 33\n" +
                "}"
            , MediaType.parse(CoreConstants.MimeTypeNames.APPLICATION_JSON));
        Response response = new Response.Builder()
            .request(mock(Request.class))
            .protocol(mock(Protocol.class))
            .body(body)
            .code(HttpURLConnection.HTTP_OK)
            .message("OK")
            .build();
        OkHttpClient mockHttpClient = getMockClient(response);
        UploadResult<TestDriveItem> result = responseHandler
            .handleResponse(mockHttpClient.newCall(mock(Request.class)).execute(), TestDriveItem::createFromDiscriminatorValue);
        responseHandler.handleResponse(response, parseNode -> {return new TestDriveItem();});
        TestDriveItem item = result.itemResponse;
        assertTrue(result.isUploadSuccessful());
        assertNotNull(item);
        assertEquals("912310013A123", item.id);
        assertEquals("largeFile.vhd", item.name);
        assertEquals(33, item.size);
    }

    @Test
    void getFileAttachmentLocationOnCompletedUpload() throws IOException {
        registry.contentTypeAssociatedFactories.put(CoreConstants.MimeTypeNames.APPLICATION_JSON, new JsonParseNodeFactory());

        UploadResponseHandler responseHandler = new UploadResponseHandler(null);
        Response response = new Response.Builder()
            .request(mock(Request.class))
            .protocol(mock(Protocol.class))
            .message("success")
            .code(HttpURLConnection.HTTP_CREATED)
            .header("location", "http://localhost")
            .build();
        OkHttpClient mockClient = getMockClient(response);
        UploadResult<TestDriveItem> result = responseHandler
            .handleResponse(mockClient.newCall(mock(Request.class)).execute()
            ,TestDriveItem::createFromDiscriminatorValue);
        TestDriveItem item = result.itemResponse;

        assertTrue(result.isUploadSuccessful());
        assertNull(item);
        assertEquals("http://localhost", result.location.toString());
    }
    @Test
    void getUploadSessionOnProgressingUpload() throws IOException {
        registry.contentTypeAssociatedFactories.put(CoreConstants.MimeTypeNames.APPLICATION_JSON, new JsonParseNodeFactory());

        UploadResponseHandler responseHandler = new UploadResponseHandler(null);
        ResponseBody body = ResponseBody.create(
            "{\n" +
                "   \"expirationDateTime\": \"2015-01-29T09:21:55.523Z\",\n" +
                "   \"nextExpectedRanges\": [\n" +
                "   \"12345-55232\",\n" +
                "   \"77829-99375\"\n" +
                "   ]" +
                "}"
            , MediaType.parse(CoreConstants.MimeTypeNames.APPLICATION_JSON));
        Response response = new Response.Builder()
            .request(mock(Request.class))
            .protocol(mock(Protocol.class))
            .message("Accepted")
            .body(body)
            .code(HttpURLConnection.HTTP_ACCEPTED)
            .build();
        OkHttpClient mockHttpClient = getMockClient(response);
        UploadResult<TestDriveItem> result = responseHandler
            .handleResponse(mockHttpClient.newCall(mock(Request.class)).execute(), TestDriveItem::createFromDiscriminatorValue);
        UploadSession session = (UploadSession) result.uploadSession;

        assertFalse(result.isUploadSuccessful());
        assertNotNull(session);
        assertTrue(session.getUploadUrl().isEmpty());
        assertEquals(OffsetDateTime.parse("2015-01-29T09:21:55.523Z"), session.getExpirationDateTime());
        assertEquals("12345-55232", session.getNextExpectedRanges().get(0));
        assertEquals("77829-99375", session.getNextExpectedRanges().get(1));
        assertEquals(2, session.getNextExpectedRanges().size());
    }

    @Test
    void throwsServiceExceptionOnErrorResponse() throws IOException {
        UploadResponseHandler responseHandler = new UploadResponseHandler(null);
        ResponseBody body = ResponseBody.create("{\n" +
            "   \"error\": {\n"+
            "       \"code\": \"InvalidAuthenticationToken\",\n" +
            "       \"message\": \"Access token is empty.\",\n" +
            "       \"innerError\": {\n" +
            "           \"request-id\": \"0e4cbf06-018b-4596-8614-50d5f7eef218\",\n" +
            "           \"date\": \"2019-11-21T13:57:37\"\n" +
            "       }" +
            "   }" +
            "}"
            , MediaType.parse(CoreConstants.MimeTypeNames.APPLICATION_JSON));
        Response response = new Response.Builder()
            .request(mock(Request.class))
            .protocol(mock(Protocol.class))
            .message("Error")
            .body(body)
            .code(HttpURLConnection.HTTP_UNAUTHORIZED)
            .build();
        OkHttpClient mockHttpClient = getMockClient(response);

        try {
            responseHandler
                .handleResponse(mockHttpClient.newCall(mock(Request.class)).execute(), TestDriveItem::createFromDiscriminatorValue);
        } catch (ApiException ex) {
            Assertions.assertEquals(ErrorConstants.Codes.GENERAL_EXCEPTION, ex.getMessage());
            assertEquals(HttpURLConnection.HTTP_UNAUTHORIZED, ex.getResponseStatusCode());
        }
    }
    @Test
    void throwsSerializationErrorOnInvalidJson() throws IOException {
        UploadResponseHandler responseHandler = new UploadResponseHandler(null);
        String malformedResponse =
            "   \"error\": {\n"+
            "       \"code\": \"InvalidAuthenticationToken\",\n" +
            "       \"message\": \"Access token is empty.\",\n" +
            "       \"innerError\": {\n" +
            "           \"request-id\": \"0e4cbf06-018b-4596-8614-50d5f7eef218\",\n" +
            "           \"date\": \"2019-11-21T13:57:37\"\n" +
            "       }" +
            "   }" +
            "}";
        //Missing open brace
        ResponseBody body = ResponseBody.create(malformedResponse, MediaType.parse(CoreConstants.MimeTypeNames.APPLICATION_JSON));
        Response response = new Response.Builder()
            .request(mock(Request.class))
            .protocol(mock(Protocol.class))
            .message("Error")
            .body(body)
            .code(HttpURLConnection.HTTP_UNAUTHORIZED)
            .build();
        OkHttpClient mockHttpClient = getMockClient(response);
        try {
            responseHandler
                .handleResponse(mockHttpClient.newCall(mock(Request.class)).execute(), TestDriveItem::createFromDiscriminatorValue);
        } catch (ApiException ex) {
            assertEquals(ErrorConstants.Codes.GENERAL_EXCEPTION, ex.getMessage());
        }
    }

    public static OkHttpClient getMockClient(final Response response) throws IOException {
        final OkHttpClient mockClient = mock(OkHttpClient.class);
        final Call remoteCall = mock(Call.class);
        final Dispatcher dispatcher = new Dispatcher();
        when(remoteCall.execute()).thenReturn(response);
        doAnswer(
                        (Answer<Void>)
                                invocation -> {
                                    Callback callback = invocation.getArgument(0);
                                    callback.onResponse(null, response);
                                    return null;
                                })
                .when(remoteCall)
                .enqueue(any(Callback.class));
        when(mockClient.dispatcher()).thenReturn(dispatcher);
        when(mockClient.newCall(any())).thenReturn(remoteCall);
        return mockClient;
    }
}
