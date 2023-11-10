package com.microsoft.graph.tasks;

import com.microsoft.graph.ErrorConstants;
import com.microsoft.graph.exceptions.ClientException;
import com.microsoft.graph.models.IProgressCallback;
import com.microsoft.graph.models.IUploadSession;
import com.microsoft.graph.models.UploadResult;
import com.microsoft.graph.models.UploadSession;
import com.microsoft.graph.requests.BaseGraphRequestAdapter;
import com.microsoft.graph.requests.FeatureFlag;
import com.microsoft.graph.requests.GraphClientFactory;
import com.microsoft.graph.requests.options.GraphClientOption;
import com.microsoft.graph.requests.upload.UploadSessionRequestBuilder;
import com.microsoft.graph.requests.upload.UploadSliceRequestBuilder;
import com.microsoft.kiota.ApiException;
import com.microsoft.kiota.RequestAdapter;
import com.microsoft.kiota.authentication.AnonymousAuthenticationProvider;
import com.microsoft.kiota.serialization.Parsable;
import com.microsoft.kiota.serialization.ParsableFactory;
import com.microsoft.kiota.serialization.ParseNode;
import okhttp3.OkHttpClient;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.concurrent.CancellationException;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Task for uploading large files including pausing and resuming.
 * @param <T> They type of Item that we will be uploading
 */
public class LargeFileUploadTask<T extends Parsable > {

    private static final long DEFAULT_MAX_SLICE_SIZE = (long) 5*1024*1024;
    private IUploadSession uploadSession;
    private final RequestAdapter requestAdapter;
    private final InputStream uploadStream;
    private final long maxSliceSize;
    private ArrayList<AbstractMap.SimpleEntry<Long, Long>> rangesRemaining;
    private final long totalUploadLength;
    private final ParsableFactory<T> factory;
    private long amountUploaded;
    /**
     * LargeFileUploadTask instance constructor.
     * @param requestAdapter The request adapter for this upload task.
     * @param uploadSession Parsable containing upload session information.
     * @param uploadStream Readable stream of information to be uploaded.
     * @param streamSize The size of the information stream to be uploaded.
     * @param factory The ParsableFactory defining the instantiation of the object being uploaded.
     * @throws IllegalAccessException thrown when attempting to extract uploadSession information.
     * @throws IOException thrown when attempting to extract uploadSession information.
     * @throws InvocationTargetException thrown when attempting to extract uploadSession information.
     * @throws NoSuchMethodException thrown when attempting to extract uploadSession information.
     */
    public LargeFileUploadTask(@Nullable final RequestAdapter requestAdapter,
                               @Nonnull Parsable uploadSession,
                               @Nonnull InputStream uploadStream,
                               long streamSize,
                               @Nonnull ParsableFactory<T> factory) throws IllegalAccessException, IOException, InvocationTargetException, NoSuchMethodException {
        this(requestAdapter, uploadSession,uploadStream, streamSize,DEFAULT_MAX_SLICE_SIZE,  factory);
    }
    /**
     * LargeFileUploadTask instance constructor.
     * @param requestAdapter The request adapter for this upload task.
     * @param uploadSession Parsable containing upload session information.
     * @param uploadStream Readable stream of information to be uploaded.
     * @param streamSize The size of the information stream to be uploaded.
     * @param maxSliceSize Max size(in bytes) of each slice to be uploaded. Defaults to 5 MB. When uploading to OneDrive or SharePoint, this value needs to be a multiple of 320 KiB (327,680 bytes).
     * @param factory The ParsableFactory defining the instantiation of the object being uploaded.
     * @throws IllegalAccessException thrown when attempting to extract uploadSession information.
     * @throws IOException thrown when attempting to extract uploadSession information.
     * @throws InvocationTargetException thrown when attempting to extract uploadSession information.
     * @throws NoSuchMethodException thrown when attempting to extract uploadSession information.
     */
    public LargeFileUploadTask(@Nullable final RequestAdapter requestAdapter,
                               @Nonnull Parsable uploadSession,
                               @Nonnull InputStream uploadStream,
                               long streamSize,
                               long maxSliceSize,
                               @Nonnull ParsableFactory<T> factory) throws IllegalAccessException, IOException, InvocationTargetException, NoSuchMethodException {
        Objects.requireNonNull(uploadSession);
        Objects.requireNonNull(uploadStream);
        Objects.requireNonNull(factory);
        if(uploadStream.available() <=0) {
            throw new IllegalArgumentException("Must provide a stream that is not empty.");
        }
        this.uploadSession = extractSessionFromParsable(uploadSession);
        this.requestAdapter = Objects.isNull(requestAdapter) ? initializeAdapter(this.uploadSession.getUploadUrl()):requestAdapter;
        this.totalUploadLength = streamSize;
        this.rangesRemaining = getRangesRemaining(this.uploadSession);
        this.uploadStream = uploadStream;
        this.maxSliceSize = maxSliceSize;
        this.factory = factory;
    }
    /**
     * Perform the upload task.
     * @return An UploadResult model containing the information from the server resulting from the upload request.
     * May also occur if interruption occurs in .sleep() call.
     * @throws IOException if there was an error reading the chunk input stream.
     * @throws InterruptedException if the thread is interrupted while sleeping.
     */
    @Nonnull
    public UploadResult<T> upload() throws IOException, InterruptedException{
        return this.upload(3, null);
    }
    /**
     * Perform the upload task.
     * @param maxTries Number of times to retry the task before giving up.
     * @param progress IProgress interface describing how to report progress.
     * @return An UploadResult model containing the information from the server resulting from the upload request.
     * May also occur if interruption occurs in .sleep() call.
     * @throws IOException if there was an error reading the chunk input stream.
     * @throws InterruptedException if the thread is interrupted while sleeping.
     */
    @Nonnull
    public UploadResult<T> upload(int maxTries, @Nullable IProgressCallback progress) throws IOException, InterruptedException {
        int uploadTries = 0;
        ArrayList<Throwable> exceptionsList = new ArrayList<>();
        while (uploadTries < maxTries) {

                List<UploadSliceRequestBuilder<T>> uploadSliceRequestBuilders = getUploadSliceRequests();
                for (UploadSliceRequestBuilder<T> request : uploadSliceRequestBuilders) {
                    UploadResult<T> result;
                    result = uploadSlice(request, exceptionsList);
                    amountUploaded += request.getRangeLength();
                    if(progress != null) {
                        progress.report(amountUploaded, this.totalUploadLength);
                    }
                    if (result.isUploadSuccessful()) {
                        return result;
                    }
                }
                updateSessionStatus();
                uploadTries += 1;
                if (uploadTries < maxTries) {
                    TimeUnit.SECONDS.sleep((long) 2 * uploadTries * uploadTries);
                }
            }
        throw new CancellationException("The upload task was retried the maximum number of times without success and has been cancelled.");
    }
    /**
     * Resume the upload task.
     * @return An UploadResult model containing the information from the server resulting from the upload request.
     * @throws ClientException if the upload session has expired.
     * @throws IOException if there was an error reading the chunk input stream.
     * @throws InterruptedException if the thread is interrupted while sleeping.
     */
    @Nonnull
    public UploadResult<T> resume() throws ClientException, IOException , InterruptedException{
        return this.resume(3, null);
    }
    /**
     * Resume the upload task.
     * @param maxTries Number of times to retry the task before giving up.
     * @param progress IProgress interface describing how to report progress.
     * @return An UploadResult model containing the information from the server resulting from the upload request.
     * @throws ClientException if the upload session has expired.
     * @throws IOException if there was an error reading the chunk input stream.
     * @throws InterruptedException if the thread is interrupted while sleeping.
     */
    @Nonnull
    public UploadResult<T> resume(int maxTries, @Nullable IProgressCallback progress) throws ClientException, IOException , InterruptedException{
        IUploadSession session;
        session = updateSessionStatus();
        OffsetDateTime expirationDateTime = Objects.isNull(session.getExpirationDateTime()) ? OffsetDateTime.now() : session.getExpirationDateTime();
        if(expirationDateTime.isBefore(OffsetDateTime.now()) || expirationDateTime.isEqual(OffsetDateTime.now())) {
            throw new ClientException(ErrorConstants.Messages.EXPIRED_UPLOAD_SESSION);
        }
        return this.upload(maxTries, progress);
    }
    /**
     * Delete the upload session.
     * @throws ClientException if the upload session has expired.
     */
    public void deleteSession() throws ClientException {
        OffsetDateTime expirationDateTime = Objects.isNull(this.uploadSession.getExpirationDateTime()) ? OffsetDateTime.now() : this.uploadSession.getExpirationDateTime();
        if(expirationDateTime.isBefore(OffsetDateTime.now()) || expirationDateTime.isEqual(OffsetDateTime.now())) {
            throw new ClientException(ErrorConstants.Messages.EXPIRED_UPLOAD_SESSION);
        }
        UploadSessionRequestBuilder<T> builder = new UploadSessionRequestBuilder<>(this.uploadSession.getUploadUrl(), this.requestAdapter, this.factory);
        builder.delete();
    }
    /**
     * Get the session information from the server, and update the internally held session with the updated information.
     * @return the updated UploadSession model.
     */
    @Nonnull
    public IUploadSession updateSessionStatus() {
        UploadSessionRequestBuilder<T> sessionRequestBuilder = new UploadSessionRequestBuilder<>(this.uploadSession.getUploadUrl(), this.requestAdapter, this.factory);
        IUploadSession session = sessionRequestBuilder.get();
        this.rangesRemaining = getRangesRemaining(session);
        session.setUploadUrl(this.uploadSession.getUploadUrl());
        this.uploadSession = session;
        return session;
    }
    private UploadResult<T> uploadSlice(UploadSliceRequestBuilder<T> uploadSliceRequestBuilder, ArrayList<Throwable> exceptionsList) throws IOException {
        byte[] buffer = chunkInputStream(uploadStream,(int) uploadSliceRequestBuilder.getRangeBegin(), (int)uploadSliceRequestBuilder.getRangeLength());
        ByteArrayInputStream chunkStream = new ByteArrayInputStream(buffer);
        try {
            return uploadSliceRequestBuilder.put(chunkStream);
        } catch (ApiException apiException) {
            return handleApiException(apiException, exceptionsList);
        }
    }
    private UploadResult<T> handleApiException(ApiException apiException, ArrayList<Throwable> exceptionsList) {
        if(apiException.getMessage().toLowerCase(Locale.ROOT).contains(ErrorConstants.Codes.GENERAL_EXCEPTION.toLowerCase(Locale.ROOT))
            || apiException.getMessage().toLowerCase(Locale.ROOT).contains(ErrorConstants.Codes.TIMEOUT.toLowerCase(Locale.ROOT))) {
                exceptionsList.add(apiException);
        } else if (apiException.getMessage().toLowerCase(Locale.ROOT).contains(ErrorConstants.Codes.INVALID_RANGE.toLowerCase(Locale.ROOT))) {
            return new UploadResult<>();
        }
        throw apiException;
    }
    /**
     * Creates the UploadSliceRequestBuilders for the upload task based on the upload session information.
     * @return The list of UploadSliceRequestsBuilders, each describing a slice to be uploaded.
     */
    @Nonnull
    protected List<UploadSliceRequestBuilder<T>> getUploadSliceRequests() {
        ArrayList<UploadSliceRequestBuilder<T>> builders = new ArrayList<>();
        for (Map.Entry<Long, Long> entry: rangesRemaining) {
            long currentRangeBegin = entry.getKey();
            long currentEnd = entry.getValue();
            while(currentRangeBegin < currentEnd) {
                long nextSliceSize = nextSliceSize(currentRangeBegin, currentEnd);
                UploadSliceRequestBuilder<T> sliceRequestBuilder =
                    new UploadSliceRequestBuilder<>(this.uploadSession.getUploadUrl(), this.requestAdapter,
                        currentRangeBegin, currentRangeBegin + nextSliceSize -1, this.totalUploadLength, this.factory);
                builders.add(sliceRequestBuilder);
                currentRangeBegin += nextSliceSize;
            }
        }
        return builders;
    }
    private ArrayList<AbstractMap.SimpleEntry<Long, Long>> getRangesRemaining(IUploadSession uploadSession) {
        ArrayList<AbstractMap.SimpleEntry<Long, Long>> remaining = new ArrayList<>();
        for (String range:uploadSession.getNextExpectedRanges()) {
            String[] specifiers = range.split("-");
            remaining.add(new AbstractMap.SimpleEntry<>(Long.valueOf(specifiers[0]),
                specifiers.length == 2 ? Long.parseLong(specifiers[1]) : this.totalUploadLength -1));
        }
        return remaining;
    }
    private RequestAdapter initializeAdapter(String uploadUrl) {
        GraphClientOption options = new GraphClientOption();
        options.featureTracker.setFeatureUsage(FeatureFlag.FILE_UPLOAD_FLAG);
        OkHttpClient client = GraphClientFactory.create(options).build();
        return new BaseGraphRequestAdapter(new AnonymousAuthenticationProvider(), uploadUrl, client);
    }
    /** Extract the upload session information from parsable and return in a new UploadSession model. */
    @Nonnull
    private IUploadSession extractSessionFromParsable(@Nonnull Parsable uploadSession) throws IllegalArgumentException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Map<String, Consumer<ParseNode>> deserializers = uploadSession.getFieldDeserializers();
        if (!deserializers.containsKey("expirationDateTime"))
            throw new IllegalArgumentException("The Parsable does not contain the 'expirationDateTime' property");
        if (!deserializers.containsKey("nextExpectedRanges"))
            throw new IllegalArgumentException("The Parsable does not contain the 'nextExpectedRanges' property");
        if (!deserializers.containsKey("uploadUrl"))
            throw new IllegalArgumentException("The Parsable does not contain the 'uploadUrl' property");

        UploadSession session = new UploadSession();
        session.setExpirationDateTime((OffsetDateTime) uploadSession.getClass().getDeclaredMethod("getExpirationDateTime").invoke(uploadSession));
        session.setUploadUrl((String) uploadSession.getClass().getDeclaredMethod("getUploadUrl").invoke(uploadSession));
        session.setNextExpectedRanges((List<String>) uploadSession.getClass().getDeclaredMethod("getNextExpectedRanges").invoke(uploadSession));
        return session;
    }
    private long nextSliceSize(long rangeBegin, long rangeEnd)  {
        long size = rangeEnd - rangeBegin + 1;
        return Math.min(size, this.maxSliceSize);
    }
    private byte[] chunkInputStream(InputStream stream, int begin, int length) throws IOException {
        byte[] buffer = new byte[length];
        int lengthAssert = stream.read(buffer, begin, length);
        assert lengthAssert == length;
        return buffer;
    }
}
