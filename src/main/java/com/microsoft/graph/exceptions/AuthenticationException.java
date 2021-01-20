package com.microsoft.graph.exceptions;

import javax.annotation.Nonnull;

import com.microsoft.graph.core.ClientException;

/**
 * Exceptions thrown by the authentication provider when the authentication sequence has failed
 */
public class AuthenticationException extends ClientException {

    /**
     * serial id
     */
    private static final long serialVersionUID = 8708883112518988965L;
    /**
     * Creates a new AuthenticationException with a specified Error as well as an inner exception
     *
     * @param message The specified error message causing exception to be thrown
     * @param rootCause The underlying exception causing AuthenticationException to be thrown
     */
    public AuthenticationException(@Nonnull final String message, @Nonnull final Throwable rootCause) {
        super(message, rootCause);
    }
    /**
     * Creates a new AuthenticationException with a specified Error as well as an inner exception
     *
     * @param rootCause The underlying exception causing AuthenticationException to be thrown
     */
    public AuthenticationException(@Nonnull final Throwable rootCause) {
        super(rootCause.getMessage(), rootCause);
    }
}
