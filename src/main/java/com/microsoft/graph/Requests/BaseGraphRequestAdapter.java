package com.microsoft.graph.Requests;

import com.fasterxml.jackson.databind.ser.Serializers;
import com.microsoft.graph.core.ClientException;
import com.microsoft.kiota.authentication.AnonymousAuthenticationProvider;
import com.microsoft.kiota.authentication.AuthenticationProvider;
import com.microsoft.kiota.http.OkHttpRequestAdapter;
import com.microsoft.kiota.serialization.ParseNodeFactory;
import com.microsoft.kiota.serialization.SerializationWriterFactory;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import okhttp3.OkHttpClient;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.net.URL;
import java.util.HashMap;

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
    private static final HashMap<Clouds, String> cloudList = new HashMap<Clouds, String>() {{
        put( Clouds.GLOBAL_CLOUD, "https://graph.microsoft.com" );
        put( Clouds.USGOV_CLOUD, "https://graph.microsoft.us");
        put( Clouds.CHINA_CLOUD, "https://microsoftgraph.chinacloudapi.cn");
        put( Clouds.GERMANY_CLOUD, "https://graph.microsoft.de");
        put( Clouds.USGOV_DOD_CLOUD, "https://dod-graph.microsoft.us");
    }};


    /**
     * The default BaseGraphRequestAdapter constructor, includes all configurable properties.
     * Note: GraphClientOptions will be ignored if you also choose to include an OKHttpClient.
     *
     * @param authenticationProvider the AuthenticationProvider for use in requests.
     * @param parseNodeFactory the ParseNodeFactory for use in requests.
     * @param serializationWriterFactory the SerializationWriterFactory for use in requests.
     * @param client the OkHttpClient for use in requests.
     * @param graphClientOptions the GraphClientOptions for use in requests.
     * @param baseUrl the base URL for use in requests.
     */
    public BaseGraphRequestAdapter(@Nonnull final AuthenticationProvider authenticationProvider, @Nullable final ParseNodeFactory parseNodeFactory, @Nullable final SerializationWriterFactory serializationWriterFactory, @Nullable final OkHttpClient client, @Nullable final GraphClientOptions graphClientOptions, @Nullable String baseUrl) {
        super(authenticationProvider, parseNodeFactory, serializationWriterFactory, client != null ? client : GraphClientFactory.create(graphClientOptions).build());
        if (baseUrl != null && !baseUrl.isEmpty()) {
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
    public BaseGraphRequestAdapter(@Nonnull AuthenticationProvider authenticationProvider, @Nonnull String baseUrl) {
        this(authenticationProvider, baseUrl, new GraphClientOptions());
    }

    /**
     * Constructor requiring an AuthenticationProvider, base URL, and an OkHttpClient.
     *
     * @param authenticationProvider the AuthenticationProvider for use in requests.
     * @param baseUrl the base URL for use in requests.
     * @param client the OkHttpClient for use in requests.
     */
    public BaseGraphRequestAdapter(@Nonnull AuthenticationProvider authenticationProvider, @Nonnull String baseUrl, @Nonnull OkHttpClient client) {
        this(authenticationProvider, null, null, client, null, baseUrl);
    }

    /**
     * Constructor requiring an AuthenticationProvider, base URL, and GraphClientOptions.
     *
     * @param authenticationProvider the AuthenticationProvider for use in requests.
     * @param baseUrl the base URL for use in requests.
     * @param graphClientOptions the GraphClientOptions for use in requests.
     */
    public BaseGraphRequestAdapter(@Nonnull AuthenticationProvider authenticationProvider, @Nonnull String baseUrl, @Nonnull GraphClientOptions graphClientOptions) {
        this(authenticationProvider, null, null, null, graphClientOptions, baseUrl);
    }

    /**
     * Constructor requiring an AuthenticationProvider, optional National Cloud, and optional Version.
     *
     * @param authenticationProvider the AuthenticationProvider for use in requests.
     * @param cloud the National Cloud for use in requests.
     * @param version the Graph version for use in requests.
     */
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
    public BaseGraphRequestAdapter(@Nonnull AuthenticationProvider authenticationProvider, @Nullable Clouds cloud, @Nullable String version, @Nonnull OkHttpClient client) {
        this(authenticationProvider, determineBaseAddress(cloud, version), client);
    }

    /**
     * Constructor requiring an AuthenticationProvider, optional National Cloud, optional Version, and required GraphClientOptions.
     *
     * @param authenticationProvider the AuthenticationProvider for use in requests.
     * @param cloud the National Cloud for use in requests.
     * @param version the Graph version for use in requests.
     * @param graphClientOptions the GraphClientOptions for use in requests.
     */
    public BaseGraphRequestAdapter(@Nonnull AuthenticationProvider authenticationProvider, @Nullable Clouds cloud, @Nullable String version, @Nonnull GraphClientOptions graphClientOptions) {
        this(authenticationProvider, determineBaseAddress(cloud, version), graphClientOptions);
    }

    private static String determineBaseAddress(@Nullable Clouds nationalCloud, @Nullable String version) throws IllegalArgumentException {
        String cloud = nationalCloud == null ? cloudList.get(Clouds.GLOBAL_CLOUD) : cloudList.get(nationalCloud);
        if(cloud == null) {
            throw new IllegalArgumentException(String.format("%s is an unexpected national cloud.", nationalCloud));
        }
        String baseAddress = version == null ? String.format("%s/%s/",cloud,"v1.0") : String.format("%s/%s/",cloud,version);
        return baseAddress;
    }
}
