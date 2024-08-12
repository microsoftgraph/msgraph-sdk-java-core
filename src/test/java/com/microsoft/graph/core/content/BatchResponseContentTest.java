package com.microsoft.graph.core.content;

import com.microsoft.graph.core.CoreConstants;
import com.microsoft.graph.core.ErrorConstants;
import com.microsoft.graph.core.testModels.*;
import com.microsoft.kiota.ApiException;
import com.microsoft.kiota.serialization.JsonParseNodeFactory;
import com.microsoft.kiota.serialization.ParseNodeFactoryRegistry;
import okhttp3.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Map;

import static com.microsoft.kiota.serialization.ParseNodeFactoryRegistry.defaultInstance;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class BatchResponseContentTest {
    ParseNodeFactoryRegistry registry = defaultInstance;
    Response.Builder defaultBuilder = new Response.Builder().protocol(Protocol.HTTP_1_1).message("Message").request(mock(Request.class));

    @Test
    void BatchResponseContent_InitializeWithNoContent() {
        Response response = defaultBuilder.code(HttpURLConnection.HTTP_BAD_REQUEST).build();
        BatchResponseContent batchResponseContent = new BatchResponseContent(response);
        Map<String, Response> responses = batchResponseContent.getResponses();
        Response response1 = responses.get("1");
        assertNotNull(responses);
        assertNull(response1);
        assertEquals(0,responses.size());
    }
    @Test
    void BatchResponseContent_InitializeWithEmptyResponseContent() {
        String jsonResponse = "{ \"responses\": [] }";
        ResponseBody responseBody = ResponseBody.create(jsonResponse,MediaType.get("application/json"));
        Response response = defaultBuilder.code(HttpURLConnection.HTTP_BAD_REQUEST).body(responseBody).build();
        BatchResponseContent batchResponseContent = new BatchResponseContent(response);
        Map<String, Response> responses = batchResponseContent.getResponses();
        Response response1 = batchResponseContent.getResponseById("1");
        assertNotNull(responses);
        assertNull(response1);
        assertEquals(0,responses.size());
    }
    @Test
    void BatchResponseContent_InitializeWithNullResponseMessage() {
        try{
            new BatchResponseContent(null);
        } catch (NullPointerException ex) {
            assertEquals(ErrorConstants.Messages.NULL_PARAMETER + "batchResponse", ex.getMessage());
        }
    }
    @Test
    void BatchResponseContent_GetResponses() {
        String responseJSON = "{\"responses\":"
            +"[{"
            +"\"id\": \"1\","
            +"\"status\":200,"
            + "\"headers\":{\"Cache-Control\":\"no-cache\",\"OData-Version\":\"4.0\",\"Content-Type\":\"application/json;odata.metadata=minimal;odata.streaming=true;IEEE754Compatible=false;charset=utf-8\"},"
            + "\"body\":{\"@odata.context\":\"https://graph.microsoft.com/v1.0/$metadata#users/$entity\",\"displayName\":\"MOD Administrator\",\"jobTitle\":null,\"id\":\"9f4fe8ea-7e6e-486e-b8f4-VkHdanfIomf\"}"
            + "},"
            +"{"
            +"\"id\": \"2\","
            +"\"status\":200,"
            + "\"headers\":{\"Cache-Control\":\"no-store, no-cache\",\"OData-Version\":\"4.0\",\"Content-Type\":\"application/json;odata.metadata=minimal;odata.streaming=true;IEEE754Compatible=false;charset=utf-8\"},"
            + "\"body\":{\"@odata.context\":\"https://graph.microsoft.com/v1.0/$metadata#drives/$entity\",\"createdDateTime\":\"2019-01-12T09:05:38Z\",\"description\":\"\",\"id\":\"b!random-VkHdanfIomf\",\"lastModifiedDateTime\":\"2019-03-06T06:59:04Z\",\"name\":\"OneDrive\",\"webUrl\":\"https://m365x751487-my.sharepoint.com/personal/admin_m365x751487_onmicrosoft_com/Documents\",\"driveType\":\"business\",\"createdBy\":{\"user\":{\"displayName\":\"System Account\"}},\"lastModifiedBy\":{\"user\":{\"displayName\":\"System Account\"}},\"owner\":{\"user\":{\"email\":\"admin@M365x751487.OnMicrosoft.com\",\"id\":\"6b4fa8ea-7e6e-486e-a8f4-d00a5b23488c\",\"displayName\":\"MOD Administrator\"}},\"quota\":{\"deleted\":0,\"remaining\":1099509670098,\"state\":\"normal\",\"total\":1099511627776,\"used\":30324}}"
            + "},"
            +"{"
            +"\"id\": \"3\","
            +"\"status\":201,"
            + "\"headers\":{\"Location\":\"https://graph.microsoft.com/v1.0/users/9f4fe8ea-7e6e-486e-a8f4-nothing-here/onenote/notebooks/1-zyz-a1c1-441a-8b41-9378jjdd2\",\"Preference-Applied\":\"odata.include-annotations=*\",\"Cache-Control\":\"no-cache\",\"OData-Version\":\"4.0\",\"Content-Type\":\"application/json;odata.metadata=minimal;odata.streaming=true;IEEE754Compatible=false;charset=utf-8\"},"
            + "\"body\":{\"@odata.context\":\"https://graph.microsoft.com/v1.0/$metadata#users('9f4fe8ea-7e6e-486e-a8f4-nothing-here')/onenote/notebooks/$entity\",\"id\":\"1-9f4fe8ea-7e6e-486e-a8f4-nothing-here\",\"self\":\"https://graph.microsoft.com/v1.0/users/9f4fe8ea-7e6e-486e-a8f4-nothing-here/onenote/notebooks/1-9f4fe8ea-7e6e-486e-a8f4-nothing-here\",\"createdDateTime\":\"2019-03-06T08:08:09Z\",\"displayName\":\"My Notebook -442293399\",\"lastModifiedDateTime\":\"2019-03-06T08:08:09Z\"}"
            + "}]}";
        ResponseBody body = ResponseBody.create(responseJSON, MediaType.get("application/json"));
        Response response = defaultBuilder.code(HttpURLConnection.HTTP_OK).body(body).build();
        BatchResponseContent batchResponseContent = new BatchResponseContent(response);

        Map<String, Response> responses = batchResponseContent.getResponses();

        assertNotNull(responses);
        assertEquals(3, responses.size());
        assertNotEquals("NoStore",responses.get("1").header("Cache-Control"));
        assertTrue(responses.get("2").header("Cache-Control").contains("no-cache"));
        assertTrue(responses.get("2").header("Cache-Control").contains("no-store"));
        assertEquals(HttpURLConnection.HTTP_CREATED, responses.get("3").code());
    }
    @Test
    void BatchResponseContent_GetResponseById() {
        String responseJSON = "{\"responses\":"
            + "[{"
            + "\"id\": \"1\","
            + "\"status\":200,"
            + "\"headers\":{\"Cache-Control\":\"no-cache\",\"OData-Version\":\"4.0\",\"Content-Type\":\"application/json;odata.metadata=minimal;odata.streaming=true;IEEE754Compatible=false;charset=utf-8\"},"
            + "\"body\":{\"@odata.context\":\"https://graph.microsoft.com/v1.0/$metadata#users/$entity\",\"displayName\":\"MOD Administrator\",\"jobTitle\":null,\"id\":\"9f4fe8ea-7e6e-486e-b8f4-VkHdanfIomf\"}"
            + "},"
            + "{"
            + "\"id\": \"2\","
            + "\"status\":409,"
            + "\"headers\" : {\"Cache-Control\":\"no-cache\"},"
            + "\"body\":{\"error\": {\"code\": \"20117\",\"message\": \"An item with this name already exists in this location.\",\"innerError\":{\"request-id\": \"nothing1b13-45cd-new-92be873c5781\",\"date\": \"2019-03-22T23:17:50\"}}}"
            + "},"
            + "{" +
            "\"id\": \"3\"," +
            "\"status\": 200," +
            "\"headers\": {" +
            "\"Cache-Control\": \"private\"," +
            "\"Content-Type\": \"image/jpeg\"," +
            "\"ETag\": \"BEB9D79C\"" +
            "}," +
            "\"body\": \"iVBORw0K\"" +
            "}" +
            "]}";
        ResponseBody body = ResponseBody.create(responseJSON, MediaType.parse("application/json"));
        Response response = defaultBuilder.code(HttpURLConnection.HTTP_OK).body(body).build();
        BatchResponseContent batchResponseContent = new BatchResponseContent(response);

        Response response2 = batchResponseContent.getResponseById("2");
        Response imageResponse = batchResponseContent.getResponseById("3");

        assertNotNull(response2);
        assertEquals(HttpURLConnection.HTTP_CONFLICT, response2.code());
        assertEquals("image/jpeg", imageResponse.header("Content-Type"));
        assertNull(batchResponseContent.getResponseById("4"));
    }
    @Test
    void BatchResponseContent_GetResponseStreamById() throws IOException {
        String responseJSON = "{"+
            "\"responses\": [" +
            "{" +
            "\"id\": \"1\"," +
            "\"status\": 200," +
            "\"headers\": {" +
            "\"Cache-Control\": \"private\"," +
            "\"Content-Type\": \"image/jpeg\"," +
            "\"ETag\": \"BEB9D79C\"" +
            "}," +
            "\"body\": \"iVBORw0KGgoAAAA\"" +
            "}" +
            "]" +
            "}";
        ResponseBody body = ResponseBody.create(responseJSON, MediaType.parse("application/json"));
        Response response = defaultBuilder.code(200).body(body).build();
        BatchResponseContent batchResponseContent = new BatchResponseContent(response);
        InputStream stream = batchResponseContent.getResponseStreamById("1");
        assertNotNull(stream);
        assertTrue(stream.available() > 0);
    }
    @Test
    void BatchResponseContent_GetResponseByIdWithDeserializer() {
        registry.contentTypeAssociatedFactories.put(CoreConstants.MimeTypeNames.APPLICATION_JSON, new JsonParseNodeFactory());
        String responseJSON = "{\"responses\":"
            + "[{"
            + "\"id\": \"1\","
            + "\"status\":200,"
            + "\"headers\":{\"Cache-Control\":\"no-cache\",\"OData-Version\":\"4.0\",\"Content-Type\":\"application/json;odata.metadata=minimal;odata.streaming=true;IEEE754Compatible=false;charset=utf-8\"},"
            + "\"body\":{\"@odata.context\":\"https://graph.microsoft.com/v1.0/$metadata#users/$entity\",\"displayName\":\"MOD Administrator\",\"jobTitle\":null,\"id\":\"9f4fe8ea-7e6e-486e-b8f4-VkHdanfIomf\"}"
            + "},"
            + "{"
            + "\"id\": \"2\","
            + "\"status\":200,"
            + "\"headers\":{\"Cache-Control\":\"no-store, no-cache\",\"OData-Version\":\"4.0\",\"Content-Type\":\"application/json;odata.metadata=minimal;odata.streaming=true;IEEE754Compatible=false;charset=utf-8\"},"
            + "\"body\":{\"@odata.context\":\"https://graph.microsoft.com/v1.0/$metadata#drives/$entity\",\"createdDateTime\":\"2019-01-12T09:05:38Z\",\"description\":\"\",\"id\":\"b!random-VkHdanfIomf\",\"lastModifiedDateTime\":\"2019-03-06T06:59:04Z\",\"name\":\"OneDrive\",\"webUrl\":\"https://m365x751487-my.sharepoint.com/personal/admin_m365x751487_onmicrosoft_com/Documents\",\"driveType\":\"business\",\"createdBy\":{\"user\":{\"displayName\":\"System Account\"}},\"lastModifiedBy\":{\"user\":{\"displayName\":\"System Account\"}},\"owner\":{\"user\":{\"email\":\"admin@M365x751487.OnMicrosoft.com\",\"id\":\"6b4fa8ea-7e6e-486e-a8f4-d00a5b23488c\",\"displayName\":\"MOD Administrator\"}},\"quota\":{\"deleted\":0,\"remaining\":1099509670098,\"state\":\"normal\",\"total\":1099511627776,\"used\":30324}}"
            + "},"
            + "{"
            + "\"id\": \"3\","
            + "\"status\":201,"
            + "\"headers\":{\"Location\":\"https://graph.microsoft.com/v1.0/users/9f4fe8ea-7e6e-486e-a8f4-nothing-here/onenote/notebooks/1-zyz-a1c1-441a-8b41-9378jjdd2\",\"Preference-Applied\":\"odata.include-annotations=*\",\"Cache-Control\":\"no-cache\",\"OData-Version\":\"4.0\",\"Content-Type\":\"application/json;odata.metadata=minimal;odata.streaming=true;IEEE754Compatible=false;charset=utf-8\"},"
            + "\"body\":{\"@odata.context\":\"https://graph.microsoft.com/v1.0/$metadata#users('9f4fe8ea-7e6e-486e-a8f4-nothing-here')/onenote/notebooks/$entity\",\"id\":\"1-9f4fe8ea-7e6e-486e-a8f4-nothing-here\",\"self\":\"https://graph.microsoft.com/v1.0/users/9f4fe8ea-7e6e-486e-a8f4-nothing-here/onenote/notebooks/1-9f4fe8ea-7e6e-486e-a8f4-nothing-here\",\"createdDateTime\":\"2019-03-06T08:08:09Z\",\"displayName\":\"My Notebook -442293399\",\"lastModifiedDateTime\":\"2019-03-06T08:08:09Z\"}"
            + "},"
            + "{"
            + "\"id\": \"4\","
            + "\"status\":409,"
            + "\"headers\" : {\"Cache-Control\":\"no-cache\"},"
            + "\"body\":{\"error\": {\"code\": \"20117\",\"message\": \"An item with this name already exists in this location.\",\"innerError\":{\"request-id\": \"nothing1b13-45cd-new-92be873c5781\",\"date\": \"2019-03-22T23:17:50\"}}}"
            + "}" +
            "]}";
        ResponseBody body = ResponseBody.create(responseJSON, MediaType.parse("application/json"));
        Response response = defaultBuilder.code(200).body(body).build();
        BatchResponseContent batchResponseContent = new BatchResponseContent(response);

        TestUser user = batchResponseContent.getResponseById("1", TestUser::createFromDiscriminatorValue);
        assertNotNull(user);
        assertEquals("MOD Administrator", user.getDisplayName());

        TestDrive drive = batchResponseContent.getResponseById("2", TestDrive::createFromDiscriminatorValue);
        assertNotNull(drive);
        assertEquals("OneDrive", drive.name);
        assertEquals("b!random-VkHdanfIomf", drive.id);

        TestNoteBook notebook = batchResponseContent.getResponseById("3", TestNoteBook::createFromDiscriminatorValue);
        assertNotNull(notebook);
        assertEquals("My Notebook -442293399", notebook.displayName);
        assertEquals("1-9f4fe8ea-7e6e-486e-a8f4-nothing-here", notebook.id);

        try{
           batchResponseContent.getResponseById("4", TestDriveItem::createFromDiscriminatorValue);
        } catch (Exception ex) {
            assertTrue(ex instanceof ApiException);
            ApiException apiException = (ApiException) ex;
            assertEquals(HttpURLConnection.HTTP_CONFLICT, apiException.getResponseStatusCode());
        }
        TestNoteBook nonExistingNotebook = batchResponseContent.getResponseById("5", TestNoteBook::createFromDiscriminatorValue);
        assertNull(nonExistingNotebook);
    }
    @Test
    void BatchResponseContent_GetResponseByIdWithDeserializerWorksWithDateTimeOffsets() {
        registry.contentTypeAssociatedFactories.put(CoreConstants.MimeTypeNames.APPLICATION_JSON, new JsonParseNodeFactory());
        String responseJSON = "{\n" +
            "    \"responses\": [\n" +
            "        {\n" +
            "            \"id\": \"3\",\n" +
            "            \"status\": 200,\n" +
            "            \"headers\": {\n" +
            "                \"Cache-Control\": \"private\",\n" +
            "                \"OData-Version\": \"4.0\",\n" +
            "                \"Content-Type\": \"application/json;odata.metadata=minimal;odata.streaming=true;IEEE754Compatible=false;charset=utf-8\",\n" +
            "                \"ETag\": \"W/\\\"h8TLt1Vki0W7hBZaqTqGTQAAQyxv+g==\\\"\"\n" +
            "            },\n" +
            "            \"body\": {\n" +
            "                \"@odata.context\": \"https://graph.microsoft.com/v1.0/$metadata#users('d9f7c4f6-e1bb-4032-a86d-6e84722b983d')/events/$entity\",\n" +
            "                \"@odata.etag\": \"W/\\\"h8TLt1Vki0W7hBZaqTqGTQAAQyxv+g==\\\"\",\n" +
            "                \"id\": \"AQMkADcyMWRhMWZmAC0xZTI1LTRjZjEtYTRjMC04M\",\n" +
            "                \"categories\": [],\n" +
            "                \"originalStartTimeZone\": \"Pacific Standard Time\",\n" +
            "                \"originalEndTimeZone\": \"Pacific Standard Time\",\n" +
            "                \"iCalUId\": \"040000008200E00074C5B7101A82E0080000000053373A40E03ED5010000000000000000100000007C41056410E97C44B2A34798E719B862\",\n" +
            "                \"reminderMinutesBeforeStart\": 15,\n" +
            "                \"type\": \"singleInstance\",\n" +
            "                \"webLink\": \"https://outlook.office365.com/owa/?itemid=AQMkADcyMWRhMWZmAC0xZTI1LTRjZjEtYTRjMC04MGY3OGEzNThiZDAARgAAA1AZwxLGN%2FJIv2Mj%2F0o8JqYHAIfEy7dVZItFu4QWWqk6hk0AAAIBDQAAAIfEy7dVZItFu4QWWqk6hk0AAAI4eQAAAA%3D%3D&exvsurl=1&path=/calendar/item\",\n" +
            "                \"onlineMeetingUrl\": null,\n" +
            "                \"recurrence\": null,\n" +
            "                \"responseStatus\": {\n" +
            "                    \"response\": \"notResponded\",\n" +
            "                    \"time\": \"0001-01-01T00:00:00Z\"\n" +
            "                },\n" +
            "                \"body\": {\n" +
            "                    \"contentType\": \"html\",\n" +
            "                    \"content\": \"<html>\\r\\n<head>\\r\\n<meta http-\"\n" +

            "                },\n" +
            "                \"start\": {\n" +
            "                    \"dateTime\": \"2019-07-30T22:00:00.0000000\",\n" +
            "                    \"timeZone\": \"UTC\"\n" +
            "                },\n" +
            "                \"end\": {\n" +
            "                    \"dateTime\": \"2019-07-30T23:00:00.0000000\",\n" +
            "                    \"timeZone\": \"UTC\"\n" +
            "                }" +
            "            }\n" +
            "        }\n" +
            "    ]\n" +
            "}";
        ResponseBody body = ResponseBody.create(responseJSON, MediaType.parse("application/json"));
        Response response = defaultBuilder.code(HttpURLConnection.HTTP_OK).body(body).build();
        BatchResponseContent batchResponseContent = new BatchResponseContent(response);

        TestEvent event = batchResponseContent.getResponseById("3", TestEvent::createFromDiscriminatorValue);
        assertEquals("2019-07-30T23:00:00.0000000", event.getEnd().getDateTime());
        assertEquals("2019-07-30T22:00:00.0000000", event.getStart().getDateTime());
        assertEquals("UTC", event.getEnd().getTimeZone());
    }
}
