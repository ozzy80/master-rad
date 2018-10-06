package com.kikkar.global;

import java.util.TimeZone;

public class ClockSingleton {

	private static ClockSingleton firstInstance;
	private Long offsetValue;
	private TimeZone timeZone;

	private ClockSingleton() {
		offsetValue = 0l;
		timeZone = TimeZone.getTimeZone("Europe/Belgrade");
	}

	public static ClockSingleton getInstance() {
		if (firstInstance == null) {
			firstInstance = new ClockSingleton();
		}
		return firstInstance;
	}

	public Long getcurrentTimeMilliseconds() {
		long now = System.currentTimeMillis();
		return now + offsetValue + timeZone.getOffset(now);
	}

	public Long getOffsetValue() {
		return offsetValue;
	}

	public void setOffsetValue(Long offsetValue) {
		this.offsetValue = offsetValue;
	}
}
