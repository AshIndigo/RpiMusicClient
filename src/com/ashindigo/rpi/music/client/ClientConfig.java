package com.ashindigo.rpi.music.client;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public enum ClientConfig {
	
	IP("IP-Address", "localhost", "The IP Address of the RPi hosting the server"),
	DIR("Directory", System.getProperty("user.home") + File.pathSeparator + "Music" + File.pathSeparator, "The directory that music files are downloaded to"),
	;
	
	public String name;
	public String defaultValue;
	public String comment;
	
	public static String ip;
	public static String dir;
	
	public static HashMap<String, String> configMap = new HashMap<String, String>();
	public static File config = new File(System.getProperty("user.home") + File.pathSeparator + "rpiMConfig.conf");

	ClientConfig(String name, String defaultValue, String comment) {
		this.name = name;
		this.defaultValue = defaultValue;
		this.comment = comment;
	}
	
	public static void createConfig() throws IOException {
		config.createNewFile();
		FileWriter fw = new FileWriter(config);
		for (int i = 0; ClientConfig.values().length > i; i++) {
			String text1 = "# " + ClientConfig.values()[i].name + ":" + ClientConfig.values()[i].comment + " (Default: " + ClientConfig.values()[i].defaultValue + ")"; 
			fw.write(text1);
			fw.write(System.lineSeparator());
			String text2 = ClientConfig.values()[i].name + "=" + ClientConfig.values()[i].defaultValue;
			fw.write(text2);
			fw.write(System.lineSeparator());
			fw.write(System.lineSeparator());
		}
		fw.close();
		MusicLogger.log("New Config Generated");
	}
}
