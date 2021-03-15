package com.microsoft.graph.options;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

public class OptionTests {

	@Test
	public void testOption() {
        FunctionOption functionOption = new FunctionOption("f1","fv1");
        assertEquals("f1",functionOption.getName());
        assertEquals("fv1",functionOption.getValue());

        HeaderOption headerOption = new HeaderOption("h1","hv1");
        assertEquals("h1",headerOption.getName());
        assertEquals("hv1",headerOption.getValue());

        QueryOption queryOption = new QueryOption("q1","qv1");
        assertEquals("q1",queryOption.getName());
        assertEquals("qv1",queryOption.getValue());

        Option option = new Option("o1","ov1");
        assertEquals("o1",option.getName());
        assertEquals("ov1",option.getValue());

        assertThrows(NullPointerException.class, () -> {
            new Option(null, "onion");
        }, "should fail on null name");
        assertThrows(IllegalArgumentException.class, () -> {
            new Option("", "onion");
        }, "should fail on empty name");
    }

}
