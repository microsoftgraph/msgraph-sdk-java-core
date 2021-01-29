package com.microsoft.graph.logger;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Test cases for {@see DefaultLogger}
 */
public class DefaultLoggerTests {

	@Test
    public void testLoggerLevel(){
        ILogger logger = new DefaultLogger();
        assertEquals(LoggerLevel.ERROR, logger.getLoggingLevel());
        logger.setLoggingLevel(LoggerLevel.DEBUG);
        assertEquals(LoggerLevel.DEBUG, logger.getLoggingLevel());
    }
}
