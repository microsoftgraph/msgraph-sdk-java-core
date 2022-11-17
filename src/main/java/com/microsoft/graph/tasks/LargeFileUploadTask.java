package com.microsoft.graph.tasks;

import com.microsoft.graph.models.IProgressCallback;
import com.microsoft.graph.models.IUploadSession;
import com.microsoft.graph.requests.BaseClient;
import com.microsoft.graph.requests.IBaseClient;
import com.microsoft.kiota.RequestAdapter;
import com.microsoft.kiota.authentication.AnonymousAuthenticationProvider;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidParameterException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class LargeFileUploadTask<UploadType> {

    /**
     * The default chunk size for upload. Currently set to 5 MiB.
     */
    private static final int DEFAULT_CHUNK_SIZE = 5 * 1024 * 1024;

    /**
     * The required chunk size increment by OneDrive service, which is 320 KiB
     */
    private static final int REQUIRED_CHUNK_SIZE_INCREMENT = 320 * 1024;

    /**
     * The maximum chunk size for a single upload allowed by OneDrive service.
     * Currently the value is 60 MiB.
     */
    private static final int MAXIMUM_CHUNK_SIZE = 60 * 1024 * 1024;

    /**
     * The client
     */
    private final RequestAdapter requestAdapter;

    /**
     * The input stream
     */
    private final InputStream inputStream;

    /**
     * The upload session URL
     */
    private final String uploadUrl;

    /**
     * The stream size
     */
    private final long streamSize;

    /**
     * The upload response handler
     */
    private final LargeFileUploadResponseHandler<UploadType> responseHandler;

    /**
     * The counter for how many bytes have been read from input stream
     */
    private long readSoFar;

    /**
     * Creates the ChunkedUploadProvider
     *
     * @param uploadSession   the initial upload session
     * @param requestAdapter          the Graph client
     * @param inputStream     the input stream
     * @param streamSize      the stream size
     * @param uploadTypeClass the upload type class
     */
    @SuppressFBWarnings
    public LargeFileUploadTask(@Nonnull final IUploadSession uploadSession,
                               @Nullable final RequestAdapter requestAdapter,
                               @Nonnull final InputStream inputStream,
                               final long streamSize,
                               @Nonnull final Class<UploadType> uploadTypeClass) {
        Objects.requireNonNull(uploadSession, "Upload session is null.");

        if (streamSize <= 0) {
            throw new InvalidParameterException("Stream size should larger than 0.");
        }

        this.requestAdapter = requestAdapter == null ? InitializeClient(uploadSession.getUploadUrl()) : requestAdapter;
        this.readSoFar = 0;
        this.inputStream = Objects.requireNonNull(inputStream, "Input stream is null.");
        this.streamSize = streamSize;
        this.uploadUrl = uploadSession.getUploadUrl();
        this.responseHandler = new LargeFileUploadResponseHandler<>(uploadTypeClass, uploadSession.getClass());
    }

    private IBaseClient InitializeClient(String uploadUrl) {
        return new BaseClient(new AnonymousAuthenticationProvider(),uploadUrl);
    }

    /**
     * Uploads content to remote upload session based on the input stream
     *
     * @param options  the upload options
     * @param chunkSize the customized chunk size
     * @param progressCallback the callback for upload progress
     * @return a future with the result
     * @throws IOException the IO exception that occurred during upload
     */
    @Nonnull
    public CompletableFuture<LargeFileUploadResult<UploadType>> uploadAsync(@Nullable final int chunkSize, @Nullable final List<Option> options, @Nullable final IProgressCallback progressCallback)
        throws IOException {

        int internalChunkSize = chunkSize;
        if (internalChunkSize == 0) {
            internalChunkSize = DEFAULT_CHUNK_SIZE;
        }

        if (internalChunkSize % REQUIRED_CHUNK_SIZE_INCREMENT != 0) {
            throw new IllegalArgumentException("Chunk size must be a multiple of 320 KiB");
        }

        if (internalChunkSize > MAXIMUM_CHUNK_SIZE) {
            throw new IllegalArgumentException("Please set chunk size smaller than 60 MiB");
        }

        byte[] buffer = new byte[internalChunkSize];

        while (this.readSoFar < this.streamSize) {
            int buffRead = 0;

            // inner loop is to work-around the case where read buffer size is limited to less than chunk size by a global setting
            while (buffRead < internalChunkSize) {
                int read = 0;
                read = this.inputStream.read(buffer, buffRead, internalChunkSize - buffRead);
                if (read == -1) {
                    break;
                }
                buffRead += read;
            }

            final LargeFileUploadRequest<UploadType> request =
                new LargeFileUploadRequest<>(this.uploadUrl, this.client, options, buffer, buffRead,
                    this.readSoFar, this.streamSize);
            final LargeFileUploadResponse<UploadType> response = request.upload(this.responseHandler);
            // TODO: upload should return a future, use sendfuture instead and the futures should be chained with completableFuture.then apply

            if (response.uploadCompleted()) {
                if(progressCallback != null) {
                    progressCallback.progress(this.streamSize, this.streamSize);
                }
                final LargeFileUploadResult<UploadType> result = new LargeFileUploadResult<>();
                if (response.getItem() != null) {
                    result.responseBody = response.getItem();
                }
                if (response.getLocation() != null) {
                    result.location = response.getLocation();
                }
                return completedFuture(result);
            } else if (response.chunkCompleted()) {
                if(progressCallback != null) {
                    progressCallback.progress(this.readSoFar, this.streamSize);
                }
            } else if (response.hasError()) {
                return failedFuture(response.getError());
            }

            this.readSoFar += buffRead;
        }
        return failedFuture(new ClientException("Upload did not complete", null));
    }
    private CompletableFuture<LargeFileUploadResult<UploadType>> completedFuture(final LargeFileUploadResult<UploadType> result) { // CompletableFuture.completedFuture(result.getItem()); missing on android
        final CompletableFuture<LargeFileUploadResult<UploadType>> fut = new CompletableFuture<>();
        fut.complete(result);
        return fut;
    }
    private CompletableFuture<LargeFileUploadResult<UploadType>> failedFuture(ClientException ex) { // CompletableFuture.failedFuture not available on android
        final CompletableFuture<LargeFileUploadResult<UploadType>> fut = new CompletableFuture<>();
        fut.completeExceptionally(ex);
        return fut;
    }

    /**
     * Uploads content to remote upload session based on the input stream
     *
     * @return a future with the result
     * @throws IOException the IO exception that occurred during upload
     */
    @Nonnull
    public CompletableFuture<LargeFileUploadResult<UploadType>> uploadAsync()
        throws IOException {
        return uploadAsync(0);
    }
    /**
     * Uploads content to remote upload session based on the input stream
     *
     * @param chunkSize the customized chunk size
     * @return a future with the result
     * @throws IOException the IO exception that occurred during upload
     */
    @Nonnull
    public CompletableFuture<LargeFileUploadResult<UploadType>> uploadAsync(@Nullable final int chunkSize)
        throws IOException {
        return uploadAsync(chunkSize, null);
    }
    /**
     * Uploads content to remote upload session based on the input stream
     *
     * @param chunkSize the customized chunk size
     * @param options  the upload options
     * @return a future with the result
     * @throws IOException the IO exception that occurred during upload
     */
    @Nonnull
    public CompletableFuture<LargeFileUploadResult<UploadType>> uploadAsync(@Nullable final int chunkSize, @Nullable final List<Option> options)
        throws IOException {
        return uploadAsync(chunkSize, options, null);
    }

    /**
     * Uploads content to remote upload session based on the input stream
     *
     * @return the result
     * @throws IOException the IO exception that occurred during upload
     */
    @Nonnull
    public LargeFileUploadResult<UploadType> upload()
        throws IOException {
        return upload(0);
    }
}
