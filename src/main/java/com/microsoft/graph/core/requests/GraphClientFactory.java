package com.microsoft.graph.core.requests;

import com.microsoft.graph.core.CoreConstants;
import com.microsoft.graph.core.requests.middleware.GraphTelemetryHandler;
import com.microsoft.graph.core.requests.options.GraphClientOption;
import com.microsoft.kiota.RequestOption;
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
    public static OkHttpClient.Builder create(@Nonnull final Interceptor... interceptors) {
        return create(new GraphClientOption(), interceptors);
    }

    /**
     * OkHttpClient Builder for Graph with specified Interceptors
     * @param interceptors desired interceptors for use in requests.
     * @return an OkHttpClient Builder instance.
     */
    @Nonnull
    public static OkHttpClient.Builder create(@Nonnull final List<Interceptor> interceptors) {
        return create(new GraphClientOption(), interceptors.toArray(new Interceptor[0]));
    }

    /**
     * OkHttpClient Builder for Graph with specified AuthenticationProvider.
     * Adds an AuthorizationHandler to the OkHttpClient Builder.
     * @param authenticationProvider the AuthenticationProvider to use for requests.
     * @return an OkHttpClient Builder instance.
     */
    @Nonnull
    public static OkHttpClient.Builder create(@Nonnull final BaseBearerTokenAuthenticationProvider authenticationProvider) {
        return create(authenticationProvider, new RequestOption[0]);
    }

    /**
     * OkHttpClient Builder for Graph with specified AuthenticationProvider and RequestOptions to override default graph interceptors
     * @param authenticationProvider the AuthenticationProvider to use for requests.
     * @param requestOptions custom request options to override default graph interceptors
     * @return an OkHttpClient Builder instance.
     */
    @Nonnull
    public static OkHttpClient.Builder create(@Nonnull final BaseBearerTokenAuthenticationProvider authenticationProvider, @Nonnull final RequestOption[] requestOptions) {
        final GraphClientOption graphClientOption = new GraphClientOption();
        final Interceptor[] interceptors = createDefaultGraphInterceptors(graphClientOption, requestOptions);
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
    public static OkHttpClient.Builder create(@Nonnull final GraphClientOption graphClientOption, @Nonnull final Interceptor... interceptors) {
        final OkHttpClient.Builder builder = KiotaClientFactory.create(interceptors);
        final List<Interceptor> customInterceptors = builder.interceptors();
        final boolean telemetryHandlerExists = customInterceptors.stream().anyMatch(x -> x instanceof GraphTelemetryHandler);
        if (!telemetryHandlerExists) {
            customInterceptors.add(new GraphTelemetryHandler(graphClientOption));
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
    public static OkHttpClient.Builder create(@Nonnull final GraphClientOption graphClientOption, @Nonnull final List<Interceptor> interceptors) {
        return create(graphClientOption, interceptors.toArray(new Interceptor[0]));
    }

    /**
     * The OkHttpClient Builder with optional GraphClientOption
     *
     * @param graphClientOption the GraphClientOption for use in requests.
     * @return an OkHttpClient Builder instance.
     */
    @Nonnull
    public static OkHttpClient.Builder create(@Nullable final GraphClientOption graphClientOption) {
        return create(graphClientOption, new RequestOption[0]);
    }

    /**
     * The OkHttpClient Builder with optional GraphClientOption and RequestOptions to override default graph interceptors
     * @param graphClientOption the GraphClientOption for use in requests.
     * @param requestOptions custom request options to override default graph interceptors
     * @return an OkHttpClient Builder instance.
     */
    @Nonnull
    public static OkHttpClient.Builder create(@Nullable final GraphClientOption graphClientOption, @Nonnull final RequestOption[] requestOptions) {
        GraphClientOption options = graphClientOption != null ? graphClientOption : new GraphClientOption();
        return KiotaClientFactory.create(createDefaultGraphInterceptors(options, requestOptions));
    }

    /**
     * Creates the default Interceptors for use with Graph.
     *
     * @param graphClientOption the GraphClientOption used to create the GraphTelemetryHandler with.
     * @return an array of interceptors.
     */
    @Nonnull
    public static Interceptor[] createDefaultGraphInterceptors(@Nonnull final GraphClientOption graphClientOption) {
        return createDefaultGraphInterceptors(graphClientOption, new RequestOption[0]);
    }

    /**
     * Creates the default Interceptors for use with Graph configured with the provided RequestOptions.
     * @param graphClientOption the GraphClientOption used to create the GraphTelemetryHandler with.
     * @param requestOptions custom request options to override default graph interceptors
     * @return an array of interceptors.
     */
    @Nonnull
    public static Interceptor[] createDefaultGraphInterceptors(@Nonnull final GraphClientOption graphClientOption, @Nonnull final RequestOption[] requestOptions) {
        Objects.requireNonNull(requestOptions, "parameter requestOptions cannot be null");

        UrlReplaceHandlerOption urlReplaceHandlerOption = new UrlReplaceHandlerOption(CoreConstants.ReplacementConstants.getDefaultReplacementPairs());

        for (RequestOption option : requestOptions) {
            if (option instanceof UrlReplaceHandlerOption) {
                urlReplaceHandlerOption = (UrlReplaceHandlerOption) option;
            }
        }

        List<Interceptor> handlers = new ArrayList<>();
        handlers.add(new UrlReplaceHandler(urlReplaceHandlerOption));
        handlers.add(new GraphTelemetryHandler(graphClientOption));
        handlers.addAll(Arrays.asList(KiotaClientFactory.createDefaultInterceptors(requestOptions)));
        addDefaultFeatureUsages(graphClientOption);
        return handlers.toArray(new Interceptor[0]);
    }

    //These are the default features used by the Graph Client
    private static void addDefaultFeatureUsages(GraphClientOption graphClientOption) {
        graphClientOption.featureTracker.setFeatureUsage(FeatureFlag.RETRY_HANDLER_FLAG);
        graphClientOption.featureTracker.setFeatureUsage(FeatureFlag.REDIRECT_HANDLER_FLAG);
        graphClientOption.featureTracker.setFeatureUsage(FeatureFlag.URL_REPLACEMENT_FLAG);
    }
}
