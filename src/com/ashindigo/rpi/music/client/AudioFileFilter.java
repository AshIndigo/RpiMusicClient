package com.ashindigo.rpi.music.client;

import java.io.File;
import java.io.FilenameFilter;

public class AudioFileFilter implements FilenameFilter {

	@Override
	public boolean accept(File dir, String name) {
		if (name.endsWith(".mp3") || name.endsWith(".wav")) {
			return true;
		}
		return false;
	}

}
