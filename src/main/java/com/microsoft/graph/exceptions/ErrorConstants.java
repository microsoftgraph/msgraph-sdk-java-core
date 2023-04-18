package com.microsoft.graph.exceptions;
/**
 * Constants used for exception building
 */
public class ErrorConstants {
    private ErrorConstants() {}

    /**
     * Exception codes
     */
    public static class Codes {
        private Codes() {}
        /** General exception code. */
        public static final String GENERAL_EXCEPTION = "generalException";
        /** Invalid request exception code. */
        public static final String INVALID_REQUEST = "invalidRequest";
        /** Item not found exception code. */
        public static final String ITEM_NOT_FOUND = "itemNotFound";
        /** Not allowed exception code. */
        public static final String NOT_ALLOWED = "notAllowed";
        /** Timeout exception code.*/
        public static final String TIMEOUT = "timeout";
        /** Too many redirects exception code. */
        public static final String TOO_MANY_REDIRECTS = "tooManyRedirects";
        /** Too many retries exception code. */
        public static final String TOO_MANY_RETRIES = "tooManyRetries";
        /** Maximum value exceeded exception code. */
        public static final String MAXIMUM_VALUE_EXCEEDED = "MaximumValueExceeded";
        /** Invalid argument exception code. */
        public static final String INVALID_ARGUMENT = "invalidArgument";
        /** Temporarily unavailable exception code. */
        public static final String TEMPORARILY_UNAVAILABLE = "temporarily_unavailable";
        /** Invalid range exception code. */
        public static final String INVALID_RANGE = "invalidRange";
        }

    /**
     * Exception messages
     */
    public static class Messages {
        private Messages() {}
        /** Authentication provider missing error message. */
        public static final String AUTHENTICATION_PROVIDER_MISSING = "Authentication provider is required before sending a request.";
        /** Base Url missing error message. */
        public static final String BASE_URL_MISSING = "Base URL cannot be null or empty.";
        /** Invalid type for date converter error message. */
        public static final String INVALID_TYPE_FOR_DATE_CONVERTER = "DateConverter can only serialize objects of type Date.";
        /** Invalid type for date time offset converter error message. */
        public static final String INVALID_TYPE_FOR_DATE_TIME_OFFSET_CONVERTER = "DateTimeOffsetConverter can only serialize objects of type DateTimeOffset.";
        /** Location header not set on redirect error message. */
        public static final String LOCATION_HEADER_NOT_SET_ON_REDIRECT = "Location header not present in redirection response.";
        /** Overall timeout cannot be set error message. */
        public static final String OVERALL_TIMEOUT_CANNOT_BE_SET = "Overall timeout cannot be set after the first request is sent.";
        /** Request timed out error message. */
        public static final String REQUEST_TIMED_OUT = "The request timed out.";
        /** Request Url missing error message. */
        public static final String REQUEST_URL_MISSING = "Request URL is required to send a request.";
        /** Too many redirects error message. */
        public static final String TOO_MANY_REDIRECTS_FORMAT_STRING = "More than {0} redirects encountered while sending the request.";
        /** Too many retries error message. */
        public static final String TOO_MANY_RETRIES_FORMAT_STRING = "More than {0} retries encountered while sending the request.";
        /** Unable to create instance of type error message. */
        public static final String UNABLE_TO_CREATE_INSTANCE_OF_TYPE_FORMAT_STRING = "Unable to create an instance of type {0}.";
        /** Unable to deserialize date error message. */
        public static final String UNABLE_TO_DESERIALIZE_DATE = "Unable to deserialize the returned Date.";
        /** Unable to deserialize date time offset error message. */
        public static final String UNABLE_TO_DESERIALIZE_DATE_TIME_OFFSET = "Unable to deserialize the returned DateDateTimeOffset.";
        /** Unexpected exception on send error message. */
        public static final String UNEXPECTED_EXCEPTION_ON_SEND = "An error occurred sending the request.";
        /** Unexpected exception response error message. */
        public static final String UNEXPECTED_EXCEPTION_RESPONSE = "Unexpected exception returned from the service.";
        /** Maximum value exceeded error message. */
        public static final String MAXIMUM_VALUE_EXCEEDED = "{0} exceeds the maximum value of {1}.";
        /** Null parameter error message. */
        public static final String NULL_PARAMETER = "{0} parameter cannot be null.";
        /** Unable to deserialize content error message.  */
        public static final String UNABLE_TO_DESERIALIZE_CONTENT = "Unable to deserialize content.";
        /** Invalid depends on request Id error message. */
        public static final String INVALID_DEPENDS_ON_REQUEST_ID = "Corresponding batch request id not found for the specified dependsOn relation.";
        /** Expired upload session error message. */
        public static final String EXPIRED_UPLOAD_SESSION = "Upload session expired. Upload cannot resume";
        /** No response for upload error message. */
        public static final String NO_RESPONSE_FOR_UPLOAD = "No Response Received for upload.";
        /** Null value error message. */
        public static final String NULL_VALUE = "{0} cannot be null.";
        /** Unexpected msal exception error message. */
        public static final String UNEXPECTED_MSAL_EXCEPTION = "Unexpected exception returned from MSAL.";
        /** Unexpected exception error message. */
        public static final String UNEXPECTED_EXCEPTION = "Unexpected exception occured while authenticating the request.";
        /** Missing retry after header error message. */
        public static final String MISSING_RETRY_AFTER_HEADER = "Missing retry after header.";
        /** Invalid proxy argument error message. */
        public static final String INVALID_PROXY_ARGUMENT = "Proxy cannot be set more once. Proxy can only be set on the proxy or defaultHttpHandler argument and not both.";
    }
}
