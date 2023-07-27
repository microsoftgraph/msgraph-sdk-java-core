package com.microsoft.graph.requests;

import com.google.common.base.Strings;
import com.microsoft.graph.requests.options.GraphClientOption;
import com.microsoft.kiota.authentication.AuthenticationProvider;
import com.microsoft.kiota.http.OkHttpRequestAdapter;
import com.microsoft.kiota.serialization.ParseNodeFactory;
import com.microsoft.kiota.serialization.SerializationWriterFactory;

import okhttp3.OkHttpClient;


import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.util.EnumMap;

/**
 * Extension of the OkHttpRequestAdapter which is used as the default for Graph Requests.
 */
public class BaseGraphRequestAdapter extends OkHttpRequestAdapter {

    /**
     * Enum list of Keys which can be used to access the cloudList map.
     */
    public enum Clouds {
        /** Key for Global Cloud URL */
        GLOBAL_CLOUD,
        /** Key for US GOV Cloud URL */
        USGOV_CLOUD,
        /** Key for China Cloud URL */
        CHINA_CLOUD,
        /** Key for Germany Cloud URL */
        GERMANY_CLOUD,
        /** Key for US DOD Cloud URL */
        USGOV_DOD_CLOUD
    }

    /**
     * Map of valid cloud urls for use in Graph requests.
     * Accessible using a Clouds enum value.
     */
    private static final EnumMap<Clouds, String> getCloudList() {
        EnumMap<Clouds, String> cloudList = new EnumMap<>(Clouds.class);
        cloudList.put( Clouds.GLOBAL_CLOUD, "https://graph.microsoft.com" );
        cloudList.put( Clouds.USGOV_CLOUD, "https://graph.microsoft.us");
        cloudList.put( Clouds.CHINA_CLOUD, "https://microsoftgraph.chinacloudapi.cn");
        cloudList.put( Clouds.GERMANY_CLOUD, "https://graph.microsoft.de");
        cloudList.put( Clouds.USGOV_DOD_CLOUD, "https://dod-graph.microsoft.us");
        return cloudList;
    }

    /**
     * The default BaseGraphRequestAdapter constructor, includes all configurable properties.
     * Note: GraphClientOption will be ignored if you also choose to include an OKHttpClient.
     *
     * @param authenticationProvider the AuthenticationProvider for use in requests.
     * @param parseNodeFactory the ParseNodeFactory for use in requests.
     * @param serializationWriterFactory the SerializationWriterFactory for use in requests.
     * @param client the OkHttpClient for use in requests.
     * @param graphClientOption the GraphClientOption for use in requests.
     * @param baseUrl the base URL for use in requests.
     */
    @SuppressWarnings("LambdaLast")
    public BaseGraphRequestAdapter(@Nonnull final AuthenticationProvider authenticationProvider, @Nullable final ParseNodeFactory parseNodeFactory, @Nullable final SerializationWriterFactory serializationWriterFactory, @Nullable final OkHttpClient client, @Nullable final GraphClientOption graphClientOption, @Nullable String baseUrl) {
        super(authenticationProvider, parseNodeFactory, serializationWriterFactory, client != null ? client : GraphClientFactory.create(graphClientOption).build());
        if (!Strings.isNullOrEmpty(baseUrl)) {
            setBaseUrl(baseUrl);
        } else {
            setBaseUrl(determineBaseAddress(null, null));
        }
    }

    /**
     * Constructor requiring only an AuthenticationProvider
     *
     * @param authenticationProvider the AuthenticationProvider for use in requests.
     */
    public BaseGraphRequestAdapter(@Nonnull AuthenticationProvider authenticationProvider) {
        this(authenticationProvider, determineBaseAddress(null, null));
    }

    /**
     * Constructor requiring an AuthenticationProvider and a base URL.
     *
     * @param authenticationProvider the AuthenticationProvider for use in requests.
     * @param baseUrl the base URL for use in requests.
     */
    @SuppressWarnings("LambdaLast")
    public BaseGraphRequestAdapter(@Nonnull AuthenticationProvider authenticationProvider, @Nonnull String baseUrl) {
        this(authenticationProvider, baseUrl, new GraphClientOption());
    }

    /**
     * Constructor requiring an AuthenticationProvider, base URL, and an OkHttpClient.
     *
     * @param authenticationProvider the AuthenticationProvider for use in requests.
     * @param baseUrl the base URL for use in requests.
     * @param client the OkHttpClient for use in requests.
     */
    @SuppressWarnings("LambdaLast")
    public BaseGraphRequestAdapter(@Nonnull AuthenticationProvider authenticationProvider, @Nonnull String baseUrl, @Nonnull OkHttpClient client) {
        this(authenticationProvider, null, null, client, null, baseUrl);
    }

    /**
     * Constructor requiring an AuthenticationProvider, base URL, and GraphClientOption.
     *
     * @param authenticationProvider the AuthenticationProvider for use in requests.
     * @param baseUrl the base URL for use in requests.
     * @param graphClientOption the GraphClientOption for use in requests.
     */
    @SuppressWarnings("LambdaLast")
    public BaseGraphRequestAdapter(@Nonnull AuthenticationProvider authenticationProvider, @Nonnull String baseUrl, @Nonnull GraphClientOption graphClientOption) {
        this(authenticationProvider, null, null, null, graphClientOption, baseUrl);
    }

    /**
     * Constructor requiring an AuthenticationProvider, optional National Cloud, and optional Version.
     *
     * @param authenticationProvider the AuthenticationProvider for use in requests.
     * @param cloud the National Cloud for use in requests.
     * @param version the Graph version for use in requests.
     */
    @SuppressWarnings("LambdaLast")
    public BaseGraphRequestAdapter(@Nonnull AuthenticationProvider authenticationProvider, @Nullable Clouds cloud, @Nullable String version) {
        this(authenticationProvider, determineBaseAddress(cloud, version));
    }

    /**
     * Constructor requiring an AuthenticationProvider, optional National Cloud, optional Version, and required OkHttpClient.
     *
     * @param authenticationProvider the AuthenticationProvider for use in requests.
     * @param cloud the National Cloud for use in requests.
     * @param version the Graph version for use in requests.
     * @param client the OkHttpClient for use in requests.
     */
    @SuppressWarnings("LambdaLast")
    public BaseGraphRequestAdapter(@Nonnull AuthenticationProvider authenticationProvider, @Nullable Clouds cloud, @Nullable String version, @Nonnull OkHttpClient client) {
        this(authenticationProvider, determineBaseAddress(cloud, version), client);
    }

    /**
     * Constructor requiring an AuthenticationProvider, optional National Cloud, optional Version, and required GraphClientOption.
     *
     * @param authenticationProvider the AuthenticationProvider for use in requests.
     * @param cloud the National Cloud for use in requests.
     * @param version the Graph version for use in requests.
     * @param graphClientOption the GraphClientOption for use in requests.
     */
    @SuppressWarnings("LambdaLast")
    public BaseGraphRequestAdapter(@Nonnull AuthenticationProvider authenticationProvider, @Nullable Clouds cloud, @Nullable String version, @Nonnull GraphClientOption graphClientOption) {
        this(authenticationProvider, determineBaseAddress(cloud, version), graphClientOption);
    }

    private static String determineBaseAddress(@Nullable Clouds nationalCloud, @Nullable String version) throws IllegalArgumentException {
        String cloud = nationalCloud == null ? getCloudList().get(Clouds.GLOBAL_CLOUD) : getCloudList().get(nationalCloud);
        if(cloud == null) {
            throw new IllegalArgumentException(nationalCloud+" is an unexpected national cloud.");
        }
        return version == null ? (cloud+"/v1.0") : (cloud+"/"+version);
    }
}
