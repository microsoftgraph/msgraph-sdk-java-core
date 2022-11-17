package com.microsoft.graph.models;

public interface IProgressCallback {

    /**
     * How progress updates are handled for this callback
     *
     * @param current the current amount of progress
     * @param max     the max amount of progress
     */
    void progress(final long current, final long max);
}
