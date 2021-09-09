package com.microsoft.graph.httpcore;

import com.microsoft.graph.authentication.IAuthenticationProvider;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.Protocol;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Objects;

/**
 * Builder to get a custom HttpClient to be used for requests against Microsoft Graph
 */
public class HttpClients {
    private HttpClients() {
        super();
    }

    /**
     * Creates builder object for construction of custom
     * {@link OkHttpClient} instances.
     *
     * @return OkHttpClient.Builder() custom builder for developer to add its own interceptors to it
     */
    @Nonnull
    public static Builder custom() {
        return new OkHttpClient.Builder()
                    .addInterceptor(new TelemetryHandler())
                    .followRedirects(false)
                    .followSslRedirects(false)
                    .protocols(Collections.singletonList(Protocol.HTTP_1_1)); //https://stackoverflow.com/questions/62031298/sockettimeout-on-java-11-but-not-on-java-8
    }

    /**
     * Creates {@link OkHttpClient} instance with default
     * configuration and provided authProvider
     *
     * @param auth Use IAuthenticationProvider instance provided while constructing http client
     * @return OkHttpClient build with authentication provider given, default redirect and default retry handlers
     */
    @Nonnull
    public static OkHttpClient createDefault(@Nonnull final IAuthenticationProvider auth) {
        Objects.requireNonNull(auth, "parameter auth cannot be null");
        return custom()
                .addInterceptor(new AuthenticationHandler(auth))
                .addInterceptor(new RetryHandler())
                .addInterceptor(new RedirectHandler())
                .build();
    }

    /**
     * Creates {@link OkHttpClient} instance with interceptors
     *
     * @param interceptors Use interceptors provided while constructing http client
     * @return OkHttpClient build with interceptors provided
     */
    @Nonnull
    public static OkHttpClient createFromInterceptors(@Nullable final Interceptor[] interceptors) {
        OkHttpClient.Builder builder = custom();
        if(interceptors != null)
            for(Interceptor interceptor : interceptors) {
                if(interceptor != null)
                    builder.addInterceptor(interceptor);
            }
        return builder.build();
    }
}
