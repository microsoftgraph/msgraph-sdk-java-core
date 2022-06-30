package com.microsoft.graph.Requests;

import com.microsoft.graph.httpcore.CompressionHandler;
import com.microsoft.graph.httpcore.GraphTelemetryHandler;
import com.microsoft.kiota.RequestInformation;
import com.microsoft.kiota.http.KiotaClientFactory;
import edu.umd.cs.findbugs.annotations.Nullable;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.Format;
import java.time.temporal.ChronoUnit;
import java.util.*;

@SuppressFBWarnings
public class GraphClientFactory {
    private GraphClientFactory() { }

    public static OkHttpClient.Builder create() {
        return create((GraphClientOptions) null);
    }

    public static OkHttpClient.Builder create(Interceptor[] interceptors, @Nullable GraphClientOptions graphClientOptions) {
        OkHttpClient.Builder builder = create((GraphClientOptions) null);
        for(Interceptor interceptor : interceptors) {
            builder.addInterceptor(interceptor);
        }
        return builder;
    }

    public static OkHttpClient.Builder create(@Nullable GraphClientOptions options) {
        OkHttpClient.Builder builder = KiotaClientFactory.Create(createDefaultGraphInterceptors(options));
        return builder;
    }

    public static Interceptor[] createDefaultGraphInterceptors(@Nullable GraphClientOptions graphClientOptions) {
        List<Interceptor> handlers = new ArrayList<>();
        handlers.add(new GraphTelemetryHandler(graphClientOptions));
        for(final Interceptor interceptor: KiotaClientFactory.CreateDefaultInterceptors()) {
            handlers.add(interceptor);
        }
        return (Interceptor[]) handlers.toArray();
    }
}
