package com.microsoft.graph.exceptions;

import com.sun.deploy.uitoolkit.impl.fx.ui.ErrorPane;

public class AuthenticationException extends Exception{

    public Error error;
    public Exception innerException;

    public AuthenticationException(Error error) {
        super(error.Message);
        this.error = error;
    }

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(Error error, Throwable rootCause) {
        super(error.getMessage(), rootCause);
    }
}
