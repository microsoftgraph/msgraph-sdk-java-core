package com.microsoft.graph;

public class CoreConstants {

    private static class VersionValues {
        private static final int Major = 3;
        private static final int Minor = 0;
        private static final int Patch = 0;
    }

    public static class Headers {
        public static final String Bearer = "Bearer";
        public static final String SdkVersionHeaderName = "SdkVersion";
        public static final String GraphVersionPrefix = "graph-java-core";
        public static final String AndroidVersionPrefix = "android";
        public static final String JavaVersionPrefix = "java";
        public static final String Version = String.format("v%d.%d.%d", VersionValues.Major, VersionValues.Minor, VersionValues.Patch);
        public static final String ClientRequestId = "client-request-id";
        public static final String FeatureFlag = "FeatureFlag";
        public static final String DefaultVersionValue = "0";

        /**The following appear in dotnet core, are they necessary in Java?
         * Content-Type header:
         * public final String FormUrlEncodedContentType = "application/x-www-form-urlencoded";
         * Throw-site header:
         * public final String ThrowSiteHeaderName = "X-ThrowSite";
         **/
    }

    //TODO add other constants classes as work on other core features continues

}
