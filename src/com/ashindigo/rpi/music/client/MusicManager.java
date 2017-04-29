package com.ashindigo.rpi.music.client;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class MusicManager {
	
	public static File musicFolder = new File(ClientConfig.configMap.get("Directory"));
	static ArrayList<String> serverMusic = new ArrayList<String>();
	static ArrayList<String> clientMusic = new ArrayList<String>();
	static ArrayList<String> unsyncedMusic = new ArrayList<String>();

	public static void scanMusicFolder() throws ImproperConfigException, SQLException {
		if (musicFolder.isDirectory()) {
			for (int i = 0; musicFolder.listFiles(new AudioFileFilter()).length > i; i++) {
				ResultSet rs = MusicClientMain.stmtL.executeQuery("select * from music where songname = '" + musicFolder.listFiles(new AudioFileFilter())[i].getName().replaceAll("'", "''") + "'");
				if (!rs.next()) {
					MusicClientMain.stmtL.execute("INSERT INTO music(filepath, songname) values(" + "'" + musicFolder.listFiles(new AudioFileFilter())[i].getPath().replaceAll("'", "''") + "','" + musicFolder.listFiles(new AudioFileFilter())[i].getName().replaceAll("'", "''") + "');");
					MusicLogger.log("Music File Added to Local DB Name: " + musicFolder.listFiles(new AudioFileFilter())[i].getName());
				}
			}
		} else {
			throw new ImproperConfigException("Config Directory setting is a file! Location: " + ClientConfig.configMap.get("Directory"));
		}
	}

	public static void checkServerMusic() throws SQLException {
		
		ResultSet rsS = MusicClientMain.stmtS.executeQuery("SELECT * from " + MusicClientMain.user.getUsername());
		while (rsS.next()) {
			serverMusic.add(rsS.getString("songname"));
		}
		
		ResultSet rsC = MusicClientMain.stmtL.executeQuery("SELECT * from music");
		while (rsC.next()) {
			clientMusic.add(rsC.getString("songname"));
		}
		
		for (int i = 0; serverMusic.size() > i; i++) {
			if (!clientMusic.contains(serverMusic.get(i))) {
				unsyncedMusic.add(serverMusic.get(i));
				MusicLogger.log("Unsynced Song! Name: " + serverMusic.get(i));
			}
		}
		
		
	}

	public static void syncAllFiles() throws UnknownHostException, IOException {
		for (int i = 0; unsyncedMusic.size() > i; i++) {
			ServerHelper.requestFile(22556, unsyncedMusic.get(i) + "\n");
		}
	}

}
