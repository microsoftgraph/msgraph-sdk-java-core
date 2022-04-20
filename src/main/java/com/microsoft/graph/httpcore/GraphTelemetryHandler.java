package com.microsoft.graph.httpcore;

import java.io.IOException;
import java.lang.reflect.Field;

import javax.annotation.Nonnull;

import com.microsoft.graph.CoreConstants;
import com.microsoft.graph.httpcore.middlewareoption.TelemetryHandlerOption;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Middleware responsible for adding telemetry information on SDK usage
 * Note: the telemetry only collects anonymous information on SDK version and usage. No personal information is collected.
 */
public class GraphTelemetryHandler implements Interceptor{

    @Override
    @Nonnull
    public Response intercept(@Nonnull final Chain chain) throws IOException {
        final Request request = chain.request();
        final Request.Builder telemetryAddedBuilder = request.newBuilder();

        TelemetryHandlerOption telemetryHandlerOption = request.tag(TelemetryHandlerOption.class);
        if(telemetryHandlerOption == null)
            telemetryHandlerOption = new TelemetryHandlerOption();

        //This assumes a call to graph will always include v1.0 or beta in the url, should this assumption be changed?
        final String graphEndpoint = request.url().toString().contains("/v1.0/") ? "v1.0" : "beta";
        System.out.println(graphEndpoint);
        final String featureUsage = "(featureUsage=" + telemetryHandlerOption.getFeatureUsage() + ")";
        final String javaVersion = System.getProperty("java.version");
        final String androidVersion = getAndroidAPILevel();
        final String sdkversion_value = CoreConstants.Headers.GraphVersionPrefix + "/" + CoreConstants.Headers.Version + " " + featureUsage +
                                                (CoreConstants.Headers.DefaultVersionValue.equals(javaVersion) ? "" : (", " + CoreConstants.Headers.JavaVersionPrefix + "/" + javaVersion)) +
                                                (CoreConstants.Headers.DefaultVersionValue.equals(androidVersion) ? "" : (", " + CoreConstants.Headers.AndroidVersionPrefix + "/" + androidVersion));
        telemetryAddedBuilder.addHeader(CoreConstants.Headers.SdkVersionHeaderName, sdkversion_value);

        if(request.header(CoreConstants.Headers.ClientRequestId) == null) {
            telemetryAddedBuilder.addHeader(CoreConstants.Headers.ClientRequestId, telemetryHandlerOption.getClientRequestId());
        }

        //TODO: add the option to set sdk and core version on telemetryOptions. Set default graph version to latest major version most likely. Add this constants.

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
                return CoreConstants.Headers.DefaultVersionValue;
            else {
                final Field sdkVersionField = versionClass.getField("SDK_INT");
                final Object value = sdkVersionField.get(null);
                final String valueStr = String.valueOf(value);
                return valueStr == null || valueStr.equals("") ? CoreConstants.Headers.DefaultVersionValue : valueStr;
            }
        } catch (IllegalAccessException | ClassNotFoundException | NoSuchFieldException ex) {
            // we're not on android and return "0" to align with java version which returns "0" when running on android
            return CoreConstants.Headers.DefaultVersionValue;
        }
    }
}
