package com.microsoft.graph.requests.middleware;

import com.microsoft.graph.requests.GraphClientFactory;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static com.microsoft.graph.CoreConstants.ReplacementConstants.ME_ENDPOINT;
import static com.microsoft.graph.CoreConstants.ReplacementConstants.USERS_ENDPOINT_WITH_REPLACE_TOKEN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UrlReplaceHandlerTest {

    private final static String defaultUsersWithTokenUrl = "https://graph.microsoft.com/v1.0"+ USERS_ENDPOINT_WITH_REPLACE_TOKEN;
    @Test
    void testUrlReplaceHandler_default_url() throws IOException {
        final OkHttpClient client = GraphClientFactory.create().build();
        final Request request = new Request.Builder().url(defaultUsersWithTokenUrl).build();
        final Response response =  client.newCall(request).execute();
        final String expectedNewUrl = "https://graph.microsoft.com/v1.0"+ ME_ENDPOINT;

        assertNotNull(response);
        assertEquals(response.request().url().toString(),expectedNewUrl);
    }




}
