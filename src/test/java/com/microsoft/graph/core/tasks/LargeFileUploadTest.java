package com.microsoft.graph.core.tasks;

import com.microsoft.graph.core.requests.upload.UploadSliceRequestBuilder;
import com.microsoft.graph.core.testModels.TestDriveItem;
import com.microsoft.graph.core.models.UploadSession;
import com.microsoft.kiota.authentication.AuthenticationProvider;
import com.microsoft.kiota.http.OkHttpRequestAdapter;

import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.doReturn;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.mockito.ArgumentCaptor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;

import org.mockito.internal.matchers.Any;

import com.microsoft.graph.core.models.UploadResult;

class LargeFileUploadTest {

    final OkHttpRequestAdapter adapter = new OkHttpRequestAdapter(mock(AuthenticationProvider.class));

    @Test
    void ThrowsIllegalArgumentExceptionOnEmptyStream() throws IllegalAccessException, IOException {
        UploadSession session = new UploadSession();
        session.setNextExpectedRanges(Arrays.asList("0-"));
        session.setUploadUrl("http://localhost");
        session.setExpirationDateTime(OffsetDateTime.parse("2019-11-07T06:39:31.499Z"));

        InputStream stream = new ByteArrayInputStream(new byte[0]);
        int size = stream.available();
        long maxSliceSize = 200*1024;

        try {
            new LargeFileUploadTask<TestDriveItem>(adapter, session, stream, size, maxSliceSize, TestDriveItem::createFromDiscriminatorValue);
        } catch (IllegalArgumentException | InvocationTargetException | NoSuchMethodException ex ) {
            assertEquals("Must provide a stream that is not empty.", ex.getMessage());
        }
    }
    @Test
    void AllowsVariableSliceSize() throws NoSuchFieldException, IllegalAccessException, IOException, InvocationTargetException, NoSuchMethodException {
        UploadSession session = new UploadSession();
        session.setNextExpectedRanges(Arrays.asList("0-"));
        session.setUploadUrl("http://localhost");
        session.setExpirationDateTime(OffsetDateTime.parse("2019-11-07T06:39:31.499Z"));

        byte[] mockData = new byte[1000000];
        ByteArrayInputStream stream = new ByteArrayInputStream(mockData);
        int size = stream.available();
        int maxSliceSize = 200*1024; //200 kb slice size

        LargeFileUploadTask<TestDriveItem> task = new LargeFileUploadTask<TestDriveItem>(adapter, session, stream, size, maxSliceSize,TestDriveItem::createFromDiscriminatorValue);
        ArrayList<UploadSliceRequestBuilder<TestDriveItem>> builders = (ArrayList<UploadSliceRequestBuilder<TestDriveItem>>) task.getUploadSliceRequests();

        assertEquals(5, builders.size()); //We expect 5 slices for a 1,000,000 byte stream
        UploadSliceRequestBuilder slice = builders.get(0);
        assertEquals(0, slice.getRangeBegin());
        assertEquals(204799,slice.getRangeEnd());
        assertEquals(204800, slice.getRangeLength());
    }
    @Test
    void singleSliceTest() throws IOException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        UploadSession session = new UploadSession();
        session.setNextExpectedRanges(Arrays.asList("0-"));
        session.setUploadUrl("http://localhost");
        session.setExpirationDateTime(OffsetDateTime.parse("2019-11-07T06:39:31.499Z"));

        byte[]  mockData = new byte[100000];
        ByteArrayInputStream stream = new ByteArrayInputStream(mockData);
        int size = stream.available();

        LargeFileUploadTask<TestDriveItem> task = new LargeFileUploadTask<TestDriveItem>(adapter, session, stream, size, TestDriveItem::createFromDiscriminatorValue);
        ArrayList<UploadSliceRequestBuilder<TestDriveItem>> builders = (ArrayList<UploadSliceRequestBuilder<TestDriveItem>>) task.getUploadSliceRequests();

        assertEquals(1, builders.size());
        UploadSliceRequestBuilder onlySlice = builders.get(0);
        assertEquals(0, onlySlice.getRangeBegin());
        assertEquals(size-1, onlySlice.getRangeEnd());
        assertEquals(size, onlySlice.getRangeLength());
    }
    @Test
    void BreakStreamIntoCorrectRanges() throws IOException, NoSuchFieldException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        UploadSession session = new UploadSession();
        session.setNextExpectedRanges(Arrays.asList("0-"));
        session.setUploadUrl("http://localhost");
        session.setExpirationDateTime(OffsetDateTime.parse("2019-11-07T06:39:31.499Z"));

        byte[] mockData = new byte[1000000];
        ByteArrayInputStream stream = new ByteArrayInputStream(mockData);
        int size = stream.available();
        int maxSliceSize = 320*1024; //320 kb slice size

        LargeFileUploadTask<TestDriveItem> task = new LargeFileUploadTask<TestDriveItem>(adapter, session, stream, size, maxSliceSize, TestDriveItem::createFromDiscriminatorValue);
        ArrayList<UploadSliceRequestBuilder<TestDriveItem>> builders = (ArrayList<UploadSliceRequestBuilder<TestDriveItem>>) task.getUploadSliceRequests();

        assertEquals(4, builders.size());
        long currentRangeBegins = 0;
        for(UploadSliceRequestBuilder slice : builders) {
            assertEquals(size, slice.getTotalSessionLength());
            assertEquals(currentRangeBegins, slice.getRangeBegin());
            currentRangeBegins += maxSliceSize;
        }

        UploadSliceRequestBuilder lastSlice = builders.get(3);
        assertEquals(size%maxSliceSize, lastSlice.getRangeLength());
        assertEquals(size-1, lastSlice.getRangeEnd());
    }
    // Test for chunkInputStream method with a 5MB file
        @Test
    void uploads5MBFileSuccessfully() throws Exception {
        // Arrange
        UploadSession session = new UploadSession();
        session.setNextExpectedRanges(Arrays.asList("0-"));
        session.setUploadUrl("http://localhost");
        session.setExpirationDateTime(OffsetDateTime.now().plusHours(1));

        // 5MB file
        byte[] data = new byte[5 * 1024 * 1024];
            for (int i = 0; i < data.length; i++) {
        data[i] = (byte)(i % 256);
    }
        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        int size = stream.available();

    // Create a real task to get the real builder(s)
        LargeFileUploadTask<TestDriveItem> realTask = new LargeFileUploadTask<>(adapter, session, stream, size, TestDriveItem::createFromDiscriminatorValue);
        var realBuilders = realTask.getUploadSliceRequests();

        // Spy the builder(s) and mock put()
        ArrayList<UploadSliceRequestBuilder<TestDriveItem>> spyBuilders = new ArrayList<>();
        ArgumentCaptor<ByteArrayInputStream> captor = ArgumentCaptor.forClass(ByteArrayInputStream.class);

        for (UploadSliceRequestBuilder<TestDriveItem> builder : realBuilders) {
            UploadSliceRequestBuilder<TestDriveItem> spyBuilder = spy(builder);
            UploadResult<TestDriveItem> mockResult = new UploadResult<>();
            TestDriveItem item = new TestDriveItem();
            item.size = data.length;
            mockResult.itemResponse = item;
            doReturn(mockResult).when(spyBuilder).put(captor.capture());
            spyBuilders.add(spyBuilder);
        }

        // Subclass LargeFileUploadTask to inject our spy builders
        LargeFileUploadTask<TestDriveItem> task = new LargeFileUploadTask<>(adapter, session, stream, size, TestDriveItem::createFromDiscriminatorValue) {
            @Override
            protected java.util.List<UploadSliceRequestBuilder<TestDriveItem>> getUploadSliceRequests() {
                return spyBuilders;
            }
        };

        // Act
        task.upload(3, null);

        // Verify the chunkStream content
        ByteArrayInputStream capturedStream = captor.getValue();
        byte[] capturedBytes = new byte[data.length];
        int read = capturedStream.read(capturedBytes);
        assertEquals(data.length, read, "Should read all bytes from chunkStream");
        for (int i = 0; i < data.length; i++) {
            assertEquals(data[i], capturedBytes[i], "Byte at position " + i + " should match original data");
        }
    }
}
