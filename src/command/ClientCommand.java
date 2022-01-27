package command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import gameController.Player;

public class ClientCommand implements ClientProtocol {
	public PrintWriter printWriter;
	public BufferedReader bufferedReader;
	public final String CONNECT = "Connect";
	public final String CREATE = "Create";
	public final String DISCONNECT = "Disconnect";
	public final String JOIN = "Join";
	public final String LEAVE = "Leave";
	public final String SWAP = "Swap";
	public final String MOVE = "Move";
	public final String SKIP = "Skip";
	public final String START = "Start";
	public final String RESPOND = "Respond";

	public ClientCommand(Socket socket) {
		try {
			InputStream inputstream = socket.getInputStream();
			InputStreamReader input = new InputStreamReader(inputstream);
			this.bufferedReader = new BufferedReader(input);
			OutputStream output = socket.getOutputStream();
			this.printWriter = new PrintWriter(output);
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	@Override
	public void connect(Player player) {
		String msg = CONNECT + ";" + player.getName() + ";";
		send(msg);

	}

	@Override
	public void create() {
		String msg = CREATE + ";";
		send(msg);
	}

	@Override
	public void create(String maxPlayers) {
		String msg = CREATE + ";" + maxPlayers + ";";
		send(msg);
	}

	@Override
	public void join() {
		String msg = JOIN + ";";
		send(msg);
	}

	@Override
	public void join(String game) {
		String msg = JOIN + ";" + game + ";";
		send(msg);
	}

	@Override
	public void start() {
		String msg = START + ";";
		send(msg);
	}

	@Override
	public void move(String tile, String index) {
		String msg = MOVE + ";" + tile + ";" + index;
		send(msg);
	}

	@Override
	public void swap(String tile) {
		String msg = SWAP + ";" + tile;
		send(msg);
	}

	@Override
	public void skip() {
		send(SKIP + ";");
	}

	@Override
	public void skip(String username) {
		send(SKIP + ";" + username + ";");
	}

	@Override
	public void leave() {
		send(LEAVE);
	}

	@Override
	public void disconnect() {
		send(DISCONNECT);
	}

	@Override
	public void send(String command) {
		this.printWriter.print(command + "\n");
		this.printWriter.flush();
	}

	@Override
	public String[] receive(String command) {
		String[] result = receive();
		if (result != null && result[0].equals(command)) {
			return result;
		} else {
			throw new RuntimeException();
		}
	}

	@Override
	public String[] receive() {
		String result = "";
		try {
			result = bufferedReader.readLine();
			System.out.println("receive massage from server:" + result);
		} catch (IOException e) {
			throw new RuntimeException();
		}
		String[] strings = result.split(";");
		return strings;
	}
}
