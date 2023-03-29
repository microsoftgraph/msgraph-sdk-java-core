package com.microsoft.graph.tasks;

import com.microsoft.graph.models.IUploadSession;
import com.microsoft.graph.models.UploadResult;
import com.microsoft.graph.models.UploadSession;
import com.microsoft.graph.requests.BaseGraphRequestAdapter;
import com.microsoft.graph.requests.FeatureFlag;
import com.microsoft.graph.requests.GraphClientFactory;
import com.microsoft.graph.requests.GraphClientOption;
import com.microsoft.graph.requests.upload.UploadSliceRequestBuilder;
import com.microsoft.kiota.RequestAdapter;
import com.microsoft.kiota.authentication.AnonymousAuthenticationProvider;
import com.microsoft.kiota.serialization.Parsable;
import com.microsoft.kiota.serialization.ParsableFactory;
import jdk.internal.util.xml.impl.Input;
import okhttp3.OkHttpClient;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.InputStream;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;

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
        this.uploadSession = ExtractSessionFromParsable(uploadSession);
        this.requestAdapter = Objects.isNull(requestAdapter) ? InitializeAdapter(uploadSession.getUploadUrl()):requestAdapter;
        this.uploadStream = uploadStream;
        this.rangesRemaining = GetRangesRemaining(uploadSession);
        this.maxSliceSize = Objects.isNull(maxSliceSize) ? defaultMaxSliceSize : maxSliceSize;
    }

    public CompletableFuture<UploadResult<T>> UploadSliceAsync() {
        boolean firstAttempt = true;

    }

    public UploadSession ExtractSessionFromParsable(Parsable uploadSession) throws IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
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

    public RequestAdapter InitializeAdapter(String uploadUrl) {
        OkHttpClient client = GraphClientFactory.create(new GraphClientOption() {{
            this.featureTracker.setFeatureUsage(FeatureFlag.FILE_UPLOAD_FLAG);
        }}).build();
        return new BaseGraphRequestAdapter(new AnonymousAuthenticationProvider(), uploadUrl, client);
    }

    private List<UploadSliceRequestBuilder<T>> GetUploadSliceRequests() {
        ArrayList<UploadSliceRequestBuilder<T>> builders = new ArrayList<UploadSliceRequestBuilder<T>>();
        for (Map.Entry entry: rangesRemaining) {
            long currentRangeBegin = (long) entry.getKey();
            long currentEnd = (long) entry.getValue();
            while(currentRangeBegin < currentEnd) {
                long nextSliceSize = NextSliceSize(currentRangeBegin, currentEnd);
                UploadSliceRequestBuilder sliceRequestBuilder =
                    new UploadSliceRequestBuilder(this.uploadSession.getUploadUrl(), this.requestAdapter, this.factory,
                        currentRangeBegin, currentRangeBegin + nextSliceSize -1, this.TotalUploadLength);
                builders.add(sliceRequestBuilder);
                currentRangeBegin += nextSliceSize;
            }
        }
        return builders;
    }

    private ArrayList<AbstractMap.SimpleEntry<Long, Long>> GetRangesRemaining(IUploadSession uploadSession) {
        ArrayList<AbstractMap.SimpleEntry<Long, Long>> remaining = new ArrayList<AbstractMap.SimpleEntry<Long, Long>>();
        for (String range:uploadSession.getNextExpectedRanges()) {
            String[] specifiers = range.split("-");
            remaining.add(new AbstractMap.SimpleEntry<Long, Long>(Long.valueOf(specifiers[0]),
                specifiers.length == 2 ? Long.valueOf(specifiers[1]) : this.TotalUploadLength-1));
        }
        return remaining;
    }
    private long NextSliceSize(long rangeBegin, long rangeEnd) {
        long size = rangeEnd - rangeBegin + 1;
        return size > this.maxSliceSize ? this.maxSliceSize : size;
    }



}
