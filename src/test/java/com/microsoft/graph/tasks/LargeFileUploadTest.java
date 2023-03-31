package com.microsoft.graph.tasks;

import com.microsoft.graph.models.UploadSession;
import com.microsoft.graph.requests.upload.UploadSliceRequestBuilder;
import com.microsoft.graph.testModels.TestDriveItem;
import com.microsoft.kiota.authentication.AuthenticationProvider;
import com.microsoft.kiota.http.OkHttpRequestAdapter;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.OffsetDateTime;
import java.util.ArrayList;

public class LargeFileUploadTest {

    final OkHttpRequestAdapter adapter = new OkHttpRequestAdapter(mock(AuthenticationProvider.class));

    @Test
    void ThrowsIllegalArgumentExceptionOnEmptyStream() throws NoSuchFieldException, IllegalAccessException, IOException {
        UploadSession session = new UploadSession();
        session.nextExpectedRanges.add("0-");
        session.uploadUrl = "http://localhost";
        session.expirationDateTime = OffsetDateTime.parse("2019-11-07T06:39:31.499Z");

        InputStream stream = InputStream.nullInputStream();
        int size = stream.available();
        long maxSliceSize = 200*1024;

        try{
            LargeFileUploadTask<TestDriveItem> task = new LargeFileUploadTask<TestDriveItem>(adapter, session, stream, size, maxSliceSize, TestDriveItem::createFromDiscriminatorValue);
        } catch (IllegalArgumentException ex ) {
            assertEquals("Must provide a stream that is not empty.", ex.getMessage());
        }
    }

    @Test
    void AllowsVariableSliceSize() throws NoSuchFieldException, IllegalAccessException, IOException {
        UploadSession session = new UploadSession();
        session.nextExpectedRanges.add("0-");
        session.uploadUrl = "http://localhost";
        session.expirationDateTime = OffsetDateTime.parse("2019-11-07T06:39:31.499Z");

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
    void singleSliceTest() throws IOException, NoSuchFieldException, IllegalAccessException {
        UploadSession session = new UploadSession();
        session.nextExpectedRanges.add("0-");
        session.uploadUrl = "http://localhost";
        session.expirationDateTime = OffsetDateTime.parse("2019-11-07T06:39:31.499Z");

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
    void BreakStreamIntoCorrectRanges() throws IOException, NoSuchFieldException, IllegalAccessException {
        UploadSession session = new UploadSession();
        session.nextExpectedRanges.add("0-");
        session.uploadUrl = "http://localhost";
        session.expirationDateTime = OffsetDateTime.parse("2019-11-07T06:39:31.499Z");

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

}
