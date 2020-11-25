package com.microsoft.graph.httpcore;

import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

import javax.annotation.Nullable;
import javax.annotation.Nonnull;

import com.microsoft.graph.httpcore.middlewareoption.MiddlewareType;
import com.microsoft.graph.httpcore.middlewareoption.TelemetryOptions;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * DO NOT USE IN PRODUCTION
 * interceptor that randomly fails the responses for unit testing purposes
 */
public class ChaosHttpHandler implements Interceptor {
	public final MiddlewareType MIDDLEWARE_TYPE = MiddlewareType.RETRY;
	/*
	 * constant string being used
	 */
	private final String RETRY_AFTER = "Retry-After";
	/**
	 * Denominator for the failure rate (i.e. 1/X)
	 */
	private final Integer failureRate = 3;
	/**
	 * default value to return on retry after
	 */
	private final String retryAfterValue = "10";
	/**
	 * body to respond on failed requests
	 */
	private final String responseBody = "{\"error\": {\"code\": \"TooManyRequests\",\"innerError\": {\"code\": \"429\",\"date\": \"2020-08-18T12:51:51\",\"message\": \"Please retry after\",\"request-id\": \"94fb3b52-452a-4535-a601-69e0a90e3aa2\",\"status\": \"429\"},\"message\": \"Please retry again later.\"}}";
	public static final int MSClientErrorCodeTooManyRequests = 429;
	
	@Override
	@Nullable
	public Response intercept(@Nonnull final Chain chain) throws IOException {
		Request request = chain.request();
		
		if(request.tag(TelemetryOptions.class) == null)
			request = request.newBuilder().tag(TelemetryOptions.class, new TelemetryOptions()).build();
		request.tag(TelemetryOptions.class).setFeatureUsage(TelemetryOptions.RETRY_HANDLER_ENABLED_FLAG);
		
		final Integer dice = ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE);

		if(dice % failureRate == 0) {
			return new Response
						.Builder()
						.request(request)
						.protocol(Protocol.HTTP_1_1)
						.code(MSClientErrorCodeTooManyRequests)
						.message("Too Many Requests")
						.addHeader(RETRY_AFTER, retryAfterValue)
						.body(ResponseBody.create(MediaType.get("application/json"), responseBody))
						.build();
		} else {
			return chain.proceed(request);
		}
	}

}