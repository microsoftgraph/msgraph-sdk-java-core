package com.microsoft.graph.exceptions;

import com.microsoft.kiota.ApiException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ClientException extends ApiException {
    /**
     * Constructor for a ClientException
     * @param message The exception message.
     * @param cause The possible inner exception causing this exception.
     */
    public ClientException(@Nonnull String message, @Nullable Throwable cause) {
        super(message, cause);
    }

}
