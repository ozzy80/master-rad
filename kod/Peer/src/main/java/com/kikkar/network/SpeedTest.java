package com.kikkar.network;

public interface SpeedTest {
	
	Long downloadSpeedTest(int measureTimeMilliseconds, int additionallyWaitForDataMilliseconds);
	
	Long uploadSpeedTest(int measureTimeMilliseconds, int additionallyWaitForDataMilliseconds);

}
