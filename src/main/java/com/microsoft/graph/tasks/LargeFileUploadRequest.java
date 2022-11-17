package com.microsoft.graph.tasks;

class LargeFileUploadRequest<UploadType> {

    /**
     * Content Range header name.
     */
    private static final String CONTENT_RANGE_HEADER_NAME = "Content-Range";

    /**
     * Content Range value format.
     */
    private static final String CONTENT_RANGE_FORMAT = "bytes %1$d-%2$d/%3$d";

    /**
     * The chunk data sent to the server.
     */
    private final byte[] data;

    /**
     * The base request.
     */
    private final BaseRequest<LargeFileUploadResponse<UploadType>> baseRequest;

    /**
     * Construct the ChunkedUploadRequest
     *
     * @param requestUrl The upload URL.
     * @param client     The Graph client.
     * @param options    The query options.
     * @param chunk      The chunk byte array.
     * @param chunkSize  The chunk array size.
     * @param beginIndex The begin index of this chunk in the input stream.
     * @param totalLength The total length of the input stream.
     */
    @SuppressWarnings("unchecked")
    protected LargeFileUploadRequest(@Nonnull final String requestUrl,
                                     @Nonnull final IBaseClient<?> client,
                                     @Nullable final List<? extends Option> options,
                                     @Nonnull final byte[] chunk,
                                     final int chunkSize,
                                     final long beginIndex,
                                     final long totalLength) {
        Objects.requireNonNull(requestUrl, "parameter requestUrl cannot be null");
        Objects.requireNonNull(client, "parameter client cannot be null");
        Objects.requireNonNull(chunk, "parameter chunk cannot be null");
        this.data = new byte[chunkSize];
        System.arraycopy(chunk, 0, this.data, 0, chunkSize);
        this.baseRequest = new BaseRequest<LargeFileUploadResponse<UploadType>>(requestUrl, client, options, (Class<? extends LargeFileUploadResponse<UploadType>>)(new LargeFileUploadResponse<>((UploadType)null)).getClass()) {
        };
        this.baseRequest.setHttpMethod(HttpMethod.PUT);
        this.baseRequest.addHeader(CONTENT_RANGE_HEADER_NAME,
            String.format(Locale.ROOT,
                CONTENT_RANGE_FORMAT,
                beginIndex,
                beginIndex + chunkSize - 1,
                totalLength));
    }

    /**
     * Upload a chunk with tries.
     *
     * @param responseHandler The handler to handle the HTTP response.
     * @return The upload result.
     */
    @SuppressWarnings("unchecked")
    @Nonnull
    public LargeFileUploadResponse<UploadType> upload(
        @Nonnull final LargeFileUploadResponseHandler<UploadType> responseHandler) {
        Objects.requireNonNull(responseHandler, "parameter responseHandler cannot be null");
        LargeFileUploadResponse<UploadType> result = null;

        try {
            result = this.baseRequest
                .getClient()
                .getHttpProvider()
                .send(baseRequest, (Class<LargeFileUploadResponse<UploadType>>)(Class<?>) LargeFileUploadResponse.class, this.data, responseHandler);
        } catch (final ClientException e) {
            throw new ClientException("Request failed with error, retry if necessary.", e);
        }

        if (result != null && (result.chunkCompleted() || result.uploadCompleted())) {
            return result;
        } else
            return new LargeFileUploadResponse<>(
                new ClientException("Upload session failed.", result == null ? null : result.getError()));
    }
}
