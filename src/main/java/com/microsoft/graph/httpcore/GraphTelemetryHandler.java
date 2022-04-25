package com.microsoft.graph.httpcore;

import java.io.IOException;
import java.lang.reflect.Field;

import javax.annotation.Nonnull;

import com.microsoft.graph.CoreConstants;
import com.microsoft.graph.httpcore.middlewareoption.GraphClientOptions;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Middleware responsible for adding telemetry information on SDK usage
 * Note: the telemetry only collects anonymous information on SDK version and usage. No personal information is collected.
 */
public class GraphTelemetryHandler implements Interceptor{

    private GraphClientOptions mGraphClientOptions;

    public GraphTelemetryHandler(){
        this.mGraphClientOptions = new GraphClientOptions();
    }

    @SuppressFBWarnings
    //graphClientOptions exposes strings which are naturally immutable
    public GraphTelemetryHandler(@Nonnull final GraphClientOptions graphClientOptions){
        this.mGraphClientOptions = graphClientOptions;
    }

    @Override
    @Nonnull
    public Response intercept(@Nonnull final Chain chain) throws IOException {
        final Request request = chain.request();
        final Request.Builder telemetryAddedBuilder = request.newBuilder();

        FeatureTracker featureTracker = request.tag(FeatureTracker.class);
        if(featureTracker == null) {
            featureTracker = new FeatureTracker();
        }

        //This assumes a call to graph will always include v1.0 or beta in the url
        final String graphEndpoint = request.url().toString().contains("/v1.0/") ? "-v1.0" : "-beta";
        final String featureUsage = "(featureUsage=" + featureTracker.getSerializedFeatureUsage() + ")";
        final String javaVersion = System.getProperty("java.version");
        final String androidVersion = getAndroidAPILevel();
        final String sdkversion_value = "graph-" + CoreConstants.Headers.JAVA_VERSION_PREFIX + graphEndpoint +
            (mGraphClientOptions.getClientLibraryVersion() == null ? "" : "/"+ mGraphClientOptions.getClientLibraryVersion()) + ", " +
            CoreConstants.Headers.GRAPH_VERSION_PREFIX + "/" + mGraphClientOptions.getCoreLibraryVersion() + " " + featureUsage +
            (CoreConstants.Headers.DEFAULT_VERSION_VALUE.equals(javaVersion) ? "" : (", " + CoreConstants.Headers.JAVA_VERSION_PREFIX + "/" + javaVersion)) +
            (CoreConstants.Headers.DEFAULT_VERSION_VALUE.equals(androidVersion) ? "" : (", " + CoreConstants.Headers.ANDROID_VERSION_PREFIX + "/" + androidVersion));
        telemetryAddedBuilder.addHeader(CoreConstants.Headers.SDK_VERSION_HEADER_NAME, sdkversion_value);

        if(request.header(CoreConstants.Headers.CLIENT_REQUEST_ID) == null) {
            telemetryAddedBuilder.addHeader(CoreConstants.Headers.CLIENT_REQUEST_ID, mGraphClientOptions.getClientRequestId());
        }

        return chain.proceed(telemetryAddedBuilder.build());
    }

    private String androidAPILevel;
    private String getAndroidAPILevel() {
        if(androidAPILevel == null) {
            androidAPILevel = getAndroidAPILevelInternal();
        }
        return androidAPILevel;
    }
    private String getAndroidAPILevelInternal() {
        try {
            final Class<?> buildClass = Class.forName("android.os.Build");
            final Class<?>[] subclasses = buildClass.getDeclaredClasses();
            Class<?> versionClass = null;
            for(final Class<?> subclass : subclasses) {
                if(subclass.getName().endsWith("VERSION")) {
                    versionClass = subclass;
                    break;
                }
            }
            if(versionClass == null)
                return CoreConstants.Headers.DEFAULT_VERSION_VALUE;
            else {
                final Field sdkVersionField = versionClass.getField("SDK_INT");
                final Object value = sdkVersionField.get(null);
                final String valueStr = String.valueOf(value);
                return valueStr == null || valueStr.equals("") ? CoreConstants.Headers.DEFAULT_VERSION_VALUE : valueStr;
            }
        } catch (IllegalAccessException | ClassNotFoundException | NoSuchFieldException ex) {
            // we're not on android and return "0" to align with java version which returns "0" when running on android
            return CoreConstants.Headers.DEFAULT_VERSION_VALUE;
        }
    }
}
