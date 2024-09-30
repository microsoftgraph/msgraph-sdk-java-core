package com.microsoft.graph.core.requests;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

class MockResponseHandler implements Interceptor {

    private int statusCode;

    public MockResponseHandler(int statusCode) {
        this.statusCode = statusCode;
    }

    public MockResponseHandler() {
        this.statusCode = 200;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        final var request = chain.request();
        final var requestBody = request.body();
        if (request != null && requestBody != null) {
            final var buffer = new Buffer();
            requestBody.writeTo(buffer);
            return new Response.Builder()
                    .code(this.statusCode)
                    .message("OK")
                    .protocol(Protocol.HTTP_1_1)
                    .request(request)
                    .body(
                            ResponseBody.create(
                                    buffer.readByteArray(), MediaType.parse("application/json")))
                    .build();
        }
        return new Response.Builder()
                .code(this.statusCode)
                .message("OK")
                .protocol(Protocol.HTTP_1_1)
                .request(request)
                .body(ResponseBody.create("", MediaType.parse("application/json")))
                .build();
    }

}
