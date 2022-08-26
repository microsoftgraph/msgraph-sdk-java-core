package com.microsoft.graph;

/**
 * Core Constants for use in other classes.
 */
public final class CoreConstants {
    private CoreConstants() {}

    private static class VersionValues {
        private static final int MAJOR = 3;
        private static final int MINOR = 0;
        private static final int PATCH = 0;
    }

    /**
     * Header Constants
     */
    public static class Headers {
        private Headers(){}
        /** Bearer string constant. */
        public static final String BEARER = "Bearer";
        /** SDK version header constant. */
        public static final String SDK_VERSION_HEADER_NAME = "SdkVersion";
        /** Graph version prefix header constant. */
        public static final String GRAPH_VERSION_PREFIX = "graph-java-core";
        /** Android version prefix header constant. */
        public static final String ANDROID_VERSION_PREFIX = "android";
        /** Java version prefix header constant. */
        public static final String JAVA_VERSION_PREFIX = "java";
        /** Version number header. */
        public static final String VERSION = String.format("%d.%d.%d", VersionValues.MAJOR, VersionValues.MINOR, VersionValues.PATCH);
        /** Client Request ID header constant. */
        public static final String CLIENT_REQUEST_ID = "client-request-id";
        /** Feature flag header constant. */
        public static final String FEATURE_FLAG = "FeatureFlag";
        /** Default version value constant. */
        public static final String DEFAULT_VERSION_VALUE = "0";

        /**The following appear in dotnet core, are they necessary in Java?
         * Content-Type header:
         * public final String FormUrlEncodedContentType = "application/x-www-form-urlencoded";
         * Throw-site header:
         * public final String ThrowSiteHeaderName = "X-ThrowSite";
         **/
    }
}
