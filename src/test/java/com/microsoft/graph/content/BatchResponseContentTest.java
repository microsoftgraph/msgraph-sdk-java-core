package com.microsoft.graph.content;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.util.Iterator;

import com.google.gson.JsonElement;
import com.microsoft.graph.http.GraphErrorResponse;
import com.microsoft.graph.http.GraphServiceException;
import com.microsoft.graph.logger.DefaultLogger;
import com.microsoft.graph.logger.ILogger;
import com.microsoft.graph.logger.LoggerLevel;
import com.microsoft.graph.serializer.DefaultSerializer;
import com.microsoft.graph.serializer.ISerializer;

import org.junit.jupiter.api.Test;

public class BatchResponseContentTest {
    @Test
    public void testValidBatchResponseContent() {
        String responsebody = "{\"responses\": [{\"id\": \"1\",\"status\":200,\"headers\" : {\"Cache-Control\":\"no-cache\",\"OData-Version\":\"4.0\",\"Content-Type\":\"application/json;odata.metadata=minimal;odata.streaming=true;IEEE754Compatible=false;charset=utf-8\"},\"body\":{\"@odata.context\":\"https://graph.microsoft.com/v1.0/$metadata#users/$entity\",\"businessPhones\":[\"8006427676\"],\"displayName\":\"MOD Administrator\",\"givenName\":\"MOD\",\"jobTitle\":null,\"mail\":\"admin@M365x751487.OnMicrosoft.com\",\"mobilePhone\":\"425-882-1032\",\"officeLocation\":null,\"preferredLanguage\":\"en-US\",\"surname\":\"Administrator\",\"userPrincipalName\":\"admin@M365x751487.onmicrosoft.com\",\"id\":\"6b4fa8ea-7e6e-486e-a8f4-d00a5b23488c\"}},{\"id\": \"2\",\"status\":200,\"headers\" : {\"Cache-Control\":\"no-store, no-cache\",\"OData-Version\":\"4.0\",\"Content-Type\":\"application/json;odata.metadata=minimal;odata.streaming=true;IEEE754Compatible=false;charset=utf-8\"},\"body\":{\"@odata.context\":\"https://graph.microsoft.com/v1.0/$metadata#drives/$entity\",\"createdDateTime\":\"2019-01-12T09:05:38Z\",\"description\":\"\",\"id\":\"b!nlu9o5I9g0y8gsHXfUM_bPTZ0oM_wVNArHM5R4-VkHLlnxx5SpqHRJledwfICP9f\",\"lastModifiedDateTime\":\"2019-03-06T06:59:04Z\",\"name\":\"OneDrive\",\"webUrl\":\"https://m365x751487-my.sharepoint.com/personal/admin_m365x751487_onmicrosoft_com/Documents\",\"driveType\":\"business\",\"createdBy\":{\"user\":{\"displayName\":\"System Account\"}},\"lastModifiedBy\":{\"user\":{\"displayName\":\"System Account\"}},\"owner\":{\"user\":{\"email\":\"admin@M365x751487.OnMicrosoft.com\",\"id\":\"6b4fa8ea-7e6e-486e-a8f4-d00a5b23488c\",\"displayName\":\"MOD Administrator\"}},\"quota\":{\"deleted\":0,\"remaining\":1099509670098,\"state\":\"normal\",\"total\":1099511627776,\"used\":30324}}},{\"id\": \"3\",\"status\":201,\"headers\" : {\"Location\":\"https://graph.microsoft.com/v1.0/users/6b4fa8ea-7e6e-486e-a8f4-d00a5b23488c/onenote/notebooks/1-94e4376a-a1c1-441a-8b41-af5c86ee39d0\",\"Preference-Applied\":\"odata.include-annotations=*\",\"Cache-Control\":\"no-cache\",\"OData-Version\":\"4.0\",\"Content-Type\":\"application/json;odata.metadata=minimal;odata.streaming=true;IEEE754Compatible=false;charset=utf-8\"},\"body\":{\"@odata.context\":\"https://graph.microsoft.com/v1.0/$metadata#users('6b4fa8ea-7e6e-486e-a8f4-d00a5b23488c')/onenote/notebooks/$entity\",\"id\":\"1-94e4376a-a1c1-441a-8b41-af5c86ee39d0\",\"self\":\"https://graph.microsoft.com/v1.0/users/6b4fa8ea-7e6e-486e-a8f4-d00a5b23488c/onenote/notebooks/1-94e4376a-a1c1-441a-8b41-af5c86ee39d0\",\"createdDateTime\":\"2019-03-06T08:08:09Z\",\"displayName\":\"My Notebook -442293399\",\"lastModifiedDateTime\":\"2019-03-06T08:08:09Z\",\"isDefault\":false,\"userRole\":\"Owner\",\"isShared\":false,\"sectionsUrl\":\"https://graph.microsoft.com/v1.0/users/6b4fa8ea-7e6e-486e-a8f4-d00a5b23488c/onenote/notebooks/1-94e4376a-a1c1-441a-8b41-af5c86ee39d0/sections\",\"sectionGroupsUrl\":\"https://graph.microsoft.com/v1.0/users/6b4fa8ea-7e6e-486e-a8f4-d00a5b23488c/onenote/notebooks/1-94e4376a-a1c1-441a-8b41-af5c86ee39d0/sectionGroups\",\"createdBy\":{\"user\":{\"id\":\"6b4fa8ea-7e6e-486e-a8f4-d00a5b23488c\",\"displayName\":\"MOD Administrator\"}},\"lastModifiedBy\":{\"user\":{\"id\":\"6b4fa8ea-7e6e-486e-a8f4-d00a5b23488c\",\"displayName\":\"MOD Administrator\"}},\"links\":{\"oneNoteClientUrl\":{\"href\":\"onenote:https://m365x751487-my.sharepoint.com/personal/admin_m365x751487_onmicrosoft_com/Documents/Notebooks/My%20Notebook%20-442293399\"},\"oneNoteWebUrl\":{\"href\":\"https://m365x751487-my.sharepoint.com/personal/admin_m365x751487_onmicrosoft_com/Documents/Notebooks/My%20Notebook%20-442293399\"}}}}]}";
        BatchResponseContent batchresponse = new DefaultSerializer(mock(ILogger.class)).deserializeObject(responsebody, BatchResponseContent.class);
        assertTrue(batchresponse.responses != null);
    }

    @Test
    public void testInvalidBatchResponseContentWithEmptyResponse() {
        String responsebody = "{\"responses\": [] }";
        BatchResponseContent batchresponse = new DefaultSerializer(mock(ILogger.class)).deserializeObject(responsebody, BatchResponseContent.class);
        assertTrue(batchresponse.getResponseById("1") == null);
    }

    @Test
    public void testInvalidBatchResponseContentWithMalformedResponse() {
        String invalidResponsebody = "{responses: [] }";
        BatchResponseContent batchresponse = new DefaultSerializer(mock(ILogger.class)).deserializeObject(invalidResponsebody, BatchResponseContent.class);
        assertTrue(batchresponse.responses.isEmpty());
    }

    @Test
    public void testGetBatchResponseContentByID() throws IOException {
        String responsebody = "{\"responses\": [{\"id\": \"1\",\"status\":200,\"headers\" : {\"Cache-Control\":\"no-cache\",\"OData-Version\":\"4.0\",\"Content-Type\":\"application/json;odata.metadata=minimal;odata.streaming=true;IEEE754Compatible=false;charset=utf-8\"},\"body\":{\"@odata.context\":\"https://graph.microsoft.com/v1.0/$metadata#users/$entity\",\"businessPhones\":[\"8006427676\"],\"displayName\":\"MOD Administrator\",\"givenName\":\"MOD\",\"jobTitle\":null,\"mail\":\"admin@M365x751487.OnMicrosoft.com\",\"mobilePhone\":\"425-882-1032\",\"officeLocation\":null,\"preferredLanguage\":\"en-US\",\"surname\":\"Administrator\",\"userPrincipalName\":\"admin@M365x751487.onmicrosoft.com\",\"id\":\"6b4fa8ea-7e6e-486e-a8f4-d00a5b23488c\"}},{\"id\": \"2\",\"status\":200,\"headers\" : {\"Cache-Control\":\"no-store, no-cache\",\"OData-Version\":\"4.0\",\"Content-Type\":\"application/json;odata.metadata=minimal;odata.streaming=true;IEEE754Compatible=false;charset=utf-8\"},\"body\":{\"@odata.context\":\"https://graph.microsoft.com/v1.0/$metadata#drives/$entity\",\"createdDateTime\":\"2019-01-12T09:05:38Z\",\"description\":\"\",\"id\":\"b!nlu9o5I9g0y8gsHXfUM_bPTZ0oM_wVNArHM5R4-VkHLlnxx5SpqHRJledwfICP9f\",\"lastModifiedDateTime\":\"2019-03-06T06:59:04Z\",\"name\":\"OneDrive\",\"webUrl\":\"https://m365x751487-my.sharepoint.com/personal/admin_m365x751487_onmicrosoft_com/Documents\",\"driveType\":\"business\",\"createdBy\":{\"user\":{\"displayName\":\"System Account\"}},\"lastModifiedBy\":{\"user\":{\"displayName\":\"System Account\"}},\"owner\":{\"user\":{\"email\":\"admin@M365x751487.OnMicrosoft.com\",\"id\":\"6b4fa8ea-7e6e-486e-a8f4-d00a5b23488c\",\"displayName\":\"MOD Administrator\"}},\"quota\":{\"deleted\":0,\"remaining\":1099509670098,\"state\":\"normal\",\"total\":1099511627776,\"used\":30324}}},{\"id\": \"3\",\"status\":201,\"headers\" : {\"Location\":\"https://graph.microsoft.com/v1.0/users/6b4fa8ea-7e6e-486e-a8f4-d00a5b23488c/onenote/notebooks/1-94e4376a-a1c1-441a-8b41-af5c86ee39d0\",\"Preference-Applied\":\"odata.include-annotations=*\",\"Cache-Control\":\"no-cache\",\"OData-Version\":\"4.0\",\"Content-Type\":\"application/json;odata.metadata=minimal;odata.streaming=true;IEEE754Compatible=false;charset=utf-8\"},\"body\":{\"@odata.context\":\"https://graph.microsoft.com/v1.0/$metadata#users('6b4fa8ea-7e6e-486e-a8f4-d00a5b23488c')/onenote/notebooks/$entity\",\"id\":\"1-94e4376a-a1c1-441a-8b41-af5c86ee39d0\",\"self\":\"https://graph.microsoft.com/v1.0/users/6b4fa8ea-7e6e-486e-a8f4-d00a5b23488c/onenote/notebooks/1-94e4376a-a1c1-441a-8b41-af5c86ee39d0\",\"createdDateTime\":\"2019-03-06T08:08:09Z\",\"displayName\":\"My Notebook -442293399\",\"lastModifiedDateTime\":\"2019-03-06T08:08:09Z\",\"isDefault\":false,\"userRole\":\"Owner\",\"isShared\":false,\"sectionsUrl\":\"https://graph.microsoft.com/v1.0/users/6b4fa8ea-7e6e-486e-a8f4-d00a5b23488c/onenote/notebooks/1-94e4376a-a1c1-441a-8b41-af5c86ee39d0/sections\",\"sectionGroupsUrl\":\"https://graph.microsoft.com/v1.0/users/6b4fa8ea-7e6e-486e-a8f4-d00a5b23488c/onenote/notebooks/1-94e4376a-a1c1-441a-8b41-af5c86ee39d0/sectionGroups\",\"createdBy\":{\"user\":{\"id\":\"6b4fa8ea-7e6e-486e-a8f4-d00a5b23488c\",\"displayName\":\"MOD Administrator\"}},\"lastModifiedBy\":{\"user\":{\"id\":\"6b4fa8ea-7e6e-486e-a8f4-d00a5b23488c\",\"displayName\":\"MOD Administrator\"}},\"links\":{\"oneNoteClientUrl\":{\"href\":\"onenote:https://m365x751487-my.sharepoint.com/personal/admin_m365x751487_onmicrosoft_com/Documents/Notebooks/My%20Notebook%20-442293399\"},\"oneNoteWebUrl\":{\"href\":\"https://m365x751487-my.sharepoint.com/personal/admin_m365x751487_onmicrosoft_com/Documents/Notebooks/My%20Notebook%20-442293399\"}}}}]}";
        BatchResponseContent batchresponse = new DefaultSerializer(mock(ILogger.class)).deserializeObject(responsebody, BatchResponseContent.class);
        BatchResponseStep<?> response = batchresponse.getResponseById("1");
        assertTrue(response != null);
    }

    @Test
    public void testGetBatchResponseContentIteratorOverResponse() throws IOException {
        String responsebody = "{\"responses\": [{\"id\": \"1\",\"status\":200,\"headers\" : {\"Cache-Control\":\"no-cache\",\"OData-Version\":\"4.0\",\"Content-Type\":\"application/json;odata.metadata=minimal;odata.streaming=true;IEEE754Compatible=false;charset=utf-8\"},\"body\":{\"@odata.context\":\"https://graph.microsoft.com/v1.0/$metadata#users/$entity\",\"businessPhones\":[\"8006427676\"],\"displayName\":\"MOD Administrator\",\"givenName\":\"MOD\",\"jobTitle\":null,\"mail\":\"admin@M365x751487.OnMicrosoft.com\",\"mobilePhone\":\"425-882-1032\",\"officeLocation\":null,\"preferredLanguage\":\"en-US\",\"surname\":\"Administrator\",\"userPrincipalName\":\"admin@M365x751487.onmicrosoft.com\",\"id\":\"6b4fa8ea-7e6e-486e-a8f4-d00a5b23488c\"}},{\"id\": \"2\",\"status\":200,\"headers\" : {\"Cache-Control\":\"no-store, no-cache\",\"OData-Version\":\"4.0\",\"Content-Type\":\"application/json;odata.metadata=minimal;odata.streaming=true;IEEE754Compatible=false;charset=utf-8\"},\"body\":{\"@odata.context\":\"https://graph.microsoft.com/v1.0/$metadata#drives/$entity\",\"createdDateTime\":\"2019-01-12T09:05:38Z\",\"description\":\"\",\"id\":\"b!nlu9o5I9g0y8gsHXfUM_bPTZ0oM_wVNArHM5R4-VkHLlnxx5SpqHRJledwfICP9f\",\"lastModifiedDateTime\":\"2019-03-06T06:59:04Z\",\"name\":\"OneDrive\",\"webUrl\":\"https://m365x751487-my.sharepoint.com/personal/admin_m365x751487_onmicrosoft_com/Documents\",\"driveType\":\"business\",\"createdBy\":{\"user\":{\"displayName\":\"System Account\"}},\"lastModifiedBy\":{\"user\":{\"displayName\":\"System Account\"}},\"owner\":{\"user\":{\"email\":\"admin@M365x751487.OnMicrosoft.com\",\"id\":\"6b4fa8ea-7e6e-486e-a8f4-d00a5b23488c\",\"displayName\":\"MOD Administrator\"}},\"quota\":{\"deleted\":0,\"remaining\":1099509670098,\"state\":\"normal\",\"total\":1099511627776,\"used\":30324}}},{\"id\": \"3\",\"status\":201,\"headers\" : {\"Location\":\"https://graph.microsoft.com/v1.0/users/6b4fa8ea-7e6e-486e-a8f4-d00a5b23488c/onenote/notebooks/1-94e4376a-a1c1-441a-8b41-af5c86ee39d0\",\"Preference-Applied\":\"odata.include-annotations=*\",\"Cache-Control\":\"no-cache\",\"OData-Version\":\"4.0\",\"Content-Type\":\"application/json;odata.metadata=minimal;odata.streaming=true;IEEE754Compatible=false;charset=utf-8\"},\"body\":{\"@odata.context\":\"https://graph.microsoft.com/v1.0/$metadata#users('6b4fa8ea-7e6e-486e-a8f4-d00a5b23488c')/onenote/notebooks/$entity\",\"id\":\"1-94e4376a-a1c1-441a-8b41-af5c86ee39d0\",\"self\":\"https://graph.microsoft.com/v1.0/users/6b4fa8ea-7e6e-486e-a8f4-d00a5b23488c/onenote/notebooks/1-94e4376a-a1c1-441a-8b41-af5c86ee39d0\",\"createdDateTime\":\"2019-03-06T08:08:09Z\",\"displayName\":\"My Notebook -442293399\",\"lastModifiedDateTime\":\"2019-03-06T08:08:09Z\",\"isDefault\":false,\"userRole\":\"Owner\",\"isShared\":false,\"sectionsUrl\":\"https://graph.microsoft.com/v1.0/users/6b4fa8ea-7e6e-486e-a8f4-d00a5b23488c/onenote/notebooks/1-94e4376a-a1c1-441a-8b41-af5c86ee39d0/sections\",\"sectionGroupsUrl\":\"https://graph.microsoft.com/v1.0/users/6b4fa8ea-7e6e-486e-a8f4-d00a5b23488c/onenote/notebooks/1-94e4376a-a1c1-441a-8b41-af5c86ee39d0/sectionGroups\",\"createdBy\":{\"user\":{\"id\":\"6b4fa8ea-7e6e-486e-a8f4-d00a5b23488c\",\"displayName\":\"MOD Administrator\"}},\"lastModifiedBy\":{\"user\":{\"id\":\"6b4fa8ea-7e6e-486e-a8f4-d00a5b23488c\",\"displayName\":\"MOD Administrator\"}},\"links\":{\"oneNoteClientUrl\":{\"href\":\"onenote:https://m365x751487-my.sharepoint.com/personal/admin_m365x751487_onmicrosoft_com/Documents/Notebooks/My%20Notebook%20-442293399\"},\"oneNoteWebUrl\":{\"href\":\"https://m365x751487-my.sharepoint.com/personal/admin_m365x751487_onmicrosoft_com/Documents/Notebooks/My%20Notebook%20-442293399\"}}}}]}";
        BatchResponseContent batchresponse = new DefaultSerializer(mock(ILogger.class)).deserializeObject(responsebody, BatchResponseContent.class);
        Iterator<BatchResponseStep<JsonElement>> it = batchresponse.responses.iterator();
        while(it.hasNext()) {
            BatchResponseStep<?> entry = it.next();
            assertTrue(entry.id != null && entry.body !=null);
        }
    }

    @Test
    public void testGetBatchResponseContentMapResponse() throws IOException {
        String responsebody = "{\"responses\": [{\"id\": \"1\",\"status\":200,\"headers\" : {\"Cache-Control\":\"no-cache\",\"OData-Version\":\"4.0\",\"Content-Type\":\"application/json;odata.metadata=minimal;odata.streaming=true;IEEE754Compatible=false;charset=utf-8\"},\"body\":{\"@odata.context\":\"https://graph.microsoft.com/v1.0/$metadata#users/$entity\",\"businessPhones\":[\"8006427676\"],\"displayName\":\"MOD Administrator\",\"givenName\":\"MOD\",\"jobTitle\":null,\"mail\":\"admin@M365x751487.OnMicrosoft.com\",\"mobilePhone\":\"425-882-1032\",\"officeLocation\":null,\"preferredLanguage\":\"en-US\",\"surname\":\"Administrator\",\"userPrincipalName\":\"admin@M365x751487.onmicrosoft.com\",\"id\":\"6b4fa8ea-7e6e-486e-a8f4-d00a5b23488c\"}},{\"id\": \"2\",\"status\":200,\"headers\" : {\"Cache-Control\":\"no-store, no-cache\",\"OData-Version\":\"4.0\",\"Content-Type\":\"application/json;odata.metadata=minimal;odata.streaming=true;IEEE754Compatible=false;charset=utf-8\"},\"body\":{\"@odata.context\":\"https://graph.microsoft.com/v1.0/$metadata#drives/$entity\",\"createdDateTime\":\"2019-01-12T09:05:38Z\",\"description\":\"\",\"id\":\"b!nlu9o5I9g0y8gsHXfUM_bPTZ0oM_wVNArHM5R4-VkHLlnxx5SpqHRJledwfICP9f\",\"lastModifiedDateTime\":\"2019-03-06T06:59:04Z\",\"name\":\"OneDrive\",\"webUrl\":\"https://m365x751487-my.sharepoint.com/personal/admin_m365x751487_onmicrosoft_com/Documents\",\"driveType\":\"business\",\"createdBy\":{\"user\":{\"displayName\":\"System Account\"}},\"lastModifiedBy\":{\"user\":{\"displayName\":\"System Account\"}},\"owner\":{\"user\":{\"email\":\"admin@M365x751487.OnMicrosoft.com\",\"id\":\"6b4fa8ea-7e6e-486e-a8f4-d00a5b23488c\",\"displayName\":\"MOD Administrator\"}},\"quota\":{\"deleted\":0,\"remaining\":1099509670098,\"state\":\"normal\",\"total\":1099511627776,\"used\":30324}}},{\"id\": \"3\",\"status\":201,\"headers\" : {\"Location\":\"https://graph.microsoft.com/v1.0/users/6b4fa8ea-7e6e-486e-a8f4-d00a5b23488c/onenote/notebooks/1-94e4376a-a1c1-441a-8b41-af5c86ee39d0\",\"Preference-Applied\":\"odata.include-annotations=*\",\"Cache-Control\":\"no-cache\",\"OData-Version\":\"4.0\",\"Content-Type\":\"application/json;odata.metadata=minimal;odata.streaming=true;IEEE754Compatible=false;charset=utf-8\"},\"body\":{\"@odata.context\":\"https://graph.microsoft.com/v1.0/$metadata#users('6b4fa8ea-7e6e-486e-a8f4-d00a5b23488c')/onenote/notebooks/$entity\",\"id\":\"1-94e4376a-a1c1-441a-8b41-af5c86ee39d0\",\"self\":\"https://graph.microsoft.com/v1.0/users/6b4fa8ea-7e6e-486e-a8f4-d00a5b23488c/onenote/notebooks/1-94e4376a-a1c1-441a-8b41-af5c86ee39d0\",\"createdDateTime\":\"2019-03-06T08:08:09Z\",\"displayName\":\"My Notebook -442293399\",\"lastModifiedDateTime\":\"2019-03-06T08:08:09Z\",\"isDefault\":false,\"userRole\":\"Owner\",\"isShared\":false,\"sectionsUrl\":\"https://graph.microsoft.com/v1.0/users/6b4fa8ea-7e6e-486e-a8f4-d00a5b23488c/onenote/notebooks/1-94e4376a-a1c1-441a-8b41-af5c86ee39d0/sections\",\"sectionGroupsUrl\":\"https://graph.microsoft.com/v1.0/users/6b4fa8ea-7e6e-486e-a8f4-d00a5b23488c/onenote/notebooks/1-94e4376a-a1c1-441a-8b41-af5c86ee39d0/sectionGroups\",\"createdBy\":{\"user\":{\"id\":\"6b4fa8ea-7e6e-486e-a8f4-d00a5b23488c\",\"displayName\":\"MOD Administrator\"}},\"lastModifiedBy\":{\"user\":{\"id\":\"6b4fa8ea-7e6e-486e-a8f4-d00a5b23488c\",\"displayName\":\"MOD Administrator\"}},\"links\":{\"oneNoteClientUrl\":{\"href\":\"onenote:https://m365x751487-my.sharepoint.com/personal/admin_m365x751487_onmicrosoft_com/Documents/Notebooks/My%20Notebook%20-442293399\"},\"oneNoteWebUrl\":{\"href\":\"https://m365x751487-my.sharepoint.com/personal/admin_m365x751487_onmicrosoft_com/Documents/Notebooks/My%20Notebook%20-442293399\"}}}}]}";
        BatchResponseContent batchresponse = new DefaultSerializer(mock(ILogger.class)).deserializeObject(responsebody, BatchResponseContent.class);
        for(BatchResponseStep<?> entry: batchresponse.responses) {
            assertTrue(entry.id != null && entry.body != null);
        }
    }

    @Test
    public void testGetBatchResponseContentSize() throws IOException {
        String responsebody = "{\"responses\": [{\"id\": \"1\",\"status\":200,\"headers\" : {\"Cache-Control\":\"no-cache\",\"OData-Version\":\"4.0\",\"Content-Type\":\"application/json;odata.metadata=minimal;odata.streaming=true;IEEE754Compatible=false;charset=utf-8\"},\"body\":{\"@odata.context\":\"https://graph.microsoft.com/v1.0/$metadata#users/$entity\",\"businessPhones\":[\"8006427676\"],\"displayName\":\"MOD Administrator\",\"givenName\":\"MOD\",\"jobTitle\":null,\"mail\":\"admin@M365x751487.OnMicrosoft.com\",\"mobilePhone\":\"425-882-1032\",\"officeLocation\":null,\"preferredLanguage\":\"en-US\",\"surname\":\"Administrator\",\"userPrincipalName\":\"admin@M365x751487.onmicrosoft.com\",\"id\":\"6b4fa8ea-7e6e-486e-a8f4-d00a5b23488c\"}},{\"id\": \"2\",\"status\":200,\"headers\" : {\"Cache-Control\":\"no-store, no-cache\",\"OData-Version\":\"4.0\",\"Content-Type\":\"application/json;odata.metadata=minimal;odata.streaming=true;IEEE754Compatible=false;charset=utf-8\"},\"body\":{\"@odata.context\":\"https://graph.microsoft.com/v1.0/$metadata#drives/$entity\",\"createdDateTime\":\"2019-01-12T09:05:38Z\",\"description\":\"\",\"id\":\"b!nlu9o5I9g0y8gsHXfUM_bPTZ0oM_wVNArHM5R4-VkHLlnxx5SpqHRJledwfICP9f\",\"lastModifiedDateTime\":\"2019-03-06T06:59:04Z\",\"name\":\"OneDrive\",\"webUrl\":\"https://m365x751487-my.sharepoint.com/personal/admin_m365x751487_onmicrosoft_com/Documents\",\"driveType\":\"business\",\"createdBy\":{\"user\":{\"displayName\":\"System Account\"}},\"lastModifiedBy\":{\"user\":{\"displayName\":\"System Account\"}},\"owner\":{\"user\":{\"email\":\"admin@M365x751487.OnMicrosoft.com\",\"id\":\"6b4fa8ea-7e6e-486e-a8f4-d00a5b23488c\",\"displayName\":\"MOD Administrator\"}},\"quota\":{\"deleted\":0,\"remaining\":1099509670098,\"state\":\"normal\",\"total\":1099511627776,\"used\":30324}}},{\"id\": \"3\",\"status\":201,\"headers\" : {\"Location\":\"https://graph.microsoft.com/v1.0/users/6b4fa8ea-7e6e-486e-a8f4-d00a5b23488c/onenote/notebooks/1-94e4376a-a1c1-441a-8b41-af5c86ee39d0\",\"Preference-Applied\":\"odata.include-annotations=*\",\"Cache-Control\":\"no-cache\",\"OData-Version\":\"4.0\",\"Content-Type\":\"application/json;odata.metadata=minimal;odata.streaming=true;IEEE754Compatible=false;charset=utf-8\"},\"body\":{\"@odata.context\":\"https://graph.microsoft.com/v1.0/$metadata#users('6b4fa8ea-7e6e-486e-a8f4-d00a5b23488c')/onenote/notebooks/$entity\",\"id\":\"1-94e4376a-a1c1-441a-8b41-af5c86ee39d0\",\"self\":\"https://graph.microsoft.com/v1.0/users/6b4fa8ea-7e6e-486e-a8f4-d00a5b23488c/onenote/notebooks/1-94e4376a-a1c1-441a-8b41-af5c86ee39d0\",\"createdDateTime\":\"2019-03-06T08:08:09Z\",\"displayName\":\"My Notebook -442293399\",\"lastModifiedDateTime\":\"2019-03-06T08:08:09Z\",\"isDefault\":false,\"userRole\":\"Owner\",\"isShared\":false,\"sectionsUrl\":\"https://graph.microsoft.com/v1.0/users/6b4fa8ea-7e6e-486e-a8f4-d00a5b23488c/onenote/notebooks/1-94e4376a-a1c1-441a-8b41-af5c86ee39d0/sections\",\"sectionGroupsUrl\":\"https://graph.microsoft.com/v1.0/users/6b4fa8ea-7e6e-486e-a8f4-d00a5b23488c/onenote/notebooks/1-94e4376a-a1c1-441a-8b41-af5c86ee39d0/sectionGroups\",\"createdBy\":{\"user\":{\"id\":\"6b4fa8ea-7e6e-486e-a8f4-d00a5b23488c\",\"displayName\":\"MOD Administrator\"}},\"lastModifiedBy\":{\"user\":{\"id\":\"6b4fa8ea-7e6e-486e-a8f4-d00a5b23488c\",\"displayName\":\"MOD Administrator\"}},\"links\":{\"oneNoteClientUrl\":{\"href\":\"onenote:https://m365x751487-my.sharepoint.com/personal/admin_m365x751487_onmicrosoft_com/Documents/Notebooks/My%20Notebook%20-442293399\"},\"oneNoteWebUrl\":{\"href\":\"https://m365x751487-my.sharepoint.com/personal/admin_m365x751487_onmicrosoft_com/Documents/Notebooks/My%20Notebook%20-442293399\"}}}}]}";
        BatchResponseContent batchresponse = new DefaultSerializer(mock(ILogger.class)).deserializeObject(responsebody, BatchResponseContent.class);
        assertTrue(batchresponse.responses.size() == 3);
    }
    @Test
    public void responseParsing() {
        final BatchResponseContent batchResponse = new DefaultSerializer(mock(ILogger.class)).deserializeObject("{\"responses\": [{\"id\": \"1\",\"status\": 200,\"body\": null}]}", BatchResponseContent.class);
        assertEquals(1, batchResponse.responses.size());
        final BatchResponseStep<?> response = batchResponse.getResponseById("1");
        assertNotNull(response);
        assertNull(batchResponse.getResponseById("2"));
    }
    @Test
    public void getResponseWithIdReturnsNullOnEmptyResponses()
    {
        final BatchResponseContent batchResponse = new BatchResponseContent();
        assertNull(batchResponse.getResponseById("id"));
    }
    @Test
    public void deserializesErrorsProperly() {
        String responsebody = "{\"responses\":[{\"id\":\"1\",\"status\":400,\"headers\":{\"Cache-Control\":\"no-cache\",\"x-ms-resource-unit\":\"1\",\"Content-Type\":\"application/json\"},\"body\":{\"error\":{\"code\":\"Request_BadRequest\",\"message\":\"Avalueisrequiredforproperty'displayName'ofresource'User'.\",\"innerError\":{\"date\":\"2021-02-02T19:19:38\",\"request-id\":\"408b8e64-4047-4c97-95b6-46e9f212ab48\",\"client-request-id\":\"102910da-260c-3028-0fb3-7d6903a02622\"}}}}]}";
        ISerializer serializer = new DefaultSerializer(mock(ILogger.class));
        BatchResponseContent batchresponse = serializer.deserializeObject(responsebody, BatchResponseContent.class);
        if(batchresponse != null) {
            if(batchresponse.responses != null)  // this is done by the batch request in the fluent API
                for(final BatchResponseStep<?> step : batchresponse.responses) {
                    step.serializer = serializer;
                }
            assertThrows(GraphServiceException.class, () -> {
                batchresponse.getResponseById("1").getDeserializedBody(BatchRequestContent.class);
            });
            try {
                batchresponse.getResponseById("1").getDeserializedBody(BatchRequestContent.class);
            } catch(GraphServiceException ex) {
                final GraphErrorResponse response = ex.getError();
                assertNotNull(response);
                assertNotNull(response.error);
                assertNotNull(response.error.message);
            }
        } else
            fail("batch response was null");
    }
    @Test
    public void includeVerboseInformation() {
        String responsebody = "{\"responses\":[{\"id\":\"1\",\"status\":400,\"headers\":{\"Cache-Control\":\"no-cache\",\"x-ms-resource-unit\":\"1\",\"Content-Type\":\"application/json\"},\"body\":{\"error\":{\"code\":\"Request_BadRequest\",\"message\":\"Avalueisrequiredforproperty'displayName'ofresource'User'.\",\"innerError\":{\"date\":\"2021-02-02T19:19:38\",\"request-id\":\"408b8e64-4047-4c97-95b6-46e9f212ab48\",\"client-request-id\":\"102910da-260c-3028-0fb3-7d6903a02622\"}}}}]}";
        ISerializer serializer = new DefaultSerializer(new DefaultLogger() {{
            setLoggingLevel(LoggerLevel.DEBUG);
        }});
        BatchResponseContent batchresponse = serializer.deserializeObject(responsebody, BatchResponseContent.class);
        if(batchresponse != null) {
            if(batchresponse.responses != null)  // this is done by the batch request in the fluent API
                for(final BatchResponseStep<?> step : batchresponse.responses) {
                    step.serializer = serializer;
                }
            try {
                batchresponse.getResponseById("1").getDeserializedBody(BatchRequestContent.class);
            } catch(GraphServiceException ex) {
                final GraphErrorResponse response = ex.getError();
                assertNotNull(response);
                assertNotNull(response.error);
                assertNotNull(response.error.message);
                assertEquals(ex.getMessage(true), ex.getMessage());
            }
        } else
            fail("batch response was null");
    }
    @Test
    public void doesNotIncludeVerboseInformation() {
        String responsebody = "{\"responses\":[{\"id\":\"1\",\"status\":400,\"headers\":{\"Cache-Control\":\"no-cache\",\"x-ms-resource-unit\":\"1\",\"Content-Type\":\"application/json\"},\"body\":{\"error\":{\"code\":\"Request_BadRequest\",\"message\":\"Avalueisrequiredforproperty'displayName'ofresource'User'.\",\"innerError\":{\"date\":\"2021-02-02T19:19:38\",\"request-id\":\"408b8e64-4047-4c97-95b6-46e9f212ab48\",\"client-request-id\":\"102910da-260c-3028-0fb3-7d6903a02622\"}}}}]}";
        ISerializer serializer = new DefaultSerializer(new DefaultLogger() {{
            setLoggingLevel(LoggerLevel.ERROR);
        }});
        BatchResponseContent batchresponse = serializer.deserializeObject(responsebody, BatchResponseContent.class);
        if(batchresponse != null) {
            if(batchresponse.responses != null)  // this is done by the batch request in the fluent API
                for(final BatchResponseStep<?> step : batchresponse.responses) {
                    step.serializer = serializer;
                }
            try {
                batchresponse.getResponseById("1").getDeserializedBody(BatchRequestContent.class);
            } catch(GraphServiceException ex) {
                final GraphErrorResponse response = ex.getError();
                assertNotNull(response);
                assertNotNull(response.error);
                assertNotNull(response.error.message);
                assertEquals(ex.getMessage(false), ex.getMessage());
            }
        } else
            fail("batch response was null");
    }
}
