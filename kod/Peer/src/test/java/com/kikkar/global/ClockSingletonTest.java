package com.kikkar.global;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.TimeZone;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ClockSingletonTest {

	private ClockSingleton clockSingleton;

	@BeforeEach
	void createClock() {
		clockSingleton = ClockSingleton.getInstance();
	}

	@Test
	void testGetInstance_isClassSingleton() {
		assertEquals(clockSingleton, ClockSingleton.getInstance());
		assertEquals(clockSingleton, ClockSingleton.getInstance());
		assertEquals(ClockSingleton.getInstance(), ClockSingleton.getInstance());
	}

	@ParameterizedTest
	@ValueSource(longs = { 31800l, 3000l, 300l, 0l, -31800l, -3000l, -300l })
	void testGetCurrentTimeMilliseconds_checkSinhronizationWithDifferentClockMove(Long lateMillisecond) {
		final Long MAX_DIFFERENCE = 1000l;
		clockSingleton.setOffsetValue(lateMillisecond);

		Long systemTime = System.currentTimeMillis();
		systemTime += TimeZone.getTimeZone("Europe/Belgrade").getOffset(systemTime);
		Long clockTime = clockSingleton.getcurrentTimeMilliseconds();

		assertTrue(systemTime + lateMillisecond <= clockTime);
		assertTrue(systemTime + lateMillisecond + MAX_DIFFERENCE >= clockTime);
	}

}
