package gameController;

import model.Bag;
import model.GameBoard;
import model.SpectrangleTile;
import view.ServerThread;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class GameController {
	// username -> game Instance
	private static Map<String, Game> userGame = new HashMap<>();

	// player_no -> game Instance
	private static Map<Integer, ArrayList<Game>> mun_games_map = new HashMap<>();

	// username -> thread
	private static Map<String, ServerThread> userHolder = new HashMap<>();

	public static Map<String, Game> getUserGame() {
		return userGame;
	}

	/**
	 * . create one game
	 */
	// @ requires \result != null;
	// @ ensures username != null && player_num != null && thread != null;
	public synchronized static Game createOneGame(String username, int player_num, 
			ServerThread thread) {
		Game game = new Game(username, player_num, thread);
		userGame.put(username, game);
		userHolder.put(username, thread);
//        game.add_one_player(username);
		if (mun_games_map.get(player_num) == null) {
			ArrayList<Game> games = new ArrayList<Game>();
			games.add(game);
			mun_games_map.put(player_num, games);
		} else {
			mun_games_map.get(player_num).add(game);
		}
		return game;
	}

	// random join a game
	// @ ensures mem_num < 4;
	public static synchronized Game join2game(String username, ServerThread thread) {
		Iterator<Game> games = userGame.values().iterator();
		if (games.hasNext()) {
			Game game = games.next();
			userHolder.put(username, thread);
			if (game.add_one_player(username)) {
				// start the game
				return game;
			} else {
				// still waiting
				return game;
			}
		} else {
			System.out.println("no game can join!");
			return null;
		}
	}

	// join a game by game name
	public static synchronized Game join2game(String username, String game_name, 
			ServerThread thread) {
		Game game = userGame.get(game_name);
		userHolder.put(username, thread);
		if (game != null) {
			if (game.add_one_player(username)) {
				// start the game
				return game;
			} else {
				// still waiting
				return game;
			}
		} else {
			System.out.println("no game can join!");
			return null;
		}
	}

	// join a game by player num
	public static synchronized Game join2game(String username, int mem_num, ServerThread thread) {
		Iterator<Game> games = mun_games_map.get(mem_num).iterator();
		if (games.hasNext()) {
			Game game = games.next();
			userHolder.put(username, thread);
			if (game.add_one_player(username)) {
				// start the game
				return game;
			} else {
				// still waiting
				return game;
			}
		} else {
			System.out.println("no game can join!");
			return null;
		}
	}

	/**.
	 * start one game
	 */
	// @ pure
	// @ ensures game != null;
	public static void startOneGame(Game game) {

		Bag bag = new Bag();
		GameBoard board = new GameBoard();
		game.setBag(bag);
		game.setBoard(board);
	}

	/**.
	 * send players to all players
	 *
	 * @param game
	 */
	// @ ensures game !-= null;
	public static void sendStartCommandToPlayers(Game game) {
		ArrayList<String> userList = game.players;
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("Start;");
		for (String user : userList) {
			stringBuffer.append(user).append(";");
		}
		String users = stringBuffer.toString().trim();
		System.out.println(users);
		for (String user : userList) {
			userHolder.get(user).serverCommand.send(users);
		}
	}

	/**.
	 * send initial four cards to all players
	 *
	 * @param game
	 */
	// @ pure
	public static void sendFourCardToPlayer(Game game) {
		ArrayList<String> userList = game.players;
		Bag bag = game.bag;
		for (String user : userList) {
			SpectrangleTile fir = bag.randomTakeOne();
			SpectrangleTile sec = bag.randomTakeOne();
			SpectrangleTile thr = bag.randomTakeOne();
			SpectrangleTile fot = bag.randomTakeOne();
			String cardList = fir.toString() + ";" + sec.toString() + ";" + 
					thr.toString() + ";" + fot.toString();
			userHolder.get(user).serverCommand.give(user, cardList);
		}
	}

	/**.
	 * game process: Turn -> Move ->Give
	 *
	 * @param game
	 */
	public static void nextPersonTurn(Game game) {
		try {
			Bag bag = game.getBag();
			GameBoard board = game.getBoard();
			ArrayList<String> userList = game.players;
			int game_player_num = userList.size();
			int nextPersonIdx = game.round % game_player_num;
			String nextPersonName = userList.get(nextPersonIdx);
			for (String user : userList) {
				userHolder.get(user).serverCommand.turn(nextPersonName);
			}

			String[] strings = userHolder.get(nextPersonName).serverCommand.receive();
			System.out.println("server:" + strings.toString());
			if (strings[0].equals("Move")) {
				board.putTile(strings[2], Integer.parseInt(strings[1]), nextPersonName);
				board.show();
				for (String user : userList) {
					userHolder.get(user).serverCommand.move(nextPersonName, strings[1], strings[2]);
				}
			} else if (strings[0].equals("Skip")) {
				if (strings.length > 1) {
					bag.putBack(strings[1]);
					SpectrangleTile spectrangleTile = bag.randomTakeOne();
					String tile = spectrangleTile.toString();
					userHolder.get(nextPersonName).serverCommand.give(nextPersonName, tile);
				}
				// update the next player
				game.round++;
				return;

			}

			if (!bag.isEmpty()) {
				SpectrangleTile tile = bag.randomTakeOne();
				String cardList = tile.toString();
				for (String user : userList) {
					userHolder.get(user).serverCommand.give(nextPersonName, cardList);
				}
			}

			// update the next player
			game.round++;

		} catch (Exception e) {
			// if a player's program down, send error to all players and clean the game
			sendErrorToAllPlayers(game, "a player left the game, please restart");
		}
	}

	/**.
	 * send error to all players and clean the game
	 *
	 * @param game
	 * @param errorMsg
	 */
	public static void sendErrorToAllPlayers(Game game, String errorMsg) {
		ArrayList<String> userList = game.players;
		Error error = new Error();
		for (String user : userList) {
			userHolder.get(user).serverCommand.exception(errorMsg);
		}
	}

	/**.
	 * end game, send score list to all players
	 *
	 * @param game
	 */
	public static void endGame(Game game) {
		ArrayList<String> userList = game.players;
		Bag bag = game.getBag();
		GameBoard board = game.getBoard();
		Map<String, Integer> map = board.scoreAllUsers();
		StringBuffer sb = new StringBuffer();
		for (String user : userList) {
			int score = map.get(user);
			sb.append(user).append(",").append(score).append(" ");
		}
		String scoreList = sb.toString().trim();
		for (String user : userList) {
			userHolder.get(user).serverCommand.end(scoreList);
			userHolder.remove(user);
		}
		userGame.remove(game.game_name);
	}

	/**.
	 * check whether the game is over
	 *
	 * @param game
	 * @return
	 */
	public static boolean isGameOver(Game game) {
		Bag bag = game.getBag();
		GameBoard board = game.getBoard();
		return board.isGameOver();
	}

	/**.
	 * check whether the bag is empty
	 *
	 * @param game
	 * @return
	 */
	public static boolean isEmpty(Game game) {
		Bag bag = game.getBag();
		return bag.isEmpty();
	}
}
