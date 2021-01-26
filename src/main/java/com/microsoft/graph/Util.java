package com.microsoft.graph;

import java.io.Closeable;

import javax.annotation.Nullable;

public final class Util {
    private Util() {}

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
