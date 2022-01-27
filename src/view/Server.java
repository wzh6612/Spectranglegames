package view;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

	public static final int PORT = 2019;
	public static void main(String[] args) {
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(PORT);
		} catch (IllegalArgumentException e) {
			System.out.println(
					"ERROR: illegal port num " + PORT + ". Port should between 0 and 65535");
			System.exit(0);
		} catch (IOException e) {
			System.out.println(
					"ERROR: the port " + PORT + " is already in use, please change another one");
		}
		System.out.println("==========The server is working now==========");

		while (true) {
			try {
				Socket toClient = serverSocket.accept();
				System.out.println(toClient.getInetAddress());
				ServerThread serverThread = new ServerThread(toClient);
				serverThread.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
