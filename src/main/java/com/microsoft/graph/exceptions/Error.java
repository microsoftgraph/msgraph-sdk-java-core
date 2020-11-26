package com.microsoft.graph.exceptions;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Error {

    //Error Code specifying error type
    @Nullable
    public String Code;
    //Error Message describing error
    @Nonnull
    public String Message;

    /**
     * Creates a new error with a specified message and code
     *
     * @param code The specified error code
     * @param message The specified error message
     */
    public Error(@Nonnull final String code, @Nonnull final String message) {
        this.Code = code;
        this.Message = message;
    }

    /**
     * Creates a new error with a specified message
     *
     * @param message The specified error message
     */
    public Error(@Nonnull final String message)
    {
        this.Message = message;
    }

    /**
     * Get the error message
     *
     * @return the error message
     */
    @Nonnull
    public String getMessage(){return this.Message;}

    /**
     * Get the error code
     *
     * @return the error code
     */
    @Nullable
    public String getCode(){return this.Code;}

    /**
     * Set the error message
     *
     * @param message the error message
     */
    public void setMessage(@Nonnull final String message){ this.Message = message; }

    /**
     * Set the error code
     *
     * @param code the error code
     */
    public void setCode(@Nonnull final String code) { this.Code = code; }

    /**
     * Returns a string with the specified error details
     *
     * @return the error details as a String
     */
    @Nonnull
    public String toString() {
        StringBuilder errorString = new StringBuilder();

        if(this.Code != null) {
            errorString.append(String.format("Code: %s", this.Code));
            errorString.append(System.lineSeparator());
        }

        if(this.Message != null){
            errorString.append(String.format("Message: %s", this.Message));
            errorString.append(System.lineSeparator());
        }

        return errorString.toString();
    }
}


