package com.microsoft.graph.core.models;

/**
 * Defines how to return progress status from a request.
 */
@FunctionalInterface
public interface IProgressCallback {

    /**
     * How progress updates are handled for this callback
     *
     * @param current the current amount of progress
     * @param max     the max amount of progress
     */
    void report(final long current, final long max);
}
