package com.ashindigo.rpi.music.client;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class ServerHelper {

	@SuppressWarnings("resource")
	public static void requestFile(int port, String name) throws UnknownHostException, IOException {
		Socket sock = new Socket(ClientConfig.configMap.get(ClientConfig.IP.name), port); // Client  Socket
		MusicLogger.log("Connection Sent " + sock.getInetAddress());
		ServerSocket sockS = new ServerSocket(25566);
		Socket sockO = sockS.accept(); // Server Socket
		MusicLogger.log("Established Duel-way connection");
		PrintWriter pw = new PrintWriter(sockO.getOutputStream());
		pw.write(MusicClientMain.user.getUsername());
		pw.flush();
		pw.close();
		ServerSocket sockS2 = new ServerSocket(25566);
		Socket sockO2 = sockS.accept();
		PrintWriter pw2 = new PrintWriter(sockO2.getOutputStream());
		pw.write(name);
		pw.flush();
		pw.close();
		DataInputStream dis = new DataInputStream(sock.getInputStream());
		File file = new File(ClientConfig.configMap.get(ClientConfig.DIR.name), name.replace("\n", ""));
		System.out.println(file);
		file.createNewFile();
		FileOutputStream fos = new FileOutputStream(file);
		byte[] buffer = new byte[4096];

		int filesize = Integer.MAX_VALUE / 2; // Send file size in separate msg
		int read = 0;
		int remaining = filesize;
		while ((read = dis.read(buffer, 0, Math.min(buffer.length, remaining))) > 0) {
			remaining -= read;
			fos.write(buffer, 0, read);
		}

		fos.close();
		dis.close();
		MusicLogger.log("File Received: " + file);
		sockS.close();
	}

}
