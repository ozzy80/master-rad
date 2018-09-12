package com.kikkar.global;

import java.util.TimeZone;

public class Clock {

	private static Clock firstInstance;
	private Long offsetValue;
	TimeZone belgrade = TimeZone.getTimeZone("Europe/Belgrade");

	private Clock() {
	}

	public static Clock getInstance() {
		if (firstInstance == null) {
			firstInstance = new Clock();
		}
		return firstInstance;
	}

	public Long getOffsetValue() {
		return offsetValue;
	}

	public void setOffsetValue(Long offsetValue) {
		this.offsetValue = offsetValue;
	}

	public Long getcurrentTimeMilliseconds() {
		long now = System.currentTimeMillis();
		return now + offsetValue + belgrade.getOffset(now);
	}

}
