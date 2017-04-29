package com.ashindigo.rpi.music.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class MusicClientMain {
	
	static File config = new File(System.getProperty("user.home") + "/rpiMConfig.conf");
	static File clientDb = new File(System.getProperty("user.home") + "\\musicDb.sql");
	static Connection connL;
	static Statement stmtL;
	static Connection connS;
	static Statement stmtS;
	static Scanner scanner = new Scanner(System.in);
	static UserLogin user;

	public static void main(String[] args) throws IOException, SQLException, ImproperConfigException {
		if (!config.exists()) {
			ClientConfig.createConfig();
		}
		
		clientDb.createNewFile();
		
		loadConfig();
		
		LoginManager.setupConnections();
		
		MusicLogger.log("Requesting Login Info");
		System.out.println("Welcome to the RPi music client!");
		System.out.println("Enter 'login' to login or enter 'create' to make a new account");
		switch (scanner.nextLine()) {
		case "login": System.out.println("Please enter your username: ");
						String username = scanner.nextLine();
						System.out.println("Please enter your password: ");
						String password = scanner.nextLine(); 
						System.out.println("Logging in");
						MusicLogger.log(username + " is attempting to login!");
						if (LoginManager.login(username, password)) { System.out.println("Login Successful: " + username); user = new UserLogin(username);	 break; } else { System.out.println("Login Failed Exiting"); System.exit(0); }
						
		case "create": 
			System.out.println("Please enter your account username");
				String username1 = scanner.nextLine();
				System.out.println("Please enter your password");
				String password1 = scanner.nextLine();
				System.out.println("Please enter your password again");
				String passwordV = scanner.nextLine();
				if (password1.equals(passwordV)) { System.out.println("Creating Account"); LoginManager.createUser(username1, password1); System.out.println("Logging in"); LoginManager.login(username1, password1); break; } else { System.out.println("Passwords dont match! Exiting"); System.exit(0); }
		}
		
		
		connL = DriverManager.getConnection("jdbc:sqlite://" + System.getProperty("user.home") + "\\musicDb.sql");
		stmtL = connL.createStatement();
		connS = DriverManager.getConnection("jdbc:postgresql://" + ClientConfig.configMap.get(ClientConfig.IP.name) + ":5432/music", "postgres", "IndigoMusic");
		stmtS = connS.createStatement();
		
		MusicLogger.log("Database Connections Established");
		
		MusicManager.scanMusicFolder();
		MusicManager.checkServerMusic();
		MusicManager.syncAllFiles();
	}

	
	public static void loadConfig() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(config));
		String line = null;
		while ((line = br.readLine()) != null) {
			if (!line.startsWith("#")) {
				for (int i = 0; ClientConfig.values().length > i; i++) {
					if (line.split("=")[0].equals(ClientConfig.values()[i].name)) {
						ClientConfig.configMap.put(ClientConfig.values()[i].name, line.split("=")[1]);
					}
				}
			}
		}
		
		br.close();
		MusicLogger.log("Config loaded Location: " + System.getProperty("user.home") + "/rpiMConfig.conf");
	}

}
