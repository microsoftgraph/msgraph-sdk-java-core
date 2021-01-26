package com.microsoft.graph;

import java.io.Closeable;
import java.io.IOException;

public final class Util {
    private Util() {}

    public static void closeQuietly(Closeable closeable) {
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
