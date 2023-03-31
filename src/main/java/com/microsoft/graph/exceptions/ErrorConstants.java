package com.microsoft.graph.exceptions;

public class ErrorConstants {

    public static class Codes {
        public static final String GeneralException = "generalException";

        public static final String InvalidRequest = "invalidRequest";

        public static final String ItemNotFound = "itemNotFound";

        public static final String NotAllowed = "notAllowed";

        public static final String Timeout = "timeout";

        public static final String TooManyRedirects = "tooManyRedirects";

        public static final String TooManyRetries = "tooManyRetries";

        public static final String MaximumValueExceeded = "MaximumValueExceeded";

        public static final String InvalidArgument = "invalidArgument";

        public static final String TemporarilyUnavailable = "temporarily_unavailable";

        public static final String InvalidRange = "invalidRange";
        }

    public static class Messages {
        public static final String AuthenticationProviderMissing = "Authentication provider is required before sending a request.";

        public static final String BaseUrlMissing = "Base URL cannot be null or empty.";

        public static final String InvalidTypeForDateConverter = "DateConverter can only serialize objects of type Date.";

        public static final String InvalidTypeForDateTimeOffsetConverter = "DateTimeOffsetConverter can only serialize objects of type DateTimeOffset.";

        public static final String LocationHeaderNotSetOnRedirect = "Location header not present in redirection response.";

        public static final String OverallTimeoutCannotBeSet = "Overall timeout cannot be set after the first request is sent.";

        public static final String RequestTimedOut = "The request timed out.";

        public static final String RequestUrlMissing = "Request URL is required to send a request.";

        public static final String TooManyRedirectsFormatString = "More than {0} redirects encountered while sending the request.";

        public static final String TooManyRetriesFormatString = "More than {0} retries encountered while sending the request.";

        public static final String UnableToCreateInstanceOfTypeFormatString = "Unable to create an instance of type {0}.";

        public static final String UnableToDeserializeDate = "Unable to deserialize the returned Date.";

        public static final String UnableToDeserializeDateTimeOffset = "Unable to deserialize the returned DateDateTimeOffset.";

        public static final String UnexpectedExceptionOnSend = "An error occurred sending the request.";

        public static final String UnexpectedExceptionResponse = "Unexpected exception returned from the service.";

        public static final String MaximumValueExceeded = "{0} exceeds the maximum value of {1}.";

        public static final String NullParameter = "{0} parameter cannot be null.";

        public static final String UnableToDeserializeContent = "Unable to deserialize content.";

        public static final String InvalidDependsOnRequestId = "Corresponding batch request id not found for the specified dependsOn relation.";

        public static final String ExpiredUploadSession = "Upload session expired. Upload cannot resume";

        public static final String NoResponseForUpload = "No Response Received for upload.";

        public static final String NullValue = "{0} cannot be null.";

        public static final String UnexpectedMsalException = "Unexpected exception returned from MSAL.";

        public static final String UnexpectedException = "Unexpected exception occured while authenticating the request.";

        public static final String MissingRetryAfterHeader = "Missing retry after header.";

        public static final String InvalidProxyArgument = "Proxy cannot be set more once. Proxy can only be set on the proxy or defaultHttpHandler argument and not both.";
    }
}
