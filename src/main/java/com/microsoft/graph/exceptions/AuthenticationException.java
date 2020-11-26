package com.microsoft.graph.exceptions;

import javax.annotation.Nonnull;

public class AuthenticationException extends Exception{

    /**
     * serial id
     */
    private static final long serialVersionUID = 8708883112518988965L;
    // Error object to store details of thrown Exception
    @Nonnull
    public Error error;

    /**
     *Creates a new AuthenticationException with a specified Error
     *
     * @param error The specifying error causing exception to be thrown
     */
    public AuthenticationException(@Nonnull final Error error) {
        super(error.getMessage());
        this.error = error;
    }

    /**
     *Creates a new AuthenticationException with a specified Error as well as an inner exception
     *
     * @param error The specified error causing exception to be thrown
     * @param rootCause The underlying exception causing AuthenticationException to be thrown
     */
    public AuthenticationException(@Nonnull final Error error, @Nonnull final Throwable rootCause) {
        super(error.getMessage(), rootCause);
        this.error = error;
    }
}
