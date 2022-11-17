package com.microsoft.graph.tasks;

class LargeFileUploadResponseHandler<UploadType>
    implements IStatefulResponseHandler<LargeFileUploadResponse<UploadType>, UploadType> {
    /**
     * The expected deserialized upload type
     */
    private final Class<UploadType> deserializeTypeClass;

    private final Class<? extends IUploadSession> uploadSessionClass;
    /**
     * Creates a chunked upload response handler
     *
     * @param uploadType the expected upload item type
     * @param uploadSessionType the type of the upload session
     */
    protected LargeFileUploadResponseHandler(@Nonnull final Class<UploadType> uploadType, @Nonnull final Class<? extends IUploadSession>  uploadSessionType) {
        this.deserializeTypeClass = Objects.requireNonNull(uploadType, "parameter uploadType cannot be null");
        this.uploadSessionClass = Objects.requireNonNull(uploadSessionType, "parameter uploadSessionType cannot be null");
    }

    @Override
    @Nullable
    @SuppressFBWarnings
    public <ResponseType> LargeFileUploadResponse<UploadType> generateResult(
        @Nonnull final IHttpRequest request,
        @Nonnull final ResponseType response,
        @Nonnull final ISerializer serializer,
        @Nonnull final ILogger logger) throws Exception {
        Objects.requireNonNull(request, "parameter request cannot be null");
        Objects.requireNonNull(response, "parameter response cannot be null");
        Objects.requireNonNull(serializer, "parameter serializer cannot be null");
        Objects.requireNonNull(logger, "parameter logger cannot be null");
        if(!(response instanceof Response)) {
            throw new ClientException("unsupported response type", null);
        }
        final Response nativeResponse = (Response)response;

        if (nativeResponse.code() >= HttpResponseCode.HTTP_CLIENT_ERROR) {
            logger.logDebug("Receiving error during upload, see detail on result error");

            return new LargeFileUploadResponse<>(
                GraphServiceException.createFromResponse(request, null, serializer,
                    nativeResponse, logger));
        } else if (nativeResponse.code() >= HTTP_OK
            && nativeResponse.code() < HttpResponseCode.HTTP_MULTIPLE_CHOICES) {
            try(final ResponseBody body = nativeResponse.body()) {
                final String location = nativeResponse.headers().get("Location");
                final MediaType contentType = body.contentType();
                final String subType = contentType == null ? null : contentType.subtype();
                if (subType != null && subType.contains("json")) {
                    return parseJsonUploadResult(body, serializer, logger);
                } else if (location != null) {
                    logger.logDebug("Upload session is completed (Outlook), uploaded item returned.");
                    return new LargeFileUploadResponse<>(location);
                } else {
                    logger.logDebug("Upload session returned an unexpected response");
                }
            }
        }
        return new LargeFileUploadResponse<>(new ClientException("Received an unexpected response from the service, response code: " + nativeResponse.code(), null));
    }

    @Nonnull
    private LargeFileUploadResponse<UploadType> parseJsonUploadResult(@Nonnull final ResponseBody responseBody, @Nonnull final ISerializer serializer, @Nonnull final ILogger logger) throws IOException {
        try (final InputStream in = responseBody.byteStream()) {
            final byte[] responseBytes = ByteStreams.toByteArray(in);
            final IUploadSession session = serializer.deserializeObject(new ByteArrayInputStream(responseBytes), uploadSessionClass);

            if (session == null || session.getNextExpectedRanges() == null) {
                logger.logDebug("Upload session is completed (ODSP), uploaded item returned.");
                final UploadType uploadedItem = serializer.deserializeObject(new ByteArrayInputStream(responseBytes), this.deserializeTypeClass);
                return new LargeFileUploadResponse<>(uploadedItem);
            } else {
                logger.logDebug("Chunk bytes has been accepted by the server.");
                return new LargeFileUploadResponse<>(session);
            }
        }
    }
}
