package com.microsoft.graph.exceptions;

import javax.annotation.Nonnull;

public final class ErrorConstants {

    public static class Codes {
        @Nonnull
        public final static String GeneralException = "generalException";
        @Nonnull
        public final static String InvalidArgument = "invalidArgument";

    }

    public static class Messages {
        @Nonnull
        public final static String NullParameter = "%s parameter cannot be null.";
        @Nonnull
        public final static String AuthTimeOut = "Authentication failed or timed out.";
    }
}
