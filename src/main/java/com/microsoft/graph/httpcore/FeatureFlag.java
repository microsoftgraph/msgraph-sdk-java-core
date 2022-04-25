package com.microsoft.graph.httpcore;

public class FeatureFlag {
    public static final int NONE_FLAG = 0;
    public static final int REDIRECT_HANDLER_FLAG = 1;
    public static final int RETRY_HANDLER_FLAG = 2;
    public static final int AUTH_HANDLER_FLAG = 4;
    public static final int DEFAULT_HTTP_FLAG = 8;
    public static final int LOGGING_HANDLER_FLAG = 16;
    public static final int SERVICE_DISCOVERY_FLAG = 32;
    public static final int COMPRESSION_HANDLER_FLAG = 64;
    public static final int CONNECTION_POOL_FLAG = 128;
    public static final int LONG_RUNNING_OP_FLAG = 256;
    public static final int BATCH_REQUEST_FLAG = 512;
    public static final int PAGE_ITERATOR_FLAG = 1024;
    public static final int FILE_UPLOAD_FLAG = 2048;
}
