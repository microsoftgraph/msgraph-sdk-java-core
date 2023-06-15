package com.microsoft.graph;

import java.util.Locale;

/**
 * Core Constants for use in other classes.
 */
public final class CoreConstants {
    private CoreConstants() {}

    private static class VersionValues {
        private static final int MAJOR = 3;
        private static final int MINOR = 0;
        private static final int PATCH = 5;
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
        public static final String VERSION = String.format(Locale.US, "%d.%d.%d", VersionValues.MAJOR, VersionValues.MINOR, VersionValues.PATCH);
        /** Client Request ID header constant. */
        public static final String CLIENT_REQUEST_ID = "client-request-id";
        /** Feature flag header constant. */
        public static final String FEATURE_FLAG = "FeatureFlag";
        /** Default version value constant. */
        public static final String DEFAULT_VERSION_VALUE = "0";
    }

    /**
     * Batch Request Constants
     */
    public static class BatchRequest {
        private BatchRequest(){}
        /** Batch request max requests property */
        public static final int MAX_REQUESTS = 20;
        /** Batch request step id property */
        public static final String ID = "id";
        /** Batch request step url property */
        public static final String URL = "url";
        /** Batch request step body property */
        public static final String BODY = "body";
        /** Batch request step dependsOn property */
        public static final String DEPENDS_ON = "dependsOn";
        /** Batch request step method property */
        public static final String METHOD = "method";
        /** Batch request step requests property */
        public static final String REQUESTS = "requests";
        /** Batch request step responses property */
        public static final String RESPONSES = "responses";
        /** Batch request step status property */
        public static final String STATUS = "status";
        /** Batch request step headers property */
        public static final String HEADERS = "headers";
        /** Batch request step error property */
        public static final String ERROR = "error";
    }

    /**
     * MimeTypeNames Constants
     */
    public static class MimeTypeNames {
        private MimeTypeNames(){}
        /** Content type header constant for application/json */
        public static final String APPLICATION_JSON = "application/json";
        /** Content type header constant for application/octet-stream */
        public static final String APPLICATION_STREAM = "application/octet-stream";
    }

    /**
     * Serialization Constants
     */
    public static class Serialization {
        private Serialization(){}
        /** OData type property */
        public static final String ODATA_TYPE = "@odata.type";
        /** OData nextLink property */
        public static final String ODATA_NEXT_LINK = "@nextLink";
    }

    public static class OdataInstanceAnnotations {
        private OdataInstanceAnnotations(){}
        public static final String NEXT_LINK = "@odata.nextLink";
        public static final String DELTA_LINK = "@odata.deltaLink";
    }

    public static class CollectionResponseMethods {
        private CollectionResponseMethods(){}
        public static final String GET_ODATA_DELTA_LINK = "getOdataDeltaLink";
        public static final String GET_ODATA_NEXT_LINK = "getOdataNextLink";
    }

}
