package com.ashindigo.rpi.music.client;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class LoginManager {
	
	static Connection connU;
	static Statement stmtU;

	public static void setupConnections() throws SQLException {
		connU = DriverManager.getConnection("jdbc:postgresql://" + ClientConfig.configMap.get(ClientConfig.IP.name) + ":5432/users", "postgres", "IndigoMusic");
		stmtU = connU.createStatement();
	}
	
	/**
	 * Creates a new user, usernames can't be the same
	 * @param username
	 * @param password
	 * @throws SQLException 
	 * @throws IOException 
	 */
	public static void createUser(String username, String password) throws SQLException, IOException {
		stmtU.execute("INSERT INTO MUSIC(username, password) values('" + username + "','" + new PasswordAuthentication(16).hash(password.toCharArray()) + "')");
		MusicClientMain.stmtS.execute("create table " + username + "(id integer PRIMARY KEY AUTOINCREMENT, filepath text, songname text);");
		new File(ClientConfig.configMap.get(ClientConfig.DIR.name) + File.pathSeparator + username).createNewFile();
		MusicLogger.log("New account created: " + username);
	}
	
	/**
	 * Logins to the users profile
	 * @param username
	 * @param password
	 * @return If the login was successful
	 * @throws SQLException 
	 */
	public static boolean login(String username, String password) throws SQLException {
		ResultSet rs = stmtU.executeQuery("select * from music where username = " + username);
		rs.next();
		return new PasswordAuthentication(16).authenticate(password.toCharArray(), rs.getString("password"));
	}

}
