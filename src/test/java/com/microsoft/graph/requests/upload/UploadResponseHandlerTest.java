package com.microsoft.graph.requests.upload;

import com.microsoft.graph.exceptions.ErrorConstants;
import com.microsoft.graph.exceptions.ServiceException;
import com.microsoft.graph.models.UploadResult;
import com.microsoft.graph.models.UploadSession;
import com.microsoft.graph.testModels.TestDriveItem;
import com.microsoft.kiota.serialization.JsonParseNodeFactory;
import com.microsoft.kiota.serialization.ParseNodeFactoryRegistry;
import okhttp3.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static com.microsoft.kiota.serialization.ParseNodeFactoryRegistry.defaultInstance;

import java.net.HttpURLConnection;
import java.time.OffsetDateTime;
import java.util.concurrent.ExecutionException;

class UploadResponseHandlerTest {

    String contentType = "application/json";
    ParseNodeFactoryRegistry registry = defaultInstance;

    @Test
    void GetUploadItemOnCompletedUpload() throws ExecutionException, InterruptedException {
        registry.contentTypeAssociatedFactories.put(contentType, new JsonParseNodeFactory());

        UploadResponseHandler responseHandler = new UploadResponseHandler(null);
        ResponseBody body = ResponseBody.create("{\n" +
                "   \"id\": \"912310013A123\",\n" +
                "   \"name\": \"largeFile.vhd\",\n" +
                "   \"size\": 33\n" +
                "}"
            , MediaType.get(contentType));
        Response response = new Response.Builder()
            .request(mock(Request.class))
            .protocol(mock(Protocol.class))
            .message("Created")
            .body(body)
            .code(HttpURLConnection.HTTP_CREATED)
            .build();
        UploadResult<TestDriveItem> result = responseHandler
            .handleResponse(response, TestDriveItem::createFromDiscriminatorValue).get();
        responseHandler.handleResponse(response, parseNode -> {return new TestDriveItem();}).get();
        TestDriveItem item = result.itemResponse;
        assertTrue(result.isUploadSuccessful());
        assertNotNull(item);
        assertEquals("912310013A123", item.id);
        assertEquals("largeFile.vhd", item.name);
        assertEquals(33, item.size);
    }
    @Test
    void GetFileAttachmentLocationOnCompletedUpload() throws ExecutionException, InterruptedException {
        registry.contentTypeAssociatedFactories.put(contentType, new JsonParseNodeFactory());

        UploadResponseHandler responseHandler = new UploadResponseHandler(null);
        Response response = new Response.Builder()
            .request(mock(Request.class))
            .protocol(mock(Protocol.class))
            .message("success")
            .body(ResponseBody.create("", MediaType.get(contentType)))
            .code(HttpURLConnection.HTTP_CREATED)
            .header("location", "http://localhost")
            .build();
        UploadResult<TestDriveItem> result = responseHandler
            .handleResponse(response,TestDriveItem::createFromDiscriminatorValue).get();
        TestDriveItem item = result.itemResponse;

        assertTrue(result.isUploadSuccessful());
        assertNull(item);
        assertEquals("http://localhost", result.location.toString());
    }
    @Test
    void GetUploadSessionOnProgressingUpload() throws ExecutionException, InterruptedException {
        registry.contentTypeAssociatedFactories.put(contentType, new JsonParseNodeFactory());

        UploadResponseHandler responseHandler = new UploadResponseHandler(null);
        ResponseBody body = ResponseBody.create(
            "{\n" +
                "   \"expirationDateTime\": \"2015-01-29T09:21:55.523Z\",\n" +
                "   \"nextExpectedRanges\": [\n" +
                "   \"12345-55232\",\n" +
                "   \"77829-99375\"\n" +
                "   ]" +
                "}"
            , MediaType.get(contentType));
        Response response = new Response.Builder()
            .request(mock(Request.class))
            .protocol(mock(Protocol.class))
            .message("OK")
            .body(body)
            .code(HttpURLConnection.HTTP_OK)
            .build();
        UploadResult<TestDriveItem> result = responseHandler
            .handleResponse(response, TestDriveItem::createFromDiscriminatorValue).get();
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
    void ThrowsServiceExceptionOnErrorResponse() throws InterruptedException {
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
            , MediaType.get(contentType));
        Response response = new Response.Builder()
            .request(mock(Request.class))
            .protocol(mock(Protocol.class))
            .message("Error")
            .body(body)
            .code(HttpURLConnection.HTTP_UNAUTHORIZED)
            .build();

        try {
            responseHandler
                .handleResponse(response, TestDriveItem::createFromDiscriminatorValue).get();
        } catch (ExecutionException ex) {
            ServiceException se = (ServiceException) ex.getCause();
            assertEquals(ErrorConstants.Codes.GENERAL_EXCEPTION, se.getMessage());
            assertEquals(HttpURLConnection.HTTP_UNAUTHORIZED, se.responseStatusCode);
        }
    }
    @Test
    void ThrowsSerializationErrorOnInvalidJson() throws InterruptedException {
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
        ResponseBody body = ResponseBody.create(malformedResponse, MediaType.get(contentType));
        Response response = new Response.Builder()
            .request(mock(Request.class))
            .protocol(mock(Protocol.class))
            .message("Error")
            .body(body)
            .code(HttpURLConnection.HTTP_UNAUTHORIZED)
            .build();
        try {
            responseHandler
                .handleResponse(response, TestDriveItem::createFromDiscriminatorValue).get();
        } catch (ExecutionException ex) {
            ServiceException se = (ServiceException) ex.getCause();
            assertEquals(ErrorConstants.Codes.GENERAL_EXCEPTION, se.getMessage());
            assertEquals(malformedResponse, se.getRawResponseBody());
        }
    }
}
