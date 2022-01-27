package view;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import command.ServerCommand;
import gameController.Game;
import gameController.GameController;
import gameController.Player;

public class ServerThread extends Thread {
	public ArrayList<Player> userList = new ArrayList<Player>();
	public ServerCommand serverCommand;
	private Socket socket;

	public ServerThread(Socket socket) {

		try {
			this.socket = socket;
			this.serverCommand = new ServerCommand(socket);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		// Create a new GameController
		this.serverCommand.send(socket.getLocalAddress().toString());
		String[] connect_msg = this.serverCommand.receive("Connect");
		String player_name = connect_msg[1];
		Player player = new Player(player_name);
		userList.add(player);
		System.out.println(player_name + "  connect in");
//		GameController GameController = new GameController();
		StringBuffer stringBuffer = new StringBuffer();
		Collection<Game> games = GameController.getUserGame().values();
		stringBuffer.append(":");
		for (Game game : games) {
			stringBuffer.append(game).append(" ");
		}
		this.serverCommand.respond(stringBuffer.toString());
		Game game = null;
		while (true) {
			String[] msg = this.serverCommand.receive();
			if (msg[0].equals(this.serverCommand.CREATE)) {
				System.out.println(player_name + " create a new game");
				game = GameController.createOneGame(player_name, Integer.parseInt(msg[1]), this);
				serverCommand
						.send("Create;" + game.getGame_name() + ";" + game.getPlayer_num() + ";" + game.players.size());
				break;
			} else if (msg[0].equals(this.serverCommand.JOIN)) {

				switch (msg.length) {
					case 1:
						System.out.println(player_name + " Join a random game");
						game = GameController.join2game(player_name, this);
						if (game != null) {
							serverCommand.send(
									"Join;" + game.getGame_name() + ";" + 
											game.getPlayer_num() + ";" + game.players.size());
						} else {
							serverCommand.send("Error;no game to join");
						}
						break;
					default:
						game = GameController.join2game(player_name, msg[1], this);
						if (game == null) {
							game = GameController.join2game(
									player_name, Integer.parseInt(msg[1]), this);
						}
						if (game != null) {
							serverCommand.send(
									"Join;" + game.getGame_name() + ";" + 
											game.getPlayer_num() + ";" + game.players.size());
						} else {
							serverCommand.send("Error;no game to join");
						}
						break;
				}
				break;
			}
		}
		if (game != null && game.player_num == game.players.size()) {
			System.out.println(game.game_name + " is starting!");
			GameController.sendStartCommandToPlayers(game);
			GameController.sendFourCardToPlayer(game);
			while (!GameController.isGameOver(game)) {
				GameController.nextPersonTurn(game);
			}
			GameController.endGame(game);
		}
	}
}
