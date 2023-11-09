package com.microsoft.graph.exceptions;

import jakarta.annotation.Nonnull;

/**
 * Graph client exception wrapper.
 */
public class ClientException extends Exception {
    /**
     * Constructor for a ClientException
     * @param message The exception message.
     * @param cause The possible inner exception causing this exception.
     */
    public ClientException(@Nonnull String message, @Nonnull Throwable cause) {
        super(message, cause);
    }
    /***
     * Constructor for a ClientException
     * @param message The exception message.
     */
    public ClientException(@Nonnull String message) {
        super(message);
    }
}
