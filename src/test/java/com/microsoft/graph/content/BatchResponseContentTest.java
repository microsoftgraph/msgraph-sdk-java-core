package com.microsoft.graph.content;

import com.microsoft.graph.CoreConstants;
import com.microsoft.graph.exceptions.ErrorConstants;
import com.microsoft.graph.exceptions.ServiceException;
import com.microsoft.graph.testModels.*;
import com.microsoft.kiota.serialization.JsonParseNodeFactory;
import com.microsoft.kiota.serialization.ParseNodeFactoryRegistry;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import okhttp3.*;
import org.junit.jupiter.api.Test;

import javax.print.attribute.standard.Media;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import static com.microsoft.kiota.serialization.ParseNodeFactoryRegistry.defaultInstance;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class BatchResponseContentTest {
    ParseNodeFactoryRegistry registry = defaultInstance;
    Response.Builder defaultBuilder = new Response.Builder().protocol(Protocol.HTTP_1_1).message("Message").request(mock(Request.class));

    @Test
    public void BatchResponseContent_InitializeWithNoContentAsync() {
        Response response = defaultBuilder.code(HttpURLConnection.HTTP_BAD_REQUEST).build();
        BatchResponseContent batchResponseContent = new BatchResponseContent(response);
        HashMap<String, Response> responses = batchResponseContent.getResponsesAsync().join();
        Response response1 = responses.get("1");
        assertNotNull(responses);
        assertNull(response1);
        assertEquals(0,responses.size());
    }
    @Test
    public void BatchResponseContent_InitializeWithEmptyResponseContentAsync() {
        String jsonResponse = "{ \"responses\": [] }";
        ResponseBody responseBody = ResponseBody.create(MediaType.get("application/json"), jsonResponse);
        Response response = defaultBuilder.code(HttpURLConnection.HTTP_BAD_REQUEST).body(responseBody).build();
        BatchResponseContent batchResponseContent = new BatchResponseContent(response);
        HashMap<String, Response> responses = batchResponseContent.getResponsesAsync().join();
        Response response1 = batchResponseContent.getResponseByIdAsync("1").join();
        assertNotNull(responses);
        assertNull(response1);
        assertEquals(0,responses.size());
    }
    @Test
    @SuppressFBWarnings
    public void BatchResponseContent_InitializeWithNullResponseMessage() {
        try{
            new BatchResponseContent(null);
        } catch (NullPointerException ex) {
            assertEquals(String.format(Locale.US, ErrorConstants.Messages.NULL_PARAMETER, "batchResponse"), ex.getMessage());
        }
    }
    @Test
    public void BatchResponseContent_GetResponsesAsync() {
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
        ResponseBody body = ResponseBody.create(MediaType.get("application/json"), responseJSON);
        Response response = defaultBuilder.code(HttpURLConnection.HTTP_OK).body(body).build();
        BatchResponseContent batchResponseContent = new BatchResponseContent(response);

        HashMap<String, Response> responses = batchResponseContent.getResponsesAsync().join();

        assertNotNull(responses);
        assertEquals(3, responses.size());
        assertNotEquals("NoStore",responses.get("1").header("Cache-Control"));
        assertTrue(responses.get("2").header("Cache-Control").contains("no-cache"));
        assertTrue(responses.get("2").header("Cache-Control").contains("no-store"));
        assertEquals(HttpURLConnection.HTTP_CREATED, responses.get("3").code());
    }
    @Test
    public void BatchResponseContent_GetResponseByIdAsync() {
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
            "\"body\": \"iVBORw0KGgoAAAANSUhEUgAAABkAAAAZCAYAAADE6YVjAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZ" +
            "SBJbWFnZVJlYWR5ccllPAAAAyJpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADw/eHBhY2tldCBiZWdpbj0i77" +
            "u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+IDx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM" +
            "6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDUuMy1jMDExIDY2LjE0NTY2MSwgMjAxMi8wMi8wNi0x" +
            "NDo1NjoyNyAgICAgICAgIj4gPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyL" +
            "zIyLXJkZi1zeW50YXgtbnMjIj4gPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIgeG1sbnM6eG1wPSJodH" +
            "RwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvIiB4bWxuczp4bXBNTT0iaHR0cDovL25zLmFkb2JlLmNvbS94YXA" +
            "vMS4wL21tLyIgeG1sbnM6c3RSZWY9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9zVHlwZS9SZXNvdXJj" +
            "ZVJlZiMiIHhtcDpDcmVhdG9yVG9vbD0iQWRvYmUgUGhvdG9zaG9wIENTNiAoV2luZG93cykiIHhtcE1NOkluc" +
            "3RhbmNlSUQ9InhtcC5paWQ6MEVBMTczNDg3QzA5MTFFNjk3ODM5NjQyRjE2RjA3QTkiIHhtcE1NOkRvY3VtZW" +
            "50SUQ9InhtcC5kaWQ6MEVBMTczNDk3QzA5MTFFNjk3ODM5NjQyRjE2RjA3QTkiPiA8eG1wTU06RGVyaXZlZEZ" +
            "yb20gc3RSZWY6aW5zdGFuY2VJRD0ieG1wLmlpZDowRUExNzM0NjdDMDkxMUU2OTc4Mzk2NDJGMTZGMDdBOSIg" +
            "c3RSZWY6ZG9jdW1lbnRJRD0ieG1wLmRpZDowRUExNzM0NzdDMDkxMUU2OTc4Mzk2NDJGMTZGMDdBOSIvPiA8L" +
            "3JkZjpEZXNjcmlwdGlvbj4gPC9yZGY6UkRGPiA8L3g6eG1wbWV0YT4gPD94cGFja2V0IGVuZD0iciI/PjjUms" +
            "sAAAGASURBVHjatJaxTsMwEIbpIzDA6FaMMPYJkDKzVYU+QFeEGPIKfYU8AETkCYI6wANkZQwIKRNDB1hA0Jr" +
            "f0rk6WXZ8BvWkb4kv99vn89kDrfVexBSYgVNwDA7AN+jAK3gEd+AlGMGIBFDgFvzouK3JV/lihQTOwLtOtw9w" +
            "IRG5pJn91Tbgqk9kSk7GViADrTD4HCyZ0NQnomi51sb0fUyCMQEbp2WpU67IjfNjwcYyoUDhjJVcZBjYBy40j" +
            "4wXgaobWoe8Z6Y80CJBwFpunepIzt2AUgFjtXXshNXjVmMh+K+zzp/CMs0CqeuzrxSRpbOKfdCkiMTS1VBQ41" +
            "uxMyQR2qbrXiiwYN3ACh1FDmsdK2Eu4J6Tlo31dYVtCY88h5ELZIJJ+IRMzBHfyJINrigNkt5VsRiub9nXICd" +
            "sYyVd2NcVvA3ScE5t2rb5JuEeyZnAhmLt9NK63vX1O5Pe8XaPSuGq1uTrfUgMEp9EJ+CQvr+BJ/AAKvAcCiAR" +
            "+bf9CjAAluzmdX4AEIIAAAAASUVORK5CYII=\"" +
            "}" +
            "]}";
        ResponseBody body = ResponseBody.create(MediaType.parse("application/json"), responseJSON);
        Response response = defaultBuilder.code(HttpURLConnection.HTTP_OK).body(body).build();
        BatchResponseContent batchResponseContent = new BatchResponseContent(response);

        Response response2 = batchResponseContent.getResponseByIdAsync("2").join();
        Response imageResponse = batchResponseContent.getResponseByIdAsync("3").join();

        assertNotNull(response2);
        assertEquals(HttpURLConnection.HTTP_CONFLICT, response2.code());
        assertEquals("image/jpeg", imageResponse.header("Content-Type"));
        assertNull(batchResponseContent.getResponseByIdAsync("4").join());
    }
    @Test
    public void BatchResponseContent_GetResponseStreamByIdAsync() throws IOException {
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
            "\"body\": \"iVBORw0KGgoAAAANSUhEUgAAABkAAAAZCAYAAADE6YVjAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZ" +
            "SBJbWFnZVJlYWR5ccllPAAAAyJpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADw/eHBhY2tldCBiZWdpbj0i77" +
            "u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+IDx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM" +
            "6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDUuMy1jMDExIDY2LjE0NTY2MSwgMjAxMi8wMi8wNi0x" +
            "NDo1NjoyNyAgICAgICAgIj4gPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyL" +
            "zIyLXJkZi1zeW50YXgtbnMjIj4gPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIgeG1sbnM6eG1wPSJodH" +
            "RwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvIiB4bWxuczp4bXBNTT0iaHR0cDovL25zLmFkb2JlLmNvbS94YXA" +
            "vMS4wL21tLyIgeG1sbnM6c3RSZWY9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9zVHlwZS9SZXNvdXJj" +
            "ZVJlZiMiIHhtcDpDcmVhdG9yVG9vbD0iQWRvYmUgUGhvdG9zaG9wIENTNiAoV2luZG93cykiIHhtcE1NOkluc" +
            "3RhbmNlSUQ9InhtcC5paWQ6MEVBMTczNDg3QzA5MTFFNjk3ODM5NjQyRjE2RjA3QTkiIHhtcE1NOkRvY3VtZW" +
            "50SUQ9InhtcC5kaWQ6MEVBMTczNDk3QzA5MTFFNjk3ODM5NjQyRjE2RjA3QTkiPiA8eG1wTU06RGVyaXZlZEZ" +
            "yb20gc3RSZWY6aW5zdGFuY2VJRD0ieG1wLmlpZDowRUExNzM0NjdDMDkxMUU2OTc4Mzk2NDJGMTZGMDdBOSIg" +
            "c3RSZWY6ZG9jdW1lbnRJRD0ieG1wLmRpZDowRUExNzM0NzdDMDkxMUU2OTc4Mzk2NDJGMTZGMDdBOSIvPiA8L" +
            "3JkZjpEZXNjcmlwdGlvbj4gPC9yZGY6UkRGPiA8L3g6eG1wbWV0YT4gPD94cGFja2V0IGVuZD0iciI/PjjUms" +
            "sAAAGASURBVHjatJaxTsMwEIbpIzDA6FaMMPYJkDKzVYU+QFeEGPIKfYU8AETkCYI6wANkZQwIKRNDB1hA0Jr" +
            "f0rk6WXZ8BvWkb4kv99vn89kDrfVexBSYgVNwDA7AN+jAK3gEd+AlGMGIBFDgFvzouK3JV/lihQTOwLtOtw9w" +
            "IRG5pJn91Tbgqk9kSk7GViADrTD4HCyZ0NQnomi51sb0fUyCMQEbp2WpU67IjfNjwcYyoUDhjJVcZBjYBy40j" +
            "4wXgaobWoe8Z6Y80CJBwFpunepIzt2AUgFjtXXshNXjVmMh+K+zzp/CMs0CqeuzrxSRpbOKfdCkiMTS1VBQ41" +
            "uxMyQR2qbrXiiwYN3ACh1FDmsdK2Eu4J6Tlo31dYVtCY88h5ELZIJJ+IRMzBHfyJINrigNkt5VsRiub9nXICd" +
            "sYyVd2NcVvA3ScE5t2rb5JuEeyZnAhmLt9NK63vX1O5Pe8XaPSuGq1uTrfUgMEp9EJ+CQvr+BJ/AAKvAcCiAR" +
            "+bf9CjAAluzmdX4AEIIAAAAASUVORK5CYII=\"" +
            "}" +
            "]" +
            "}";
        ResponseBody body = ResponseBody.create(MediaType.parse("application/json"), responseJSON);
        Response response = defaultBuilder.code(200).body(body).build();
        BatchResponseContent batchResponseContent = new BatchResponseContent(response);
        InputStream stream = batchResponseContent.getResponseStreamByIdAsync("1").join();
        assertNotNull(stream);
        assertTrue(stream.available() > 0);
    }
    @Test
    public void BatchResponseContent_GetResponseByIdAsyncWithDeserializer() {
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
        ResponseBody body = ResponseBody.create(MediaType.parse("application/json"), responseJSON);
        Response response = defaultBuilder.code(200).body(body).build();
        BatchResponseContent batchResponseContent = new BatchResponseContent(response);

        TestUser user = batchResponseContent.getResponseByIdAsync("1", TestUser::createFromDiscriminatorValue).join();
        assertNotNull(user);
        assertEquals("MOD Administrator", user.getDisplayName());

        TestDrive drive = batchResponseContent.getResponseByIdAsync("2", TestDrive::createFromDiscriminatorValue).join();
        assertNotNull(drive);
        assertEquals("OneDrive", drive.name);
        assertEquals("b!random-VkHdanfIomf", drive.id);

        TestNoteBook notebook = batchResponseContent.getResponseByIdAsync("3", TestNoteBook::createFromDiscriminatorValue).join();
        assertNotNull(notebook);
        assertEquals("My Notebook -442293399", notebook.displayName);
        assertEquals("1-9f4fe8ea-7e6e-486e-a8f4-nothing-here", notebook.id);

        try{
           batchResponseContent.getResponseByIdAsync("4", TestDriveItem::createFromDiscriminatorValue).join();
        } catch (Exception ex) {
            assertTrue(ex.getCause() instanceof ServiceException);
            ServiceException serviceException = (ServiceException) ex.getCause();
            assertEquals(HttpURLConnection.HTTP_CONFLICT, serviceException.responseStatusCode);
            assertNotNull(serviceException.getRawResponseBody());
        }
        TestNoteBook nonExistingNotebook = batchResponseContent.getResponseByIdAsync("5", TestNoteBook::createFromDiscriminatorValue).join();
        assertNull(nonExistingNotebook);
    }
    @Test
    public void BatchResponseContent_GetResponseByIdAsyncWithDeserializerWorksWithDateTimeOffsets() {
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
        ResponseBody body = ResponseBody.create(MediaType.parse("application/json"), responseJSON);
        Response response = defaultBuilder.code(HttpURLConnection.HTTP_OK).body(body).build();
        BatchResponseContent batchResponseContent = new BatchResponseContent(response);

        TestEvent event = batchResponseContent.getResponseByIdAsync("3", TestEvent::createFromDiscriminatorValue).join();
        assertEquals("2019-07-30T23:00:00.0000000", event.getEnd().getDateTime());
        assertEquals("2019-07-30T22:00:00.0000000", event.getStart().getDateTime());
        assertEquals("UTC", event.getEnd().getTimeZone());
    }




}
