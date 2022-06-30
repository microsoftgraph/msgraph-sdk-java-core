package com.microsoft.graph.Requests;

import com.microsoft.kiota.authentication.AnonymousAuthenticationProvider;
import com.microsoft.kiota.authentication.AuthenticationProvider;
import com.microsoft.kiota.http.OkHttpRequestAdapter;
import com.microsoft.kiota.serialization.ParseNodeFactory;
import com.microsoft.kiota.serialization.SerializationWriterFactory;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import okhttp3.OkHttpClient;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.HashMap;

@SuppressFBWarnings
public class BaseGraphRequestAdapter extends OkHttpRequestAdapter {

    public enum Clouds {
        GLOBAL_CLOUD,
        USGOV_CLOUD,
        CHINA_CLOUD,
        GERMANY_CLOUD,
        USGOV_DOD_CLOUD
    }

    private static final HashMap<Clouds, String> cloudList = new HashMap<Clouds, String>() {{
        put( Clouds.GLOBAL_CLOUD, "https://graph.microsoft.com" );
        put( Clouds.USGOV_CLOUD, "https://graph.microsoft.us");
        put( Clouds.CHINA_CLOUD, "https://microsoftgraph.chinacloudapi.cn");
        put( Clouds.GERMANY_CLOUD, "https://graph.microsoft.de");
        put( Clouds.USGOV_DOD_CLOUD, "https://dod-graph.microsoft.us");
    }};


    /** Base Constructor */
    public BaseGraphRequestAdapter(@Nonnull final AuthenticationProvider authenticationProvider, @Nullable final ParseNodeFactory parseNodeFactory, @Nullable final SerializationWriterFactory serializationWriterFactory, @Nullable final OkHttpClient client, @Nullable final GraphClientOptions graphClientOptions, @Nullable String baseUrl) {
        super(authenticationProvider, parseNodeFactory, serializationWriterFactory, client != null ? client : GraphClientFactory.create(graphClientOptions).build());
        if (baseUrl != null && !baseUrl.isEmpty()) {
            setBaseUrl(baseUrl);
        } else {
            setBaseUrl(determineBaseAddress(null, null));
        }
    }

    public BaseGraphRequestAdapter(@Nonnull final  AuthenticationProvider authenticationProvider, @Nonnull String baseUrl) {
        this(authenticationProvider, null, null, null, null, baseUrl);
    }

    public BaseGraphRequestAdapter(@Nonnull final AuthenticationProvider authenticationProvider, @Nonnull Clouds nationalCloud, @Nonnull String version) {
        this(authenticationProvider, determineBaseAddress(nationalCloud, version));
    }

    public BaseGraphRequestAdapter(@Nonnull OkHttpClient client, @Nullable GraphClientOptions graphClientOptions, @Nonnull String baseUrl) {
        this(new AnonymousAuthenticationProvider(), null, null, client,graphClientOptions, baseUrl);
    }

    public BaseGraphRequestAdapter(@Nonnull OkHttpClient client, @Nullable GraphClientOptions graphClientOptions, @Nonnull Clouds nationalCloud, @Nonnull String version) {
        this(client,graphClientOptions, determineBaseAddress(nationalCloud, version));
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
