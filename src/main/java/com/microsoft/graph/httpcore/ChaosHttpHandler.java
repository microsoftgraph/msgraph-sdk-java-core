package com.microsoft.graph.httpcore;

import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

import com.microsoft.graph.httpcore.middlewareoption.MiddlewareType;
import com.microsoft.graph.httpcore.middlewareoption.TelemetryOptions;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class ChaosHttpHandler implements Interceptor {
	
	public final MiddlewareType MIDDLEWARE_TYPE = MiddlewareType.RETRY;

    /*
     * constant string being used
     */
    private final String RETRY_AFTER = "Retry-After";
    
    public static final int MSClientErrorCodeTooManyRequests = 429;
    
	@Override
	public Response intercept(Chain chain) throws IOException {
		Request request = chain.request();
		
		if(request.tag(TelemetryOptions.class) == null)
			request = request.newBuilder().tag(TelemetryOptions.class, new TelemetryOptions()).build();
		request.tag(TelemetryOptions.class).setFeatureUsage(TelemetryOptions.RETRY_HANDLER_ENABLED_FLAG);
        
        final Integer dice = ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE);

        if(dice % 3 == 0) {
            return new Response
                        .Builder()
                        .request(request)
                        .protocol(Protocol.HTTP_1_1)
                        .code(MSClientErrorCodeTooManyRequests)
                        .message("Too Many Requests")
                        .addHeader(RETRY_AFTER, "10")
                        .body(ResponseBody.create(MediaType.get("application/json"), "{\"error\": {\"code\": \"TooManyRequests\",\"innerError\": {\"code\": \"429\",\"date\": \"2020-08-18T12:51:51\",\"message\": \"Please retry after\",\"request-id\": \"94fb3b52-452a-4535-a601-69e0a90e3aa2\",\"status\": \"429\"},\"message\": \"Please retry again later.\"}}"))
                        .build();
        } else {
            return chain.proceed(request);
        }
	}

}