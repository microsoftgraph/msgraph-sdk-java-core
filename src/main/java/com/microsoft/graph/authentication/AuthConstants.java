package com.microsoft.graph.authentication;

import com.google.common.annotations.VisibleForTesting;

/**
 * Internal only.
 * Constants in use for the authentication provider
 */
@VisibleForTesting
class AuthConstants {
    /** The bearer value for the authorization request header, contains a space */
    @VisibleForTesting
    protected static final String BEARER = "Bearer ";
    /** The authorization request header name */
    @VisibleForTesting
    protected static final String AUTHORIZATION_HEADER = "Authorization";
}
