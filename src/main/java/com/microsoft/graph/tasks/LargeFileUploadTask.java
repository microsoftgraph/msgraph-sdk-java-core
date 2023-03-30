package com.microsoft.graph.tasks;

import com.microsoft.graph.models.IUploadSession;
import com.microsoft.graph.models.UploadResult;
import com.microsoft.graph.models.UploadSession;
import com.microsoft.graph.requests.BaseGraphRequestAdapter;
import com.microsoft.graph.requests.FeatureFlag;
import com.microsoft.graph.requests.GraphClientFactory;
import com.microsoft.graph.requests.GraphClientOption;
import com.microsoft.graph.requests.upload.UploadSessionRequestBuilder;
import com.microsoft.graph.requests.upload.UploadSliceRequestBuilder;
import com.microsoft.kiota.RequestAdapter;
import com.microsoft.kiota.authentication.AnonymousAuthenticationProvider;
import com.microsoft.kiota.serialization.Parsable;
import com.microsoft.kiota.serialization.ParsableFactory;
import okhttp3.OkHttpClient;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class LargeFileUploadTask<T extends Parsable > {

    private final long defaultMaxSliceSize = 5*1024*1024;
    private IUploadSession uploadSession;
    private RequestAdapter requestAdapter;
    private InputStream uploadStream;
    private long maxSliceSize;
    private ArrayList<AbstractMap.SimpleEntry<Long, Long>> rangesRemaining;
    private long TotalUploadLength;
    private ParsableFactory<T> factory;


    public LargeFileUploadTask(@Nonnull IUploadSession uploadSession, @Nonnull InputStream uploadStream, @Nullable long maxSliceSize, @Nullable RequestAdapter requestAdapter, ParsableFactory<T> factory) throws NoSuchFieldException, IllegalAccessException {
        Objects.requireNonNull(uploadSession);
        Objects.requireNonNull(uploadStream);
        Objects.requireNonNull(factory);
        this.factory = factory;
        this.uploadSession = extractSessionFromParsable(uploadSession);
        this.requestAdapter = Objects.isNull(requestAdapter) ? initializeAdapter(uploadSession.getUploadUrl()):requestAdapter;
        this.uploadStream = uploadStream;
        this.rangesRemaining = getRangesRemaining(uploadSession);
        this.maxSliceSize = Objects.isNull(maxSliceSize) ? defaultMaxSliceSize : maxSliceSize;
    }

    public CompletableFuture<UploadResult<T>> uploadSliceAsync(UploadSliceRequestBuilder<T> uploadSliceRequestBuilder) {
        boolean firstAttempt = true;
        byte[] buffer = chunkInputStream(uploadStream,(int) uploadSliceRequestBuilder.getRangeBegin(), uploadSliceRequestBuilder.getRangeLength());
        ByteArrayInputStream chunkStream = new ByteArrayInputStream(buffer);
        return uploadSliceRequestBuilder.putAsync(chunkStream);
    }
    public CompletableFuture<UploadResult<T>> uploadAsync(@Nullable int maxTries) throws ExecutionException, InterruptedException {
        int _maxTries = (Objects.isNull(maxTries)) ? 3 : maxTries;
        int uploadTries = 0;
        while (uploadTries < _maxTries) {
            List<UploadSliceRequestBuilder<T>> uploadSliceRequestBuilders = getUploadSliceRequests();
            for (UploadSliceRequestBuilder<T> request : uploadSliceRequestBuilders ) {
                UploadResult<T> result = uploadSliceAsync(request).get();
                if(result.uploadSucceeded()) {
                    return CompletableFuture.completedFuture(result);
                }
            }
            updateSessionStatusAsync().get();
            uploadTries +=1;
            if(uploadTries < _maxTries) {
                TimeUnit.SECONDS.sleep((long) 2*uploadTries*uploadTries);
            }
        }
        throw new CancellationException();
    }

    public UploadSession extractSessionFromParsable(Parsable uploadSession) throws IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
        if (!uploadSession.getFieldDeserializers().containsKey("expirationDateTime"))
            throw new IllegalArgumentException("The Parsable does not contain the 'expirationDateTime' property");
        if (!uploadSession.getFieldDeserializers().containsKey("nextExpectedRanges"))
            throw new IllegalArgumentException("The Parsable does not contain the 'nextExpectedRanges' property");
        if (!uploadSession.getFieldDeserializers().containsKey("uploadUrl"))
            throw new IllegalArgumentException("The Parsable does not contain the 'uploadUrl' property");
        return new UploadSession(){{
            setExpirationDateTime((OffsetDateTime) uploadSession.getClass().getDeclaredField("expirationDateTime").get(this));
            setUploadUrl((String) uploadSession.getClass().getDeclaredField("uploadUrl").get(this));
            setNextExpectedRanges((List<String>) uploadSession.getClass().getDeclaredField("nextExpectedRanges").get(this));
        }};
    };

    public RequestAdapter initializeAdapter(String uploadUrl) {
        OkHttpClient client = GraphClientFactory.create(new GraphClientOption() {{
            this.featureTracker.setFeatureUsage(FeatureFlag.FILE_UPLOAD_FLAG);
        }}).build();
        return new BaseGraphRequestAdapter(new AnonymousAuthenticationProvider(), uploadUrl, client);
    }

    private List<UploadSliceRequestBuilder<T>> getUploadSliceRequests() {
        ArrayList<UploadSliceRequestBuilder<T>> builders = new ArrayList<UploadSliceRequestBuilder<T>>();
        for (Map.Entry entry: rangesRemaining) {
            long currentRangeBegin = (long) entry.getKey();
            long currentEnd = (long) entry.getValue();
            while(currentRangeBegin < currentEnd) {
                long nextSliceSize = nextSliceSize(currentRangeBegin, currentEnd);
                UploadSliceRequestBuilder sliceRequestBuilder =
                    new UploadSliceRequestBuilder(this.uploadSession.getUploadUrl(), this.requestAdapter, this.factory,
                        currentRangeBegin, currentRangeBegin + nextSliceSize -1, this.TotalUploadLength);
                builders.add(sliceRequestBuilder);
                currentRangeBegin += nextSliceSize;
            }
        }
        return builders;
    }

    public CompletableFuture<UploadResult<T>> resumeAsync(@Nullable int maxTries) throws Exception {
        int _maxTries = (Objects.isNull(maxTries)) ? 3 : maxTries;
        IUploadSession session;
        try{
            session = updateSessionStatusAsync().get();
        } catch (ExecutionException ex) {
            return new CompletableFuture<UploadResult<T>>() {{
                this.completeExceptionally(ex);
            }};
        } catch (InterruptedException ex) {
            return new CompletableFuture<UploadResult<T>>() {{
                this.completeExceptionally(ex);
            }};
        }
        OffsetDateTime expirationDateTime =
            Objects.isNull(session.getExpirationDateTime()) ? OffsetDateTime.now() : session.getExpirationDateTime();
        if(expirationDateTime.isBefore(OffsetDateTime.now()) || expirationDateTime.isEqual(OffsetDateTime.now())) {
            throw new Exception();
            //TODO: make this a client exception
        }
        return this.uploadAsync(_maxTries);
    }

    public CompletableFuture<Void> deleteSessionAsync() throws Exception {
        OffsetDateTime expirationDateTime =
            Objects.isNull(this.uploadSession.getExpirationDateTime()) ? OffsetDateTime.now() : this.uploadSession.getExpirationDateTime();
        if(expirationDateTime.isBefore(OffsetDateTime.now()) || expirationDateTime.isEqual(OffsetDateTime.now())) {
            throw new Exception();
            //TODO: make this a client exception
        }
        UploadSessionRequestBuilder<T> builder = new UploadSessionRequestBuilder<T>(this.uploadSession, this.requestAdapter, this.factory);
        return builder.deleteAsync();
    }

    public CompletableFuture<IUploadSession> updateSessionStatusAsync() {
        UploadSessionRequestBuilder<T> sessionRequestBuilder = new UploadSessionRequestBuilder<T>(this.uploadSession, this.requestAdapter, this.factory);
        return sessionRequestBuilder.getAsync().thenApply(x ->
        {
            this.rangesRemaining = getRangesRemaining(x);
            x.setUploadUrl(this.uploadSession.getUploadUrl());
            this.uploadSession = x;
            return x;
        });
    }

    private ArrayList<AbstractMap.SimpleEntry<Long, Long>> getRangesRemaining(IUploadSession uploadSession) {
        ArrayList<AbstractMap.SimpleEntry<Long, Long>> remaining = new ArrayList<AbstractMap.SimpleEntry<Long, Long>>();
        for (String range:uploadSession.getNextExpectedRanges()) {
            String[] specifiers = range.split("-");
            remaining.add(new AbstractMap.SimpleEntry<Long, Long>(Long.valueOf(specifiers[0]),
                specifiers.length == 2 ? Long.valueOf(specifiers[1]) : this.TotalUploadLength-1));
        }
        return remaining;
    }

    private long nextSliceSize(long rangeBegin, long rangeEnd) {
        long size = rangeEnd - rangeBegin + 1;
        return size > this.maxSliceSize ? this.maxSliceSize : size;
    }

    private byte[] chunkInputStream(InputStream stream, int begin, int length) {
        byte[] buffer = new byte[length];
        try{
            stream.read(buffer, begin, length);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return buffer;
    }
}
