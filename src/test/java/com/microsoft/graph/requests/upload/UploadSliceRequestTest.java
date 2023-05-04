package com.microsoft.graph.requests.upload;

import com.microsoft.graph.CoreConstants;
import com.microsoft.graph.models.UploadResult;
import com.microsoft.graph.models.UploadSession;
import com.microsoft.graph.testModels.TestDriveItem;
import com.microsoft.kiota.authentication.AnonymousAuthenticationProvider;
import com.microsoft.kiota.http.OkHttpRequestAdapter;
import com.microsoft.kiota.serialization.JsonParseNodeFactory;
import com.microsoft.kiota.serialization.ParsableFactory;
import com.microsoft.kiota.serialization.ParseNodeFactoryRegistry;
import okhttp3.*;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.time.OffsetDateTime;
import java.util.concurrent.ExecutionException;

import static com.microsoft.kiota.serialization.ParseNodeFactoryRegistry.defaultInstance;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UploadSliceRequestTest {
    ParseNodeFactoryRegistry registry = defaultInstance;

    @Test
    void PutAsyncReturnsExpectedUploadSessionAsync() throws ExecutionException, InterruptedException, IOException {
        registry.contentTypeAssociatedFactories.put(CoreConstants.MimeTypeNames.APPLICATION_JSON, new JsonParseNodeFactory());
        ParsableFactory<TestDriveItem> factory = TestDriveItem::createFromDiscriminatorValue;
        ResponseBody body = ResponseBody.create(
            "{\n" +
                "   \"expirationDateTime\": \"2015-01-29T09:21:55.523Z\",\n" +
                "   \"nextExpectedRanges\": [\n" +
                "   \"12345-55232\",\n" +
                "   \"77829-99375\"\n" +
                "   ]" +
                "}", MediaType.parse(CoreConstants.MimeTypeNames.APPLICATION_JSON));
        Response response = new Response.Builder()
            .request(new Request.Builder().post(mock(RequestBody.class)).url("https://a.b.c/").build())
            .protocol(Protocol.HTTP_1_1)
            .message("OK")
            .body(body)
            .code(HttpURLConnection.HTTP_OK)
            .build();

        OkHttpClient mockClient = getMockClient(response);
        final OkHttpRequestAdapter adapter =
            new OkHttpRequestAdapter(new AnonymousAuthenticationProvider(), null, null,mockClient);

        byte[] mockData = new byte[500];
        ByteArrayInputStream stream = new ByteArrayInputStream(mockData);

        UploadSliceRequestBuilder<TestDriveItem> sliceRequestBuilder = new UploadSliceRequestBuilder<>(
            "https://a.b.c/", adapter, 0, 200 , 1000, factory);

        UploadResult<TestDriveItem> result = sliceRequestBuilder.putAsync(stream).get();
        UploadSession session = (UploadSession) result.uploadSession;

        assertFalse(result.isUploadSuccessful());
        assertNotNull(session);
        assertTrue(session.getUploadUrl().isEmpty());
        assertEquals(OffsetDateTime.parse("2015-01-29T09:21:55.523Z"), session.getExpirationDateTime());
        assertEquals("12345-55232", session.getNextExpectedRanges().get(0));
        assertEquals("77829-99375", session.getNextExpectedRanges().get(1));
        assertEquals(2, session.getNextExpectedRanges().size());
    }

    public static OkHttpClient getMockClient(final Response response) throws IOException {
        final OkHttpClient mockClient = mock(OkHttpClient.class);
        final Call remoteCall = mock(Call.class);
        final Dispatcher dispatcher = new Dispatcher();
        when(remoteCall.execute()).thenReturn(response);
        doAnswer((Answer<Void>) invocation -> {
            Callback callback = invocation.getArgument(0);
            callback.onResponse(null, response);
            return null;
        }).when(remoteCall).enqueue(any(Callback.class));
        when(mockClient.dispatcher()).thenReturn(dispatcher);
        when(mockClient.newCall(any())).thenReturn(remoteCall);
        return mockClient;
    }

}
