package command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import gameController.GameController;

public class ServerCommand implements ServerProtocol {
	public final String CREATE = "Create";
	public final String JOIN = "Join";
	public final String RESPOND = "Respond";
	public final String GIVE = "Give";
	public final String TURN = "Turn";
	public final String MOVE = "Move";
	public final String SWAP = "Swap";
	public final String SKIP = "Skip";
	public final String END = "End";
	public final String EXCEPTION = "Exception";
	BufferedReader bufferedReader;
	PrintWriter printWriter;
	public GameController gameController;

	public ServerCommand(Socket socket) {
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
	public void respond(String gameList) {
		String a = RESPOND + ";" + gameList;
		send(a);
	}

	@Override
	public void give(String player, String tiles) {
		String a = GIVE + ";" + player + ";" + tiles + ";";
		send(a);
	}

	@Override
	public void turn(String player) {
		send(TURN + ";" + player);
	}

	@Override
	public void move(String player, String tile, String index) {
		send(MOVE + ";" + player + ";" + tile.toString() + ";" + index);
	}

	@Override
	public void swap(String player, String tile) {
		send(SWAP + ";" + player + ";" + tile.toString());
	}

	@Override
	public void skip(String player) {
		send(SKIP + ";" + player);
	}

	@Override
	public void end(String player, String... players) {
		send(END + ";" + player + ";");
	}

	@Override
	public void exception(String message) {
		send(EXCEPTION + ";" + message);
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
		if (strings != null) {
			return strings;
		} else {
			throw new RuntimeException();
		}
	}
}
