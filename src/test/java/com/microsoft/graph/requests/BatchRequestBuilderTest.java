package com.microsoft.graph.requests;

import com.microsoft.graph.BaseClient;
import com.microsoft.graph.CoreConstants;
import com.microsoft.graph.content.BatchRequestContent;
import com.microsoft.graph.models.BatchRequestStep;
import com.microsoft.kiota.RequestInformation;
import com.microsoft.kiota.authentication.AnonymousAuthenticationProvider;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BatchRequestBuilderTest {

    @Test
    void BatchRequestBuilder_DefaultBuilderTest() {
        BaseClient client = new BaseClient(new AnonymousAuthenticationProvider(), "https://localhost");
        BatchRequestBuilder batchRequestBuilder = new BatchRequestBuilder(client.getRequestAdapter());

        Request request = new Request.Builder().url("https://graph.microsoft.com/v1.0/me/").build();
        RequestBody requestBody = RequestBody.create("{}", MediaType.get(CoreConstants.MimeTypeNames.APPLICATION_JSON));
        Request request2 = new Request.Builder().url("https://graph.microsoft.com/v1.0/me/onenote/notebooks").post(requestBody).build();
        BatchRequestStep batchRequestStep = new BatchRequestStep("1", request);
        BatchRequestStep batchRequestStep2 = new BatchRequestStep("2", request2, List.of("1"));

        BatchRequestContent batchRequestContent = new BatchRequestContent(client,List.of(batchRequestStep, batchRequestStep2));
        RequestInformation requestInformation = batchRequestBuilder.toPostRequestInformationAsync(batchRequestContent).join();

        assertEquals("{+baseurl}/$batch", requestInformation.urlTemplate);
        assertEquals(client.getRequestAdapter(), batchRequestBuilder.getRequestAdapter());

    }
}
