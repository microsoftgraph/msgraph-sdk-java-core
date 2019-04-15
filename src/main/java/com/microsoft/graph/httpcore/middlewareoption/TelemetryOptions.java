package com.microsoft.graph.httpcore.middlewareoption;

import java.math.BigInteger;
import java.util.UUID;

public class TelemetryOptions {

	public static final String NONE_FLAG = "0x00000000";
	public static final String REDIRECT_HANDLER_ENABLED_FLAG =  "0x00000001";
	public static final String RETRY_HANDLER_ENABLED_FLAG = "0x00000002";
	public static final String AUTH_HANDLER_ENABLED_FLAG = "0x00000004";
	public static final String DEFAULT_HTTPROVIDER_ENABLED_FLAG = "0x00000008";
	public static final String LOGGING_HANDLER_ENABLED_FLAG = "0x00000010";

	private static final int RADIX = 16;
	private BigInteger featureUsage = new BigInteger(NONE_FLAG.substring(2), RADIX);
	private String clientRequestId;

	public void setFeatureUsage(String flag) {
		featureUsage = featureUsage.or(new BigInteger(flag.substring(2), RADIX));
	}

	public String getFeatureUsage() {
		return featureUsage.toString(RADIX);
	}

	public void setClientRequestId(String clientRequestId) {
		this.clientRequestId = clientRequestId;
	}

	public String getClientRequestId() {
		if(clientRequestId == null) {
			clientRequestId = UUID.randomUUID().toString();
		}
		return clientRequestId;
	}

}
