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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static com.microsoft.kiota.serialization.ParseNodeFactoryRegistry.defaultInstance;

import java.net.HttpURLConnection;
import java.time.OffsetDateTime;

class UploadResponseHandlerTest {

    ParseNodeFactoryRegistry registry = defaultInstance;

    @Test
    void GetUploadItemOnCompletedUpload() {
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
        UploadResult<TestDriveItem> result = responseHandler
            .handleResponse(response, TestDriveItem::createFromDiscriminatorValue);
        responseHandler.handleResponse(response, parseNode -> {return new TestDriveItem();});
        TestDriveItem item = result.itemResponse;
        assertTrue(result.isUploadSuccessful());
        assertNotNull(item);
        assertEquals("912310013A123", item.id);
        assertEquals("largeFile.vhd", item.name);
        assertEquals(33, item.size);
    }

    @Test
    void GetUploadItemOnCompletedUpdate() {
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
        UploadResult<TestDriveItem> result = responseHandler
            .handleResponse(response, TestDriveItem::createFromDiscriminatorValue);
        responseHandler.handleResponse(response, parseNode -> {return new TestDriveItem();});
        TestDriveItem item = result.itemResponse;
        assertTrue(result.isUploadSuccessful());
        assertNotNull(item);
        assertEquals("912310013A123", item.id);
        assertEquals("largeFile.vhd", item.name);
        assertEquals(33, item.size);
    }

    @Test
    void getFileAttachmentLocationOnCompletedUpload() {
        registry.contentTypeAssociatedFactories.put(CoreConstants.MimeTypeNames.APPLICATION_JSON, new JsonParseNodeFactory());

        UploadResponseHandler responseHandler = new UploadResponseHandler(null);
        Response response = new Response.Builder()
            .request(mock(Request.class))
            .protocol(mock(Protocol.class))
            .message("success")
            .body(ResponseBody.create("", MediaType.parse(CoreConstants.MimeTypeNames.APPLICATION_JSON)))
            .code(HttpURLConnection.HTTP_CREATED)
            .header("location", "http://localhost")
            .build();
        UploadResult<TestDriveItem> result = responseHandler
            .handleResponse(response,TestDriveItem::createFromDiscriminatorValue);
        TestDriveItem item = result.itemResponse;

        assertTrue(result.isUploadSuccessful());
        assertNull(item);
        assertEquals("http://localhost", result.location.toString());
    }
    @Test
    void getUploadSessionOnProgressingUpload() {
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
        UploadResult<TestDriveItem> result = responseHandler
            .handleResponse(response, TestDriveItem::createFromDiscriminatorValue);
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
    void throwsServiceExceptionOnErrorResponse() {
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

        try {
            responseHandler
                .handleResponse(response, TestDriveItem::createFromDiscriminatorValue);
        } catch (ApiException ex) {
            Assertions.assertEquals(ErrorConstants.Codes.GENERAL_EXCEPTION, ex.getMessage());
            assertEquals(HttpURLConnection.HTTP_UNAUTHORIZED, ex.getResponseStatusCode());
        }
    }
    @Test
    void throwsSerializationErrorOnInvalidJson() {
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
        try {
            responseHandler
                .handleResponse(response, TestDriveItem::createFromDiscriminatorValue);
        } catch (ApiException ex) {
            assertEquals(ErrorConstants.Codes.GENERAL_EXCEPTION, ex.getMessage());
        }
    }
}
