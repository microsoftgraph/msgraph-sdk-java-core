package com.microsoft.graph.serializer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.jupiter.api.Test;

@SuppressFBWarnings
public class DurationTests {

	@Test
	public void testDurationSerializer() throws Exception {
        String strDuration = DatatypeFactory.newInstance().newDurationDayTime(true, 0, 2, 30, 0).toString();
        assertEquals("P0DT2H30M0S", strDuration);
    }

	@Test
    public void testDurationDeserializer() throws Exception {
        Duration duration = DatatypeFactory.newInstance().newDurationDayTime(true, 0, 1, 30, 45);
        assertEquals(0, duration.getMonths());
        assertEquals(0, duration.getDays());
        assertEquals(1, duration.getHours());
        assertEquals(30, duration.getMinutes());
        assertEquals(45, duration.getSeconds());
    }

}
