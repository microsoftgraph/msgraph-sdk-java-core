package com.microsoft.graph.requests.middleware;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Objects;

import javax.annotation.Nonnull;

import com.microsoft.graph.CoreConstants;
import com.microsoft.graph.requests.GraphClientOption;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Middleware responsible for adding telemetry information on SDK usage
 * Note: the telemetry only collects anonymous information on SDK version and usage. No personal information is collected.
 */
public class GraphTelemetryHandler implements Interceptor{

    private GraphClientOption mGraphClientOption;

    /**
     * Instantiate a GraphTelemetryHandler with default GraphClientOption.
     */
    public GraphTelemetryHandler(){
        this(new GraphClientOption());
    }
    /**
     * Instantiate a GraphTelemetryHandler with specified GraphClientOption
     * @param graphClientOption the specified GraphClientOption for the GraphTelemetryHandler.
     */
    @SuppressFBWarnings
    public GraphTelemetryHandler(@Nonnull final GraphClientOption graphClientOption){
        this.mGraphClientOption = Objects.requireNonNull(graphClientOption);
    }

    @Override
    @Nonnull
    public Response intercept(@Nonnull final Chain chain) throws IOException {
        final Request request = chain.request();
        final Request.Builder telemetryAddedBuilder = request.newBuilder();

        final String graphEndpoint = mGraphClientOption.getGraphServiceTargetVersion();
        final String featureUsage = "(featureUsage=" + mGraphClientOption.featureTracker.getSerializedFeatureUsage() + ")";
        final String javaVersion = System.getProperty("java.version");
        final String androidVersion = getAndroidAPILevel();
        final String sdkversion_value = "graph-" + CoreConstants.Headers.JAVA_VERSION_PREFIX +"/"+ graphEndpoint +
            (mGraphClientOption.getClientLibraryVersion() == null ? "" : "/"+ mGraphClientOption.getClientLibraryVersion()) + ", " +
            CoreConstants.Headers.GRAPH_VERSION_PREFIX + "/" + mGraphClientOption.getCoreLibraryVersion() + " " + featureUsage +
            (CoreConstants.Headers.DEFAULT_VERSION_VALUE.equals(javaVersion) ? "" : (", " + CoreConstants.Headers.JAVA_VERSION_PREFIX + "/" + javaVersion)) +
            (CoreConstants.Headers.DEFAULT_VERSION_VALUE.equals(androidVersion) ? "" : (", " + CoreConstants.Headers.ANDROID_VERSION_PREFIX + "/" + androidVersion));
        telemetryAddedBuilder.addHeader(CoreConstants.Headers.SDK_VERSION_HEADER_NAME, sdkversion_value);

        if(request.header(CoreConstants.Headers.CLIENT_REQUEST_ID) == null) {
            telemetryAddedBuilder.addHeader(CoreConstants.Headers.CLIENT_REQUEST_ID, mGraphClientOption.getClientRequestId());
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
