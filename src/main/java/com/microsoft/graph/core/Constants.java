package com.microsoft.graph.core;

/** Multi-purpose constants holder used accross the SDK */
public final class Constants {
	private Constants() {
	}
	/**
	 * The content type header
	 */
	public static final String CONTENT_TYPE_HEADER_NAME = "Content-Type";
	/**
	 * The encoding type for getBytes
	 */
	public static final String JSON_ENCODING = "UTF-8";
	/**
	 * The content type for JSON responses
	 */
    public static final String JSON_CONTENT_TYPE = "application/json";
    /**
	 * The content type for TEXT responses
	 */
	public static final String TEXT_CONTENT_TYPE = "text/plain";
	/**
	 * The binary content type header's value
	 */
	public static final String BINARY_CONTENT_TYPE = "application/octet-stream";

	/** The SDK version */
	public static final String VERSION_NAME = "2.5.0";
}

