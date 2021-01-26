package com.microsoft.graph.httpcore;

import java.io.IOException;
import java.util.Objects;

import javax.annotation.Nullable;
import javax.annotation.Nonnull;

import com.microsoft.graph.httpcore.middlewareoption.IShouldRetry;
import com.microsoft.graph.httpcore.middlewareoption.MiddlewareType;
import com.microsoft.graph.httpcore.middlewareoption.RetryOptions;
import com.microsoft.graph.httpcore.middlewareoption.TelemetryOptions;
import com.microsoft.graph.logger.DefaultLogger;
import com.microsoft.graph.logger.ILogger;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * The middleware responsible for retrying requests when they fail because of transient issues
 */
public class RetryHandler implements Interceptor{

    /**
     * Type of the current middleware
     */
    public final MiddlewareType MIDDLEWARE_TYPE = MiddlewareType.RETRY;

    private RetryOptions mRetryOption;

    /**
     * Header name to track the retry attempt number
     */
    private final String RETRY_ATTEMPT_HEADER = "Retry-Attempt";
    /**
     * Header name for the retry after information
     */
    private final String RETRY_AFTER = "Retry-After";
    /**
     * Header name for the transfer information
     */
    private final String TRANSFER_ENCODING = "Transfer-Encoding";
    /**
     * Chunked encoding header value
     */
    private final String TRANSFER_ENCODING_CHUNKED = "chunked";
    /**
     * Binary content type header value
     */
    private final String APPLICATION_OCTET_STREAM = "application/octet-stream";
    /**
     * Header name for the content type
     */
    private final String CONTENT_TYPE = "Content-Type";

    /**
     * Too many requests status code
     */
    public static final int MSClientErrorCodeTooManyRequests = 429;
    /**
     * Service unavailable status code
     */
    public static final int MSClientErrorCodeServiceUnavailable  = 503;
    /**
     * Gateway timeout status code
     */
    public static final int MSClientErrorCodeGatewayTimeout = 504;

    /**
     * One second as milliseconds
     */
    private final long DELAY_MILLISECONDS = 1000;

    private final ILogger logger;

    /**
     * @param retryOption Create Retry handler using retry option
     */
    public RetryHandler(@Nullable final RetryOptions retryOption) {
        this(new DefaultLogger(), retryOption);
    }
    /**
     * @param retryOption Create Retry handler using retry option
     * @param logger logger to use for telemetry
     */
    public RetryHandler(@Nonnull final ILogger logger, @Nullable final RetryOptions retryOption) {
        this.logger = Objects.requireNonNull(logger, "logger parameter cannot be null");
        this.mRetryOption = retryOption;
        if(this.mRetryOption == null) {
            this.mRetryOption = new RetryOptions();
        }
    }
    /**
     * Initialize retry handler with default retry option
     */
    public RetryHandler() {
        this(null);
    }

    boolean retryRequest(Response response, int executionCount, Request request, RetryOptions retryOptions) {

        // Should retry option
        // Use should retry common for all requests
        IShouldRetry shouldRetryCallback = null;
        if(retryOptions != null) {
            shouldRetryCallback = retryOptions.shouldRetry();
        }

        boolean shouldRetry = false;
        // Status codes 429 503 504
        int statusCode = response.code();
        // Only requests with payloads that are buffered/rewindable are supported.
        // Payloads with forward only streams will be have the responses returned
        // without any retry attempt.
        shouldRetry =
                (executionCount <= retryOptions.maxRetries())
                && checkStatus(statusCode) && isBuffered(response, request)
                && shouldRetryCallback != null
                && shouldRetryCallback.shouldRetry(retryOptions.delay(), executionCount, request, response);

        if(shouldRetry) {
            long retryInterval = getRetryAfter(response, retryOptions.delay(), executionCount);
            try {
                Thread.sleep(retryInterval);
            } catch (InterruptedException e) {
                logger.logError("error retrying the request", e);
            }
        }
        return shouldRetry;
    }

    /**
     * Get retry after in milliseconds
     * @param response Response
     * @param delay Delay in seconds
     * @param executionCount Execution count of retries
     * @return Retry interval in milliseconds
     */
    long getRetryAfter(Response response, long delay, int executionCount) {
        String retryAfterHeader = response.header(RETRY_AFTER);
        double retryDelay = RetryOptions.DEFAULT_DELAY * DELAY_MILLISECONDS;
        if(retryAfterHeader != null) {
            retryDelay = Long.parseLong(retryAfterHeader) * DELAY_MILLISECONDS;
        } else {
            retryDelay = (double)((Math.pow(2.0, (double)executionCount)-1)*0.5);
            retryDelay = (executionCount < 2 ? delay : retryDelay + delay) + (double)Math.random();
            retryDelay *= DELAY_MILLISECONDS;
        }
        return (long)Math.min(retryDelay, RetryOptions.MAX_DELAY * DELAY_MILLISECONDS);
    }

    boolean checkStatus(int statusCode) {
        return (statusCode == MSClientErrorCodeTooManyRequests || statusCode == MSClientErrorCodeServiceUnavailable
                || statusCode == MSClientErrorCodeGatewayTimeout);
    }

    boolean isBuffered(Response response, Request request) {
        String methodName = request.method();
        if(methodName.equalsIgnoreCase("GET") || methodName.equalsIgnoreCase("DELETE") || methodName.equalsIgnoreCase("HEAD") || methodName.equalsIgnoreCase("OPTIONS"))
            return true;

        boolean isHTTPMethodPutPatchOrPost = methodName.equalsIgnoreCase("POST") ||
                methodName.equalsIgnoreCase("PUT") ||
                methodName.equalsIgnoreCase("PATCH");

        if(isHTTPMethodPutPatchOrPost) {
            boolean isStream = response.header(CONTENT_TYPE)!=null && response.header(CONTENT_TYPE).equalsIgnoreCase(APPLICATION_OCTET_STREAM);
            if(!isStream) {
                String transferEncoding = response.header(TRANSFER_ENCODING);
                boolean isTransferEncodingChunked = (transferEncoding != null) &&
                        transferEncoding.equalsIgnoreCase(TRANSFER_ENCODING_CHUNKED);

                if(request.body() != null && isTransferEncodingChunked)
                    return true;
            }
        }
        return false;
    }

    @Override
    @Nullable
    public Response intercept(@Nonnull final Chain chain) throws IOException {
        Request request = chain.request();

        if(request.tag(TelemetryOptions.class) == null)
            request = request.newBuilder().tag(TelemetryOptions.class, new TelemetryOptions()).build();
        request.tag(TelemetryOptions.class).setFeatureUsage(TelemetryOptions.RETRY_HANDLER_ENABLED_FLAG);

        Response response = chain.proceed(request);

        // Use should retry pass along with this request
        RetryOptions retryOption = request.tag(RetryOptions.class);
        retryOption = retryOption != null ? retryOption : mRetryOption;

        int executionCount = 1;
        while(retryRequest(response, executionCount, request, retryOption)) {
            request = request.newBuilder().addHeader(RETRY_ATTEMPT_HEADER, String.valueOf(executionCount)).build();
            executionCount++;
            if(response != null && response.body() != null) {
                response.body().close();
            }
            response = chain.proceed(request);
        }
        return response;
    }

}
