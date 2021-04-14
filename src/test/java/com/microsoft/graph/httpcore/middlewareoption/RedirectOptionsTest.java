package com.microsoft.graph.httpcore.middlewareoption;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;

import okhttp3.Response;

public class RedirectOptionsTest {
    @Test
    public void constructorDefensiveProgramming() {
        assertThrows(IllegalArgumentException.class, () -> {
            new RedirectOptions(RedirectOptions.MAX_REDIRECTS +1, null);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new RedirectOptions(-1, null);
        });
    }
    @Test
    public void defaultShouldRedirectValue() {
        RedirectOptions options = new RedirectOptions();
        assertEquals(options.shouldRedirect(), RedirectOptions.DEFAULT_SHOULD_REDIRECT);
    }
    @Test
    public void defaultShouldRedirectIsTrue() {
        Response response = mock(Response.class);
        assertTrue(RedirectOptions.DEFAULT_SHOULD_REDIRECT.shouldRedirect(response));
    }
}
