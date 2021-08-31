package com.microsoft.graph.httpcore.middlewareoption;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class RetryOptionsTest {
    @Test
    public void constructorDefensiveProgramming() {
        assertThrows(IllegalArgumentException.class, () -> {
            new RetryOptions(null, RetryOptions.MAX_RETRIES +1, 0);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new RetryOptions(null, RetryOptions.MAX_RETRIES, RetryOptions.MAX_DELAY +1);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new RetryOptions(null, RetryOptions.MAX_RETRIES, -1);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new RetryOptions(null, -1, RetryOptions.MAX_DELAY);
        });
    }
    @Test
    public void defaultShouldRetryValue() {
        RetryOptions options = new RetryOptions();
        assertEquals(options.shouldRetry(), RetryOptions.DEFAULT_SHOULD_RETRY);
    }
}
