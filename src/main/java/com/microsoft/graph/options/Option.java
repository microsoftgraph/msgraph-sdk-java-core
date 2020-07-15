package com.microsoft.graph.options;

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
    protected Option(final String name, final Object value) {
        this.name = name;
        this.value = value;
    }

    /**
     * Gets the name of the option
     *
     * @return the name of the option
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the value of the option
     *
     * @return the value of the option
     */
    public Object getValue() {
        return value;
    }
}