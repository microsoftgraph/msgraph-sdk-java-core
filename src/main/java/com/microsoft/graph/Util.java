package com.microsoft.graph;

import java.io.Closeable;

import javax.annotation.Nullable;

/**
 * Class with commonly used utility methods.<br>
 * <b>Note</b>: This class is meant for internal SDK use only and SDK users should not take a
 * dependency on it.
 */
public final class Util {
    private Util() {}

    /**
     * Closes a {@code Closeable} quietly, i.e. ignores any exceptions thrown when {@code close()}.
     * @param closeable The {@code Closeable} instance to be quietly closed.
     */
    public static void closeQuietly(@Nullable Closeable closeable) {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        } catch (Exception e) {
            // we don't care much
        }
    }
}
