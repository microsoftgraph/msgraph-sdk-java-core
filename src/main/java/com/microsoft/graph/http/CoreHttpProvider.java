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

package com.microsoft.graph.http;

import com.google.common.annotations.VisibleForTesting;

import com.microsoft.graph.core.ClientException;
import com.microsoft.graph.httpcore.middlewareoption.RedirectOptions;
import com.microsoft.graph.httpcore.middlewareoption.RetryOptions;
import com.microsoft.graph.logger.ILogger;
import com.microsoft.graph.logger.LoggerLevel;
import com.microsoft.graph.options.HeaderOption;
import com.microsoft.graph.serializer.ISerializer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSink;

/**
 * HTTP provider based off of OkHttp and msgraph-sdk-java-core library
 */
public class CoreHttpProvider implements IHttpProvider<Request> {
    /**
     * The content type header
     */
    private static final String CONTENT_TYPE_HEADER_NAME = "Content-Type";
    /**
     * The encoding type for getBytes
     */
    private static final String JSON_ENCODING = "UTF-8";
    /**
     * The content type for JSON responses
     */
    private static final String JSON_CONTENT_TYPE = "application/json";
    /**
     * The binary content type header's value
     */
    private static final String BINARY_CONTENT_TYPE = "application/octet-stream";
    /**
	 * The serializer
	 */
	private final ISerializer serializer;

	/**
	 * The logger
	 */
	private final ILogger logger;

	/**
	 * The OkHttpClient that handles all requests
	 */
    private OkHttpClient corehttpClient;

	/**
	 * Creates the CoreHttpProvider
	 *
	 * @param serializer             the serializer
	 * @param logger                 the logger for diagnostic information
	 * @param httpClient             the client to send http requests with
	 */
	public CoreHttpProvider(@Nonnull final ISerializer serializer,
			@Nonnull final ILogger logger,
			@Nonnull final OkHttpClient httpClient) {
        Objects.requireNonNull(logger, "parameter logger cannot be null");
        Objects.requireNonNull(serializer, "parameter serializer cannot be null");
        Objects.requireNonNull(httpClient, "parameter httpClient cannot be null");
        this.serializer = serializer;
        this.logger = logger;
        this.corehttpClient = httpClient;
	}

	/**
	 * Gets the serializer for this HTTP provider
	 *
	 * @return the serializer for this provider
	 */
	@Override
	@Nullable
	public ISerializer getSerializer() {
		return serializer;
	}

	/**
	 * Sends the HTTP request asynchronously
	 *
	 * @param request      the request description
	 * @param serializable the object to send to the service in the body of the request
	 * @param <Result>     the type of the response object
	 * @param <Body>       the type of the object to send to the service in the body of the request
	 * @return a future with the result
	 */
	@Override
	@Nonnull
	public <Result, Body> java.util.concurrent.CompletableFuture<Result> sendAsync(@Nonnull final IHttpRequest request,
			@Nonnull final Class<Result> resultClass,
			@Nullable final Body serializable) {
        Objects.requireNonNull(request, "parameter request cannot be null");
        Objects.requireNonNull(resultClass, "parameter resultClass cannot be null");
		return sendAsync(request,
							resultClass,
							serializable,
							null);
    }
    /**
     * Sends the HTTP request
     *
     * @param request           the request description
     * @param resultClass       the class of the response from the service
     * @param serializable      the object to send to the service in the body of the request
     * @param handler           the handler for stateful response
     * @param <Result>          the expected return type return
     * @param <BodyType>        the type of the object to send to the service in the body of the request
     * @param <DeserializeType> the type of the HTTP response object
     * @return                  a future with the result
     * @throws ClientException  this exception occurs if the request was unable to complete for any reason
     */
    @Nonnull
    public <Result, BodyType, DeserializeType> java.util.concurrent.CompletableFuture<Result> sendAsync(@Nonnull final IHttpRequest request,
                                                    @Nonnull final Class<Result> resultClass,
                                                    @Nullable final BodyType serializable,
                                                    @Nullable final IStatefulResponseHandler<Result, DeserializeType> handler)
            throws ClientException {
        Objects.requireNonNull(request, "parameter request cannot be null");
        Objects.requireNonNull(resultClass, "parameter resultClass cannot be null");
        return sendRequestAsyncInternal(request,
                resultClass,
                serializable,
                handler);
    }

	/**
	 * Sends the HTTP request
	 *
	 * @param request      the request description
	 * @param resultClass  the class of the response from the service
	 * @param serializable the object to send to the service in the body of the request
	 * @param <Result>     the type of the response object
	 * @param <Body>       the type of the object to send to the service in the body of the request
	 * @return             the result from the request
	 * @throws ClientException an exception occurs if the request was unable to complete for any reason
	 */
	@Override
	@Nullable
	public <Result, Body> Result send(@Nonnull final IHttpRequest request,
			@Nonnull final Class<Result> resultClass,
			@Nullable final Body serializable)
					throws ClientException {
        Objects.requireNonNull(request, "parameter request cannot be null");
        Objects.requireNonNull(resultClass, "parameter resultClass cannot be null");
		return send(request, resultClass, serializable, null);
	}

	/**
	 * Sends the HTTP request
	 *
	 * @param request           the request description
	 * @param resultClass       the class of the response from the service
	 * @param serializable      the object to send to the service in the body of the request
	 * @param handler           the handler for stateful response
	 * @param <Result>          the type of the response object
	 * @param <Body>            the type of the object to send to the service in the body of the request
	 * @param <DeserializeType> the response handler for stateful response
	 * @return                  the result from the request
	 * @throws ClientException this exception occurs if the request was unable to complete for any reason
	 */
    @Nullable
	public <Result, Body, DeserializeType> Result send(@Nonnull final IHttpRequest request,
			@Nonnull final Class<Result> resultClass,
			@Nullable final Body serializable,
			@Nullable final IStatefulResponseHandler<Result, DeserializeType> handler) throws ClientException {
            Objects.requireNonNull(request, "parameter request cannot be null");
            Objects.requireNonNull(resultClass, "parameter resultClass cannot be null");
            return sendRequestInternal(request, resultClass, serializable, handler);
	}
	/**
	 * Sends the HTTP request
	 *
	 * @param request           the request description
	 * @param resultClass       the class of the response from the service
	 * @param serializable      the object to send to the service in the body of the request
	 * @param <Result>          the type of the response object
	 * @param <Body>            the type of the object to send to the service in the body of the request
	 * @return                  the result from the request
	 * @throws ClientException an exception occurs if the request was unable to complete for any reason
	 */
	@Nullable
	public <Result, Body> Request getHttpRequest(@Nonnull final IHttpRequest request,
			@Nonnull final Class<Result> resultClass,
			@Nullable final Body serializable) throws ClientException {
        Objects.requireNonNull(request, "parameter request cannot be null");
        Objects.requireNonNull(resultClass, "parameter resultClass cannot be null");
		final int defaultBufferSize = 4096;

		final URL requestUrl = request.getRequestUrl();
		logger.logDebug("Starting to send request, URL " + requestUrl.toString());

		// Request level middleware options
		final RedirectOptions redirectOptions = request.getMaxRedirects() <= 0 ? null : new RedirectOptions(request.getMaxRedirects(), request.getShouldRedirect());
		final RetryOptions retryOptions = request.getShouldRetry() == null ? null : new RetryOptions(request.getShouldRetry(), request.getMaxRetries(), request.getDelay());

		final Request coreHttpRequest = convertIHttpRequestToOkHttpRequest(request);
		Request.Builder corehttpRequestBuilder = coreHttpRequest
				.newBuilder();
		if(redirectOptions != null) {
			corehttpRequestBuilder = corehttpRequestBuilder.tag(RedirectOptions.class, redirectOptions);
		}
		if(retryOptions != null) {
			corehttpRequestBuilder = corehttpRequestBuilder.tag(RetryOptions.class, retryOptions);
		}

		String contenttype = null;

		logger.logDebug("Request Method " + request.getHttpMethod().toString());
		final List<HeaderOption> requestHeaders = request.getHeaders();

		for(HeaderOption headerOption : requestHeaders) {
			if(headerOption.getName().equalsIgnoreCase(CONTENT_TYPE_HEADER_NAME)) {
				contenttype = headerOption.getValue().toString();
				break;
			}
		}

		final byte[] bytesToWrite;
		corehttpRequestBuilder.addHeader("Accept", "*/*");
		if (serializable == null) {
			// Send an empty body through with a POST request
			// This ensures that the Content-Length header is properly set
			if (request.getHttpMethod() == HttpMethod.POST) {
				bytesToWrite = new byte[0];
				if(contenttype == null) {
					contenttype = BINARY_CONTENT_TYPE;
				}
			}
			else {
				bytesToWrite = null;
			}
		} else if (serializable instanceof byte[]) {
			logger.logDebug("Sending byte[] as request body");
			bytesToWrite = (byte[]) serializable;

			// If the user hasn't specified a Content-Type for the request
			if (!hasHeader(requestHeaders, CONTENT_TYPE_HEADER_NAME)) {
				corehttpRequestBuilder.addHeader(CONTENT_TYPE_HEADER_NAME, BINARY_CONTENT_TYPE);
				contenttype = BINARY_CONTENT_TYPE;
			}
		} else {
			logger.logDebug("Sending " + serializable.getClass().getName() + " as request body");
			final String serializeObject = serializer.serializeObject(serializable);
			try {
				bytesToWrite = serializeObject.getBytes(JSON_ENCODING);
			} catch (final UnsupportedEncodingException ex) {
				final ClientException clientException = new ClientException("Unsupported encoding problem: ",
						ex);
				logger.logError("Unsupported encoding problem: " + ex.getMessage(), ex);
				throw clientException;
			}

			// If the user hasn't specified a Content-Type for the request
			if (!hasHeader(requestHeaders, CONTENT_TYPE_HEADER_NAME)) {
				corehttpRequestBuilder.addHeader(CONTENT_TYPE_HEADER_NAME, JSON_CONTENT_TYPE);
				contenttype = JSON_CONTENT_TYPE;
			}
		}

		RequestBody requestBody = null;
		// Handle cases where we've got a body to process.
		if (bytesToWrite != null) {
			final String mediaContentType = contenttype;
			requestBody = new RequestBody() {
				@Override
				public long contentLength() throws IOException {
					return bytesToWrite.length;
				}
				@Override
				public void writeTo(BufferedSink sink) throws IOException {
					int writtenSoFar = 0;
					try (final OutputStream out = sink.outputStream()) {
						try (final BufferedOutputStream bos = new BufferedOutputStream(out)){
							int toWrite;
							do {
								toWrite = Math.min(defaultBufferSize, bytesToWrite.length - writtenSoFar);
								bos.write(bytesToWrite, writtenSoFar, toWrite);
								writtenSoFar = writtenSoFar + toWrite;
							} while (toWrite > 0);
						}
					}
				}

				@Override
				public MediaType contentType() {
					return MediaType.parse(mediaContentType);
				}
			};
		}

		corehttpRequestBuilder.method(request.getHttpMethod().toString(), requestBody);
		return corehttpRequestBuilder.build();
	}
	/**
	 * Sends the HTTP request
	 *
	 * @param request           the request description
	 * @param resultClass       the class of the response from the service
	 * @param serializable      the object to send to the service in the body of the request
	 * @param handler           the handler for stateful response
	 * @param <Result>          the type of the response object
	 * @param <Body>            the type of the object to send to the service in the body of the request
	 * @param <DeserializeType> the response handler for stateful response
	 * @return                  the result from the request
	 * @throws ClientException an exception occurs if the request was unable to complete for any reason
	 */
	@Nonnull
	private <Result, Body, DeserializeType> java.util.concurrent.CompletableFuture<Result> sendRequestAsyncInternal(@Nonnull final IHttpRequest request,
			@Nonnull final Class<Result> resultClass,
			@Nullable final Body serializable,
			@Nullable final IStatefulResponseHandler<Result, DeserializeType> handler)
					throws ClientException {
            final Request coreHttpRequest = getHttpRequest(request, resultClass, serializable);
            final CoreHttpCallbackFutureWrapper wrapper = new CoreHttpCallbackFutureWrapper();
            corehttpClient.newCall(coreHttpRequest).enqueue(wrapper);
            return wrapper.future.thenApply(r -> processResponse(r, request, resultClass, serializable, handler));
    }
    /**
	 * Sends the HTTP request
	 *
	 * @param request           the request description
	 * @param resultClass       the class of the response from the service
	 * @param serializable      the object to send to the service in the body of the request
	 * @param handler           the handler for stateful response
	 * @param <Result>          the type of the response object
	 * @param <Body>            the type of the object to send to the service in the body of the request
	 * @param <DeserializeType> the response handler for stateful response
	 * @return                  the result from the request
	 * @throws ClientException an exception occurs if the request was unable to complete for any reason
	 */
	@Nullable
	private <Result, Body, DeserializeType> Result sendRequestInternal(@Nonnull final IHttpRequest request,
			@Nonnull final Class<Result> resultClass,
			@Nullable final Body serializable,
			@Nullable final IStatefulResponseHandler<Result, DeserializeType> handler)
					throws ClientException {
            final Request coreHttpRequest = getHttpRequest(request, resultClass, serializable);
            try {
                final Response response = corehttpClient.newCall(coreHttpRequest).execute();
                return processResponse(response, request, resultClass, serializable, handler);
            } catch(IOException ex) {
                throw new ClientException("Error executing the request", ex);
            }
    }

    @SuppressWarnings("unchecked")
    private <Result, Body, DeserializeType> Result processResponse(final Response response,
                                                                    final IHttpRequest request,
                                                                    final Class<Result> resultClass,
                                                                    final Body serializable,
                                                                    final IStatefulResponseHandler<Result, DeserializeType> handler) {
        if (response == null) return null;
        final ResponseBody body = response.body();
        try {
            InputStream in = null;
            boolean isBinaryStreamInput = false;
            try {

                // Call being executed


                if (handler != null) {
                    handler.configConnection(response);
                }

                logger.logDebug(String.format(Locale.ROOT, "Response code %d, %s",
                        response.code(),
                        response.message()));

                if (handler != null) {
                    logger.logDebug("StatefulResponse is handling the HTTP response.");
                    return handler.generateResult(
                            request, response, this.serializer, this.logger);
                }

                if (response.code() >= HttpResponseCode.HTTP_CLIENT_ERROR && body != null) {
                    logger.logDebug("Handling error response");
                    in = body.byteStream();
                    handleErrorResponse(request, serializable, response);
                }

                final Map<String, List<String>> responseHeaders = response.headers().toMultimap();

                if (response.code() == HttpResponseCode.HTTP_NOBODY
                        || response.code() == HttpResponseCode.HTTP_NOT_MODIFIED) {
                    logger.logDebug("Handling response with no body");
                    return handleEmptyResponse(responseHeaders, resultClass);
                }

                if (response.code() == HttpResponseCode.HTTP_ACCEPTED) {
                    logger.logDebug("Handling accepted response");
                    return handleEmptyResponse(responseHeaders, resultClass);
                }

                if (body == null || body.contentLength() == 0)
                    return null;

                in = new BufferedInputStream(body.byteStream());

                final MediaType contentType = body.contentType();
                if (contentType != null && contentType.subtype().contains("json")
                    && resultClass != InputStream.class) {
                    logger.logDebug("Response json");
                    return handleJsonResponse(in, responseHeaders, resultClass);
                } else if (resultClass == InputStream.class) {
                    logger.logDebug("Response binary");
                    isBinaryStreamInput = true;
                    return (Result) handleBinaryStream(in);
                } else if (contentType != null && resultClass != InputStream.class &&
                    contentType.type().contains("text") &&
                    contentType.subtype().contains("plain")) {
                    return handleRawResponse(in, resultClass);
                } else {
                    return null;
                }
            } finally {
                if (!isBinaryStreamInput) {
                    try{
                        if (in != null) in.close();
                        if (body != null) body.close();
                    }catch(IOException e) {
                        logger.logError(e.getMessage(), e);
                    }
                    response.close();
                }
            }
        } catch (final GraphServiceException ex) {
            final boolean shouldLogVerbosely = logger.getLoggingLevel() == LoggerLevel.DEBUG;
            logger.logError("Graph service exception " + ex.getMessage(shouldLogVerbosely), ex);
            throw ex;
        } catch (final Exception ex) {
            final ClientException clientException = new ClientException("Error during http request",
                    ex);
            logger.logError("Error during http request", clientException);
            throw clientException;
        }
    }
    /**
	 * Handles the event of an error response
	 *
	 * @param request      the request that caused the failed response
	 * @param serializable the body of the request
	 * @param response     the original response object
	 * @throws IOException an exception occurs if there were any problems interacting with the connection object
	 */
	private <Body> void handleErrorResponse(final IHttpRequest request,
                                    final Body serializable,
                                    final Response response)
                                            throws IOException {
        throw GraphServiceException.createFromResponse(request, serializable, serializer,
            response, logger);
    }
    /**
	 * Handles the cause where the response is a binary stream
	 *
	 * @param in the input stream from the response
	 * @return   the input stream to return to the caller
	 */
	private InputStream handleBinaryStream(final InputStream in) {
		return in;
    }
    /**
	 * Handles the cause where the response is a JSON object
	 *
	 * @param in              the input stream from the response
	 * @param responseHeaders the response header
	 * @param clazz           the class of the response object
	 * @param <Result>        the type of the response object
	 * @return                the JSON object
	 */
	private <Result> Result handleJsonResponse(final InputStream in, Map<String, List<String>> responseHeaders, final Class<Result> clazz) {
		if (clazz == null) {
			return null;
		}

        return serializer.deserializeObject(in, clazz, responseHeaders);
    }
    /**
	 * Handles the cause where the response is a Text object
	 *
	 * @param in              the input stream from the response
	 * @param clazz           the class of the response object
	 * @param <Result>        the type of the response object
	 * @return                the Text object
	 */
    @SuppressWarnings("unchecked")
    private <Result> Result handleRawResponse(final InputStream in, final Class<Result> clazz) {
		if (clazz == null) {
			return null;
		}

        final String rawText = CoreHttpProvider.streamToString(in);
        if(clazz == Long.class) {
            try {
                return (Result) Long.valueOf(rawText);
            } catch (NumberFormatException ex) {
                return null;
            }
        } else {
            return null;
        }
    }
    /**
	 * Handles the case where the response body is empty
	 *
	 * @param responseHeaders the response headers
	 * @param clazz           the type of the response object
	 * @return                the JSON object
	 */
	private <Result> Result handleEmptyResponse(Map<String, List<String>> responseHeaders, final Class<Result> clazz)
        throws UnsupportedEncodingException{
        //Create an empty object to attach the response headers to
        Result result = null;
        try(final InputStream in = new ByteArrayInputStream("{}".getBytes(JSON_ENCODING))) {
            result = handleJsonResponse(in, responseHeaders, clazz);
        } catch (IOException ex) {
            //noop we couldnt close the byte array stream we just opened and its ok
        }
        return result;
    }

	private Request convertIHttpRequestToOkHttpRequest(IHttpRequest request) {
		if(request != null) {
			Request.Builder requestBuilder = new Request.Builder();
			requestBuilder.url(request.getRequestUrl());
			for (final HeaderOption header : request.getHeaders()) {
				requestBuilder.addHeader(header.getName(), header.getValue().toString());
			}
			return requestBuilder.build();
		}
		return null;
	}

	/**
	 * Reads in a stream and converts it into a string
	 *
	 * @param input the response body stream
	 * @return      the string result
	 */
    @Nullable
	public static String streamToString(@Nonnull final InputStream input) {
        Objects.requireNonNull(input, "parameter input cannot be null");
		final String httpStreamEncoding = "UTF-8";
		final String endOfFile = "\\A";
		try (final Scanner scanner = new Scanner(input, httpStreamEncoding)) {
			scanner.useDelimiter(endOfFile);
			if (scanner.hasNext()) {
				return scanner.next();
			}
			return "";
		}
	}

	/**
	 * Searches for the given header in a list of HeaderOptions
	 *
	 * @param headers the list of headers to search through
	 * @param header  the header name to search for (case insensitive)
	 * @return        true if the header has already been set
	 */
	@VisibleForTesting
	static boolean hasHeader(List<HeaderOption> headers, String header) {
		for (HeaderOption option : headers) {
			if (option.getName().equalsIgnoreCase(header)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Gets the logger in use
	 *
	 * @return the logger
	 */
	@VisibleForTesting
	@Nullable
	public ILogger getLogger() {
		return logger;
    }
}
