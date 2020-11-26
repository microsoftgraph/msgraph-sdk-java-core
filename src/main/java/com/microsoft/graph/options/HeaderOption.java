package com.microsoft.graph.options;

import javax.annotation.Nonnull;

/**
 * A header value
 */
public class HeaderOption extends Option {

    /**
     * Creates a header option object
     *
     * @param name  the name of the header
     * @param value the value of the header
     */
    public HeaderOption(@Nonnull final String name, @Nonnull final String value) {
        super(name, value);
    }
}
