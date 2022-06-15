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

    //private static final ChronoUnit defaultTimeout = new ChroUn

    private static final HashMap<String, String> cloudList = new HashMap<String, String>() {{
        put( "Global_Cloud" , "https://graph.microsoft.com" );
        put( "USGOV_Cloud", "https://graph.microsoft.us");
        put( "China_Cloud", "https://microsoftgraph.chinacloudapi.cn");
        put( "Germany_Cloud", "https://graph.microsoft.de");
        put( "USGOV_DOD_Cloud", "https://dod-graph.microsoft.us");
    }};

    public final String Global_Cloud = "Global";
    public final String USGOV_Cloud = "US_GOV";
    public final String USGOV_DOD_Cloud = "US_GOV_DOD";
    public final String China_Cloud = "China";
    public final String Germany_Cloud = "Germany";

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
        handlers.add(new CompressionHandler());
        for(final Interceptor interceptor: KiotaClientFactory.CreateDefaultInterceptors()) {
            handlers.add(interceptor);
        }
        return (Interceptor[]) handlers.toArray();
    }


    //Cant set base address on client in OKhttp, Keeping this in case we need it but I don't believe this will belong here.
    private static URI determineBaseAddress(String nationalCloud, String version) throws URISyntaxException, IllegalArgumentException {
        String cloud = cloudList.get(nationalCloud);
        if(cloud == null){
            throw new IllegalArgumentException(String.format("%s is an unexpected national cloud.", nationalCloud));
        }
        return new URI(String.format("%s/%s/",nationalCloud,version));
    }

}
