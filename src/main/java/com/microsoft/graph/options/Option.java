package com.microsoft.graph.options;

import javax.annotation.Nonnull;

/**
 * An option that is settable for a request
 */
public class Option {

    /**
     * The name of the option
     */
    private final String name;

    /**
     * The value of the option
     */
    private final Object value;

    /**
     * Creates an option object
     *
     * @param name  the name of the option
     * @param value the value of the option
     */
    protected Option(@Nonnull final String name, @Nonnull final Object value) {
        this.name = name;
        this.value = value;
    }

    /**
     * Gets the name of the option
     *
     * @return the name of the option
     */
    @Nonnull
    public String getName() {
        return name;
    }

    /**
     * Gets the value of the option
     *
     * @return the value of the option
     */
    @Nonnull
    public Object getValue() {
        return value;
    }
}