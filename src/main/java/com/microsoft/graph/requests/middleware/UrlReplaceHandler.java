package com.microsoft.graph.requests.middleware;

import com.microsoft.graph.UrlReplacement;
import com.microsoft.graph.requests.options.UrlReplaceOption;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class UrlReplaceHandler implements Interceptor {

    private final UrlReplaceOption mUrlReplaceOption;

    /**
     * Instantiate a GraphTelemetryHandler with default GraphClientOption.
     */
    public UrlReplaceHandler(){
        this(new UrlReplaceOption());
    }
    /**
     * Instantiate a GraphTelemetryHandler with specified GraphClientOption
     * @param urlReplaceOption the specified GraphClientOption for the GraphTelemetryHandler.
     */
    @SuppressFBWarnings
    public UrlReplaceHandler(@Nonnull final UrlReplaceOption urlReplaceOption){
        this.mUrlReplaceOption = Objects.requireNonNull(urlReplaceOption);
    }
    @Nonnull
    @Override
    public Response intercept(@Nonnull Chain chain) throws IOException {
        final Request request = chain.request();
        return chain.proceed(UrlReplacement.replaceRequestUrl(request, mUrlReplaceOption.getReplacementPairs()));
    }
}
