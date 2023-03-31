package com.microsoft.graph.tasks;

import com.microsoft.graph.exceptions.ClientException;
import com.microsoft.graph.exceptions.ErrorConstants;
import com.microsoft.graph.exceptions.ServiceException;
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

    private static final long DEFAULT_MAX_SLICE_SIZE = 5*1024*1024;
    private IUploadSession uploadSession;
    private final RequestAdapter requestAdapter;
    private final InputStream uploadStream;
    private final long maxSliceSize;
    private ArrayList<AbstractMap.SimpleEntry<Long, Long>> rangesRemaining;
    private final long TotalUploadLength;
    private final ParsableFactory<T> factory;

    public LargeFileUploadTask(@Nullable RequestAdapter requestAdapter,
                               @Nonnull IUploadSession uploadSession,
                               @Nonnull InputStream uploadStream,
                               long streamSize,
                               @Nonnull ParsableFactory<T> factory) throws NoSuchFieldException, IllegalAccessException, IOException {
        this(requestAdapter, uploadSession,uploadStream, streamSize,DEFAULT_MAX_SLICE_SIZE,  factory);
    }

    public LargeFileUploadTask(@Nullable RequestAdapter requestAdapter,
                               @Nonnull IUploadSession uploadSession,
                               @Nonnull InputStream uploadStream,
                               long streamSize,
                               long maxSliceSize,
                               @Nonnull ParsableFactory<T> factory) throws NoSuchFieldException, IllegalAccessException, IOException {
        Objects.requireNonNull(uploadSession);
        Objects.requireNonNull(uploadStream);
        Objects.requireNonNull(factory);
        if(uploadStream.available() <=0) {
            throw new IllegalArgumentException("Must provide a stream that is not empty.");
        }

        this.requestAdapter = Objects.isNull(requestAdapter) ? initializeAdapter(uploadSession.getUploadUrl()):requestAdapter;
        this.uploadSession = extractSessionFromParsable(uploadSession);
        this.TotalUploadLength = streamSize;
        this.rangesRemaining = getRangesRemaining(uploadSession);
        this.uploadStream = uploadStream;
        this.maxSliceSize = maxSliceSize;
        this.factory = factory;
    }

    private UploadResult<T> uploadSliceAsync(UploadSliceRequestBuilder<T> uploadSliceRequestBuilder, ArrayList<Throwable> exceptionsList) throws Throwable {
        boolean firstAttempt = true;
        byte[] buffer = chunkInputStream(uploadStream,(int) uploadSliceRequestBuilder.getRangeBegin(), uploadSliceRequestBuilder.getRangeLength());
        ByteArrayInputStream chunkStream = new ByteArrayInputStream(buffer);
        while(true) {
            try{
                return uploadSliceRequestBuilder.putAsync(chunkStream).get();
            } catch (ExecutionException ex) {
                if(ex.getCause() instanceof ServiceException) {
                    ServiceException se = (ServiceException) ex.getCause();
                    if(se.isMatch(ErrorConstants.Codes.GeneralException) ||
                        (se.isMatch(ErrorConstants.Codes.Timeout))) {
                        if (firstAttempt) {
                            firstAttempt = false;
                            exceptionsList.add(ex.getCause());
                        } else {
                            throw ex.getCause();
                        }
                    }
                    else if(se.isMatch(ErrorConstants.Codes.InvalidRange)){
                        return new UploadResult<>();
                    }
                }
                else{
                    throw ex;
                }
            } catch (InterruptedException ex){
                throw ex;
            }
        }
    }
    @Nonnull
    public CompletableFuture<UploadResult<T>> uploadAsync() {
        return this.uploadAsync(3);
    }
    @Nonnull
    public CompletableFuture<UploadResult<T>> uploadAsync(int maxTries) {
        int uploadTries = 0;
        ArrayList<Throwable> exceptionsList = new ArrayList<>();
        while (uploadTries < maxTries) {
            try {
                List<UploadSliceRequestBuilder<T>> uploadSliceRequestBuilders = getUploadSliceRequests();
                for (UploadSliceRequestBuilder<T> request : uploadSliceRequestBuilders) {
                    UploadResult<T> result;
                    result = uploadSliceAsync(request, exceptionsList);
                    if (result.uploadSucceeded()) {
                        return CompletableFuture.completedFuture(result);
                    }
                }
                updateSessionStatusAsync().get();
                uploadTries += 1;
                if (uploadTries < maxTries) {
                    TimeUnit.SECONDS.sleep((long) 2 * uploadTries * uploadTries);
                }
            } catch (Throwable ex) {
                return new CompletableFuture<UploadResult<T>>() {{
                    completeExceptionally(ex);
                }};
            }
        }
        return new CompletableFuture<UploadResult<T>>() {{
            completeExceptionally(new CancellationException());
        }};
    }
    @Nonnull
    public UploadSession extractSessionFromParsable(@Nonnull Parsable uploadSession) throws IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
        if (!uploadSession.getFieldDeserializers().containsKey("expirationDateTime"))
            throw new IllegalArgumentException("The Parsable does not contain the 'expirationDateTime' property");
        if (!uploadSession.getFieldDeserializers().containsKey("nextExpectedRanges"))
            throw new IllegalArgumentException("The Parsable does not contain the 'nextExpectedRanges' property");
        if (!uploadSession.getFieldDeserializers().containsKey("uploadUrl"))
            throw new IllegalArgumentException("The Parsable does not contain the 'uploadUrl' property");

        UploadSession session = new UploadSession();
        session.setExpirationDateTime((OffsetDateTime) uploadSession.getClass().getDeclaredField("expirationDateTime").get(uploadSession));
        session.setUploadUrl((String) uploadSession.getClass().getDeclaredField("uploadUrl").get(uploadSession));
        session.setNextExpectedRanges((List<String>) uploadSession.getClass().getDeclaredField("nextExpectedRanges").get(uploadSession));
        return session;
    }

    public RequestAdapter initializeAdapter(String uploadUrl) {
        OkHttpClient client = GraphClientFactory.create(new GraphClientOption() {{
            this.featureTracker.setFeatureUsage(FeatureFlag.FILE_UPLOAD_FLAG);
        }}).build();
        return new BaseGraphRequestAdapter(new AnonymousAuthenticationProvider(), uploadUrl, client);
    }

    protected List<UploadSliceRequestBuilder<T>> getUploadSliceRequests() {
        ArrayList<UploadSliceRequestBuilder<T>> builders = new ArrayList<UploadSliceRequestBuilder<T>>();
        for (Map.Entry<Long, Long> entry: rangesRemaining) {
            long currentRangeBegin = entry.getKey();
            long currentEnd = entry.getValue();
            while(currentRangeBegin < currentEnd) {
                long nextSliceSize = nextSliceSize(currentRangeBegin, currentEnd);
                UploadSliceRequestBuilder<T> sliceRequestBuilder =
                    new UploadSliceRequestBuilder<>(this.uploadSession.getUploadUrl(), this.requestAdapter,
                        currentRangeBegin, currentRangeBegin + nextSliceSize -1, this.TotalUploadLength, this.factory);
                builders.add(sliceRequestBuilder);
                currentRangeBegin += nextSliceSize;
            }
        }
        return builders;
    }

    public CompletableFuture<UploadResult<T>> resumeAsync() {
        return this.resumeAsync(3);
    }

    public CompletableFuture<UploadResult<T>> resumeAsync(int maxTries) {
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
            return new CompletableFuture<UploadResult<T>>() {{
                completeExceptionally(new ClientException(ErrorConstants.Messages.ExpiredUploadSession, null));
            }};
        }
        return this.uploadAsync(maxTries);
    }

    public CompletableFuture<Void> deleteSessionAsync() {
        OffsetDateTime expirationDateTime =
            Objects.isNull(this.uploadSession.getExpirationDateTime()) ? OffsetDateTime.now() : this.uploadSession.getExpirationDateTime();
        if(expirationDateTime.isBefore(OffsetDateTime.now()) || expirationDateTime.isEqual(OffsetDateTime.now())) {
            return new CompletableFuture<Void>() {{
                completeExceptionally(new ClientException(ErrorConstants.Messages.ExpiredUploadSession, null));
            }};
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

    protected ArrayList<AbstractMap.SimpleEntry<Long, Long>> getRangesRemaining(IUploadSession uploadSession) {
        ArrayList<AbstractMap.SimpleEntry<Long, Long>> remaining = new ArrayList<AbstractMap.SimpleEntry<Long, Long>>();
        for (String range:uploadSession.getNextExpectedRanges()) {
            String[] specifiers = range.split("-");
            remaining.add(new AbstractMap.SimpleEntry<Long, Long>(Long.valueOf(specifiers[0]),
                specifiers.length == 2 ? Long.parseLong(specifiers[1]) : this.TotalUploadLength-1));
        }
        return remaining;
    }

    private long nextSliceSize(long rangeBegin, long rangeEnd) {
        long size = rangeEnd - rangeBegin + 1;
        return Math.min(size, this.maxSliceSize);
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
