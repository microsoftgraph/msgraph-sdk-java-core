package com.microsoft.graph;

public class CoreConstants {

    private static class VersionValues {
        private static final int Major = 3;
        private static final int Minor = 0;
        private static final int Patch = 0;
    }

    public static class Headers {
        public static final String BEARER = "Bearer";
        public static final String SDK_VERSION_HEADER_NAME = "SdkVersion";
        public static final String GRAPH_VERSION_PREFIX = "graph-java-core";
        public static final String ANDROID_VERSION_PREFIX = "android";
        public static final String JAVA_VERSION_PREFIX = "java";
        public static final String VERSION = String.format("%d.%d.%d", VersionValues.Major, VersionValues.Minor, VersionValues.Patch);
        public static final String CLIENT_REQUEST_ID = "client-request-id";
        public static final String FEATURE_FLAG = "FeatureFlag";
        public static final String DEFAULT_VERSION_VALUE = "0";

        /**The following appear in dotnet core, are they necessary in Java?
         * Content-Type header:
         * public final String FormUrlEncodedContentType = "application/x-www-form-urlencoded";
         * Throw-site header:
         * public final String ThrowSiteHeaderName = "X-ThrowSite";
         **/
    }

    //TODO add other constants classes as work on other core features continues

}
