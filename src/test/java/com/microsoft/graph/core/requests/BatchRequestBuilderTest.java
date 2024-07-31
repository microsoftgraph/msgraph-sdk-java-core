package com.microsoft.graph.core.requests;

import com.microsoft.graph.core.BaseClient;
import com.microsoft.graph.core.CoreConstants;
import com.microsoft.graph.core.content.BatchRequestContent;
import com.microsoft.graph.core.models.BatchRequestStep;
import com.microsoft.kiota.HttpMethod;
import com.microsoft.kiota.RequestInformation;
import com.microsoft.kiota.authentication.AnonymousAuthenticationProvider;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.ByteArrayInputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class BatchRequestBuilderTest {

    @Test
    void BatchRequestBuilder_DefaultBuilderTest() throws IOException {
        BaseClient client = new BaseClient(new AnonymousAuthenticationProvider(), "https://localhost");
        BatchRequestBuilder batchRequestBuilder = new BatchRequestBuilder(client.getRequestAdapter());

        Request request = new Request.Builder().url("https://graph.microsoft.com/v1.0/me/").build();
        RequestBody requestBody = RequestBody.create("{}", MediaType.get(CoreConstants.MimeTypeNames.APPLICATION_JSON));
        Request request2 = new Request.Builder().url("https://graph.microsoft.com/v1.0/me/onenote/notebooks").post(requestBody).build();
        BatchRequestStep batchRequestStep = new BatchRequestStep("1", request);
        BatchRequestStep batchRequestStep2 = new BatchRequestStep("2", request2, Arrays.asList("1"));

        BatchRequestContent batchRequestContent = new BatchRequestContent(client,Arrays.asList(batchRequestStep, batchRequestStep2));
        RequestInformation requestInformation = batchRequestBuilder.toPostRequestInformation(batchRequestContent);

        assertEquals("{+baseurl}/$batch", requestInformation.urlTemplate);
        assertEquals(client.getRequestAdapter(), batchRequestBuilder.getRequestAdapter());

    }
    @Test
    void BatchContentDoesNotDeadlockOnLargeContent() throws IOException {
        final BaseClient client = new BaseClient(new AnonymousAuthenticationProvider(), "https://localhost");
        final BatchRequestContent batchRequestContent = new BatchRequestContent(client);
        final List<InputStream> streamsToClose = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            final RequestInformation requestInformation = new RequestInformation();
            requestInformation.httpMethod = HttpMethod.POST;
            requestInformation.setUri(URI.create("https://graph.microsoft.com/v1.0/me/"));
            final String payload = "{\"displayName\": \"Test\", \"lastName\": \"User\", \"mailNickname\": \"testuser\", \"userPrincipalName\": \"testUser\", \"passwordProfile\": {\"forceChangePasswordNextSignIn\": true, \"password\": \"password\"}, \"accountEnabled\": true}";
            final InputStream content = new ByteArrayInputStream(payload.getBytes(StandardCharsets.UTF_8));
            streamsToClose.add(content);
            requestInformation.setStreamContent(content, CoreConstants.MimeTypeNames.APPLICATION_JSON);
            batchRequestContent.addBatchRequestStep(requestInformation);
        }
        BatchRequestBuilder batchRequestBuilder = new BatchRequestBuilder(client.getRequestAdapter());
        RequestInformation requestInformation = batchRequestBuilder.toPostRequestInformation(batchRequestContent);
        assertNotNull(requestInformation);
        for (final InputStream inputStream : streamsToClose) {
            inputStream.close();
        }
    }
}
