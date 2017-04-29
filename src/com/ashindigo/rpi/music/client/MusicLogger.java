package com.ashindigo.rpi.music.client;

import java.time.LocalDateTime;

public class MusicLogger {

	public static void log (String str) {
		System.out.println("[" + getTime() + "] RPi Music Client (" +  Thread.currentThread().getName() + "): " + str);
	}
	
	public static String getTime() {
		return LocalDateTime.now().getHour() + ":" + LocalDateTime.now().getMinute() + ":" + LocalDateTime.now().getSecond();
	}
}
