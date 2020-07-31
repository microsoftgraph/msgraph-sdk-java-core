package com.microsoft.graph.exceptions;

import com.sun.deploy.uitoolkit.impl.fx.ui.ErrorPane;

public class AuthenticationException extends Exception{

    public Error error;

    /**
     *Creates a new AuthenticationException with a specified Error
     *
     * @param error The specifying error causing exception to be thrown
     */
    public AuthenticationException(Error error) {
        super(error.getMessage());
        this.error = error;
    }

    /**
     *Creates a new AuthenticationException with a specified Error as well as an inner exception
     *
     * @param error The specified error causing exception to be thrown
     * @param rootCause The underlying exception causing AuthenticationException to be thrown
     */
    public AuthenticationException(Error error, Throwable rootCause) {
        super(error.getMessage(), rootCause);
        this.error = error;
    }

}
