// ------------------------------------------------------------------------------
// Copyright (c) 2017 Microsoft Corporation
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sub-license, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.
// ------------------------------------------------------------------------------

package com.microsoft.graph.concurrency;

import com.google.common.io.ByteStreams;

import com.microsoft.graph.core.ClientException;
import com.microsoft.graph.http.GraphServiceException;
import com.microsoft.graph.http.HttpResponseCode;
import com.microsoft.graph.http.IHttpRequest;
import com.microsoft.graph.http.IStatefulResponseHandler;
import com.microsoft.graph.logger.ILogger;
import com.microsoft.graph.serializer.ISerializer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import okhttp3.MediaType;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static com.microsoft.graph.http.HttpResponseCode.HTTP_OK;

/**
 * Handles the stateful response from the OneDrive upload session
 *
 * @param <UploadType> the expected uploaded item
 */
public class ChunkedUploadResponseHandler<UploadType>
		implements IStatefulResponseHandler<ChunkedUploadResult<UploadType>, UploadType> {
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
	protected ChunkedUploadResponseHandler(@Nonnull final Class<UploadType> uploadType, @Nonnull final Class<? extends IUploadSession>  uploadSessionType) {
        this.deserializeTypeClass = Objects.requireNonNull(uploadType, "parameter uploadType cannot be null");
        this.uploadSessionClass = Objects.requireNonNull(uploadSessionType, "parameter uploadSessionType cannot be null");
	}

	/**
	 * Do nothing before getting the response
	 *
	 * @param response The response
	 */
	@Override
	public void configConnection(@Nonnull final Response response) {
		return;
	}

	/**
	 * Generate the chunked upload response result
	 *
	 * @param request	the HTTP request
	 * @param response the HTTP response
	 * @param serializer the serializer
	 * @param logger	 the system logger
	 * @return the chunked upload result, which could be either an uploaded item or error
	 * @throws Exception an exception occurs if the request was unable to complete for any reason
	 */
	@Override
	@Nullable
	public ChunkedUploadResult<UploadType> generateResult(
			@Nonnull final IHttpRequest request,
			@Nonnull final Response response,
			@Nonnull final ISerializer serializer,
			@Nonnull final ILogger logger) throws Exception {
        Objects.requireNonNull(request, "parameter request cannot be null");
        Objects.requireNonNull(response, "parameter response cannot be null");
        Objects.requireNonNull(serializer, "parameter serializer cannot be null");
        Objects.requireNonNull(logger, "parameter logger cannot be null");
		if (response.code() >= HttpResponseCode.HTTP_CLIENT_ERROR) {
			logger.logDebug("Receiving error during upload, see detail on result error");

			return new ChunkedUploadResult<>(
					GraphServiceException.createFromResponse(request, null, serializer,
							response, logger));
		} else if (response.code() >= HTTP_OK
				&& response.code() < HttpResponseCode.HTTP_MULTIPLE_CHOICES) {
            try(final ResponseBody body = response.body()) {
                final String location = response.headers().get("Location");
                final MediaType contentType = body.contentType();
                final String subType = contentType == null ? null : contentType.subtype();
                if (subType != null && subType.contains("json")) {
                    return parseJsonUploadResult(body, serializer, logger);
                } else if (location != null) {
                    logger.logDebug("Upload session is completed (Outlook), uploaded item returned.");
                    return new ChunkedUploadResult<>(this.deserializeTypeClass.getDeclaredConstructor().newInstance());
                } else {
                    logger.logDebug("Upload session returned an unexpected response");
                }
            }
		}
		return new ChunkedUploadResult<>(new ClientException("Received an unexpected response from the service, response code: " + response.code(), null));
	}

	@Nonnull
	private ChunkedUploadResult<UploadType> parseJsonUploadResult(@Nonnull final ResponseBody responseBody, @Nonnull final ISerializer serializer, @Nonnull final ILogger logger) throws IOException {
		try (final InputStream in = responseBody.byteStream()) {
			final byte[] responseBytes = ByteStreams.toByteArray(in);
			final IUploadSession session = serializer.deserializeObject(new ByteArrayInputStream(responseBytes), uploadSessionClass);

			if (session == null || session.getNextExpectedRanges() == null) {
				logger.logDebug("Upload session is completed (ODSP), uploaded item returned.");
				final UploadType uploadedItem = serializer.deserializeObject(new ByteArrayInputStream(responseBytes), this.deserializeTypeClass);
				return new ChunkedUploadResult<>(uploadedItem);
			} else {
				logger.logDebug("Chunk bytes has been accepted by the server.");
				return new ChunkedUploadResult<>(session);
			}
		}
	}
}
