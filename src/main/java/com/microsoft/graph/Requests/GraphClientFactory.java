package com.microsoft.graph.Requests;

import com.microsoft.graph.httpcore.CompressionHandler;
import com.microsoft.graph.httpcore.GraphTelemetryHandler;
import com.microsoft.kiota.RequestInformation;
import com.microsoft.kiota.http.KiotaClientFactory;
import edu.umd.cs.findbugs.annotations.Nullable;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

import javax.annotation.Nonnull;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.Format;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * The GraphClientFactory used to create the OkHttpClient.
 */
public class GraphClientFactory {
    private GraphClientFactory() { }

    /**
     * The default OkHttpClient Builder for Graph.
     *
     * @return an OkHttpClient Builder instance.
     */
    public static OkHttpClient.Builder create() {
        return create(new GraphClientOptions());
    }

    /**
     * OkHttpClient Builder for Graph with specified Interceptors.
     *
     * @param interceptors desired interceptors for use in requests.
     * @return an OkHttpClient Builder instance.
     */
    public static OkHttpClient.Builder create(@Nonnull Interceptor[] interceptors) {
        return create(interceptors, new GraphClientOptions());
    }

    /**
     * OkHttpClient Builder for Graph with specified Interceptors and GraphClientOptions.
     *
     * @param interceptors desired interceptors for use in requests.
     * @param graphClientOptions the GraphClientOptions for use in requests.
     * @return an OkHttpClient Builder instance.
     */
    public static OkHttpClient.Builder create(@Nonnull Interceptor[] interceptors, @Nonnull GraphClientOptions graphClientOptions) {
        OkHttpClient.Builder builder = create(graphClientOptions);
        for(Interceptor interceptor : interceptors) {
            builder.addInterceptor(interceptor);
        }
        return builder;
    }

    /**
     * The OkHttpClient Builder with optional GraphClientOptions
     *
     * @param graphClientOptions the GraphClientOptions for use in requests.
     * @return an OkHttpClient Builder instance.
     */
    public static OkHttpClient.Builder create(@Nullable GraphClientOptions graphClientOptions) {
        GraphClientOptions options = graphClientOptions != null ? graphClientOptions : new GraphClientOptions();
        return KiotaClientFactory.Create(createDefaultGraphInterceptors(options));
    }

    /**
     * Creates the default Interceptors for use with Graph.
     *
     * @param graphClientOptions the GraphClientOptions used to create the GraphTelemetryHandler with.
     * @return an array of interceptors.
     */
    public static Interceptor[] createDefaultGraphInterceptors(@Nonnull GraphClientOptions graphClientOptions) {
        List<Interceptor> handlers = new ArrayList<>();
        handlers.add(new GraphTelemetryHandler(graphClientOptions));
        for(final Interceptor interceptor: KiotaClientFactory.CreateDefaultInterceptors()) {
            handlers.add(interceptor);
        }
        return (Interceptor[]) handlers.toArray();
    }
}
