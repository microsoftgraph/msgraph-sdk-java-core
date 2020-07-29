package com.microsoft.graph.exceptions;

import com.sun.deploy.uitoolkit.impl.fx.ui.ErrorPane;

public class AuthenticationException extends Exception{

    public Error error;

    public AuthenticationException(Error error) {
        super(error.getMessage());
        this.error = error;
    }

    public AuthenticationException(Error error, Throwable rootCause) {
        super(error.getMessage(), rootCause);
        this.error = error;
    }

}
