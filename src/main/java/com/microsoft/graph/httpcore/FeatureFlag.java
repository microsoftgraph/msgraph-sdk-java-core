package com.microsoft.graph.httpcore;

/**
 * The class which holds the values of each feature flag.
 * Values are set such that they translate seamlessly to base 2.
 */
public final class FeatureFlag {
    private FeatureFlag(){}
    /** The value of the None flag, 0.*/
    public static final int NONE_FLAG = 0;
    /** The value of the Redirect Handler flag, 1. */
    public static final int REDIRECT_HANDLER_FLAG = 1;
    /** The value of the Retry Handler flag, 10. */
    public static final int RETRY_HANDLER_FLAG = 2;
    /** The value of the Auth Handler flag, 100. */
    public static final int AUTH_HANDLER_FLAG = 4;
    /** The value of the Default Http flag, 1000. */
    public static final int DEFAULT_HTTP_FLAG = 8;
    /** The value of the Logging Handler flag, 10000. */
    public static final int LOGGING_HANDLER_FLAG = 16;
    /** The value of the Service Discovery flag, 100000. */
    public static final int SERVICE_DISCOVERY_FLAG = 32;
    /** The value of the Compression Handler flag, 1000000. */
    public static final int COMPRESSION_HANDLER_FLAG = 64;
    /** The value of the Connection Pool flag, 10000000. */
    public static final int CONNECTION_POOL_FLAG = 128;
    /** The value of the Long Running Operation flag, 100000000. */
    public static final int LONG_RUNNING_OP_FLAG = 256;
    /** The value of the Batch Request flag, 1000000000. */
    public static final int BATCH_REQUEST_FLAG = 512;
    /** The value of the Page Iterator flag, 10000000000. */
    public static final int PAGE_ITERATOR_FLAG = 1024;
    /** The value of the File Upload flag, 100000000000. */
    public static final int FILE_UPLOAD_FLAG = 2048;
}
