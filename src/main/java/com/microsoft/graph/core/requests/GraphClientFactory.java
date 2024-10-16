package com.microsoft.graph.core.requests;

import com.microsoft.graph.core.CoreConstants;
import com.microsoft.graph.core.requests.middleware.GraphTelemetryHandler;
import com.microsoft.graph.core.requests.options.GraphClientOption;
import com.microsoft.kiota.authentication.BaseBearerTokenAuthenticationProvider;
import com.microsoft.kiota.http.KiotaClientFactory;
import com.microsoft.kiota.http.middleware.AuthorizationHandler;
import com.microsoft.kiota.http.middleware.UrlReplaceHandler;
import com.microsoft.kiota.http.middleware.options.UrlReplaceHandlerOption;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
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
    @Nonnull
    public static OkHttpClient.Builder create() {
        return create(new GraphClientOption());
    }
    /**
     * OkHttpClient Builder for Graph with specified Interceptors.
     *
     * @param interceptors desired interceptors for use in requests.
     * @return an OkHttpClient Builder instance.
     */
    @Nonnull
    public static OkHttpClient.Builder create(@Nonnull Interceptor... interceptors) {
        return create(new GraphClientOption(), interceptors);
    }

    /**
     * OkHttpClient Builder for Graph with specified Interceptors
     * @param interceptors desired interceptors for use in requests.
     * @return an OkHttpClient Builder instance.
     */
    @Nonnull
    public static OkHttpClient.Builder create(@Nonnull List<Interceptor> interceptors) {
        return create(new GraphClientOption(), interceptors.toArray(new Interceptor[0]));
    }

    /**
     * OkHttpClient Builder for Graph with specified AuthenticationProvider.
     * Adds an AuthorizationHandler to the OkHttpClient Builder.
     * @param authenticationProvider the AuthenticationProvider to use for requests.
     * @return an OkHttpClient Builder instance.
     */
    @Nonnull
    public static OkHttpClient.Builder create(@Nonnull BaseBearerTokenAuthenticationProvider authenticationProvider) {
        final GraphClientOption graphClientOption = new GraphClientOption();
        final Interceptor[] interceptors = createDefaultGraphInterceptors(graphClientOption);
        final ArrayList<Interceptor> interceptorList = new ArrayList<>(Arrays.asList(interceptors));
        interceptorList.add(new AuthorizationHandler(authenticationProvider));
        graphClientOption.featureTracker.setFeatureUsage(FeatureFlag.AUTH_HANDLER_FLAG);
        return create(graphClientOption, interceptorList);
    }

    /**
     * OkHttpClient Builder for Graph with specified Interceptors and GraphClientOption.
     *
     * @param interceptors desired interceptors for use in requests.
     * @param graphClientOption the GraphClientOption for use in requests.
     * @return an OkHttpClient Builder instance.
     */
    @Nonnull
    public static OkHttpClient.Builder create(@Nonnull GraphClientOption graphClientOption, @Nonnull Interceptor... interceptors) {
        final OkHttpClient.Builder builder = KiotaClientFactory.create(interceptors);
        //Skip adding interceptor if that class of interceptor already exist.
        final List<String> appliedInterceptors = new ArrayList<>();
        for(Interceptor interceptor: builder.interceptors()) {
            appliedInterceptors.add(interceptor.getClass().toString());
        }
        for (Interceptor interceptor:createDefaultGraphInterceptors(graphClientOption)){
            if(appliedInterceptors.contains(interceptor.getClass().toString())) {
                continue;
            }
            builder.addInterceptor(interceptor);
        }
        return builder;
    }

    /**
     * OkHttpClient Builder for Graph with specified Interceptors and GraphClientOption.
     * @param graphClientOption the GraphClientOption for use in requests.
     * @param interceptors desired interceptors for use in requests.
     * @return an OkHttpClient Builder instance.
     */
    @Nonnull
    public static OkHttpClient.Builder create(@Nonnull GraphClientOption graphClientOption, @Nonnull List<Interceptor> interceptors) {
        return create(graphClientOption, interceptors.toArray(new Interceptor[0]));
    }

    /**
     * The OkHttpClient Builder with optional GraphClientOption
     *
     * @param graphClientOption the GraphClientOption for use in requests.
     * @return an OkHttpClient Builder instance.
     */
    @Nonnull
    public static OkHttpClient.Builder create(@Nullable GraphClientOption graphClientOption) {
        GraphClientOption options = graphClientOption != null ? graphClientOption : new GraphClientOption();
        return KiotaClientFactory.create(createDefaultGraphInterceptors(options));
    }
    /**
     * Creates the default Interceptors for use with Graph.
     *
     * @param graphClientOption the GraphClientOption used to create the GraphTelemetryHandler with.
     * @return an array of interceptors.
     */
    @Nonnull
    public static Interceptor[] createDefaultGraphInterceptors(@Nonnull GraphClientOption graphClientOption) {
        List<Interceptor> handlers = new ArrayList<>();
        addDefaultFeatureUsages(graphClientOption);

        handlers.add(new UrlReplaceHandler(new UrlReplaceHandlerOption(CoreConstants.ReplacementConstants.getDefaultReplacementPairs())));
        handlers.add(new GraphTelemetryHandler(graphClientOption));
        handlers.addAll(Arrays.asList(KiotaClientFactory.createDefaultInterceptors()));
        return handlers.toArray(new Interceptor[0]);
    }
    //These are the default features used by the Graph Client
    private static void addDefaultFeatureUsages(GraphClientOption graphClientOption) {
        graphClientOption.featureTracker.setFeatureUsage(FeatureFlag.RETRY_HANDLER_FLAG);
        graphClientOption.featureTracker.setFeatureUsage(FeatureFlag.REDIRECT_HANDLER_FLAG);
        graphClientOption.featureTracker.setFeatureUsage(FeatureFlag.URL_REPLACEMENT_FLAG);
    }
}
