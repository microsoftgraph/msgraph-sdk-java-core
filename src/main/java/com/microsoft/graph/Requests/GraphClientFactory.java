package com.microsoft.graph.Requests;

import com.microsoft.graph.Requests.Middleware.GraphTelemetryHandler;
import com.microsoft.kiota.http.KiotaClientFactory;
import edu.umd.cs.findbugs.annotations.Nullable;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

import javax.annotation.Nonnull;
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
        return create(new GraphClientOption());
    }
    /**
     * OkHttpClient Builder for Graph with specified Interceptors.
     *
     * @param interceptors desired interceptors for use in requests.
     * @return an OkHttpClient Builder instance.
     */
    public static OkHttpClient.Builder create(@Nonnull Interceptor... interceptors) {
        return create(new GraphClientOption(), interceptors);
    }
    /**
     * OkHttpClient Builder for Graph with specified Interceptors and GraphClientOption.
     *
     * @param interceptors desired interceptors for use in requests.
     * @param graphClientOption the GraphClientOption for use in requests.
     * @return an OkHttpClient Builder instance.
     */
    public static OkHttpClient.Builder create(@Nonnull GraphClientOption graphClientOption, @Nonnull Interceptor... interceptors) {
        OkHttpClient.Builder builder = create(graphClientOption);
        //Skip adding interceptor if that class of interceptor already exist.
        List<String> appliedInterceptors = new ArrayList<String>();
        for(Interceptor interceptor: builder.interceptors()) {
            appliedInterceptors.add(interceptor.getClass().toString());
        }
        for (Interceptor interceptor:interceptors){
            if(appliedInterceptors.contains(interceptor.getClass().toString())) {
                continue;
            }
            builder.addInterceptor(interceptor);
        }
        return builder;
    }
    /**
     * The OkHttpClient Builder with optional GraphClientOption
     *
     * @param graphClientOption the GraphClientOption for use in requests.
     * @return an OkHttpClient Builder instance.
     */
    public static OkHttpClient.Builder create(@Nullable GraphClientOption graphClientOption) {
        GraphClientOption options = graphClientOption != null ? graphClientOption : new GraphClientOption();
        return KiotaClientFactory.Create(createDefaultGraphInterceptors(options));
    }
    /**
     * Creates the default Interceptors for use with Graph.
     *
     * @param graphClientOption the GraphClientOption used to create the GraphTelemetryHandler with.
     * @return an array of interceptors.
     */
    public static Interceptor[] createDefaultGraphInterceptors(@Nonnull GraphClientOption graphClientOption) {
        List<Interceptor> handlers = new ArrayList<>();
        addDefaultFeatureUsages(graphClientOption);
        handlers.add(new GraphTelemetryHandler(graphClientOption));
        for(final Interceptor interceptor: KiotaClientFactory.CreateDefaultInterceptors()) {
            handlers.add(interceptor);
        }
        return handlers.toArray(new Interceptor[handlers.size()]);
    }
    //These are the default features used by the Graph Client
    private static void addDefaultFeatureUsages(GraphClientOption graphClientOption) {
        graphClientOption.featureTracker.setFeatureUsage(FeatureFlag.RETRY_HANDLER_FLAG, FeatureFlag.REDIRECT_HANDLER_FLAG);
    }
}
