package com.microsoft.graph.options;

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
    public HeaderOption(final  String name, final String value) {
        super(name, value);
    }
}
