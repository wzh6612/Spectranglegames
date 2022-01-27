package view;

import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;
import command.ClientCommand;
import gameController.Player;
import model.GameBoard;
import model.Bag;

import java.io.IOException;

public class Client {

	static int server_port = 2019;
	public static ClientCommand clientCommand;
	public final static String IP_ADDR = "localhost";

	public static void main(String[] args) {
		Socket socket = null;
		Bag bag = new Bag();
		GameBoard board = new GameBoard();
		int flag = 0;
		ArrayList<String> handCards = new ArrayList<>();
		try {
			socket = new Socket(IP_ADDR, server_port);
			clientCommand = new ClientCommand(socket);

		} catch (IOException e) {
			e.printStackTrace();
		}
		while (true) {
			String[] glist = clientCommand.receive();
			if (glist != null) {
				break;
			}
		}

		String name = getInput("Please enter  your name.");
		Player player = new Player(name);
		String num;
		clientCommand.connect(player);
		String[] massage = clientCommand.receive();
		while (true) {
			System.out.println(massage[1]);
			System.out.println("Create a new game: please enter create + number");
			System.out.println("Join a new game: please enter join + "
					+ "game_name or join + player_number");
			String createOrJoin = getInput("please enter:");
			if (createOrJoin.split(" ")[0].equals("join")) {
				if (createOrJoin.split(" ").length < 2) {
					clientCommand.join();
				} else {
					clientCommand.join(createOrJoin.split(" ")[1]);
				}
				massage = clientCommand.receive();
				if (massage[0].equals("Join")) {
					System.out.println("join the game, game name:" + 
							massage[1] + "; player number:" + massage[2]);
					num = massage[2];
					break;
				} else {
					System.out.println("no game to join");
					continue;
				}
			} else if (createOrJoin.split(" ")[0].equals("create")) {
				num = createOrJoin.split(" ")[1];
				clientCommand.create(num);
				massage = clientCommand.receive("Create");
				System.out.println("Create a game:");
				System.out.println("game name:" + massage[1] + " player number:" + massage[2]);
				num = massage[2];
				System.out.println("Waiting for other players...");
				break;
			} else {
				System.out.println("command error");
			}
		}
		massage = clientCommand.receive("Start");
//		while (massage.length < 2 || !massage[1].equals(num)) {
//			System.out.println("We are still waiting for palyer to start a game");
//		}
		System.out.println("++++++++++" + "Game start" + "++++++++++");
		System.out.println("Current Player:" + massage.toString());
		// reset game board
		bag = new Bag();
		board = new GameBoard();
		handCards = new ArrayList<>();
		// receive the initial four cards
		massage = clientCommand.receive("Give");
		if (name.equals(massage[1])) {
			handCards.add(massage[2]);
			bag.take(massage[2]);
			handCards.add(massage[3]);
			bag.take(massage[3]);
			handCards.add(massage[4]);
			bag.take(massage[4]);
			handCards.add(massage[5]);
			bag.take(massage[5]);
		}
		System.out.println("Your initial hand cards are:\n" + 
				handCards.get(0) + "\n" + handCards.get(1) + "\n"
				+ handCards.get(2) + "\n" + handCards.get(3));
		int card_in_bag = 36 - Integer.parseInt(num) * 4;
		try {
			while (!board.isGameOver()) {
				// receive Turn or End command from server
				if ((!massage[0].equalsIgnoreCase("Turn")) && (card_in_bag > 0)) {
					massage = null;
					massage = clientCommand.receive();
					if (massage != null && massage[0].equals("End")) {
						String msg = "";
						for (int i = 0; i < Integer.parseInt(num); i++) {
							msg = msg + massage[i + 1] + "\n";
						}
						System.out.println("Game Over~\nscore list: \n" + msg);
						break;
					}
					if (massage != null && massage[0].equals("Error")) {
						throw new RuntimeException();
					}
				}

				// Process others turn
				while (!massage[1].equals(name)) {
					// receive others move, when other player choose skip, a Turn command will be
					// received here
					String[] others_move = clientCommand.receive();
					if (others_move[0].equals("Turn")) {
						if (others_move[1].equals(name)) {
							break;
						}
						// eg:player1 Skip, player2 Skip, it's player3's turn, player1 will enter 
						// the following loop
						while (others_move[0].equals("Turn") && !others_move[1].equals(name)) {
							others_move = clientCommand.receive();
							if (others_move[0].equals("Move")) {
								board.putTile(others_move[3], Integer.parseInt(others_move[2]), 
										others_move[1]);
								System.out.println("Others moved, flush game board");
								board.show();
								others_move = clientCommand.receive();
								if (others_move[0].equals("Give")) {
									// read Turn
									others_move = clientCommand.receive();
								}
							}
						}
						break;
					}

					System.out.println("Others moved, flush game board");
					board.putTile(others_move[3], Integer.parseInt(others_move[2]), others_move[1]);
					board.show();

					// receive boardcast Give command from server
					if (card_in_bag > 0) {
						String[] others_give = clientCommand.receive();
						if (others_give[0].equals("End")) {
							String score_list = "";
							for (int i = 0; i < Integer.parseInt(num); i++) {
								score_list = score_list + others_give[i + 1] + "\n";
							}
							System.out.println("Game Over~\nscore list: \n" + score_list);
							massage[0] = "End";
							break;
						}
						if (others_give[0].equals("Turn")) {
							if (others_give[1].equals(name)) {
								break;
							} else {
								continue;
							}
						}
						bag.take(others_give[2]);
						System.out.println(others_give[1] + " received card:" + others_give[2]);
						card_in_bag--;
					}

					// reveiced Turn or End from server
					massage = clientCommand.receive();
					if (massage != null && massage[0].equals("End")) {
						String msg = "";
						for (int i = 0; i < Integer.parseInt(num); i++) {
							msg = msg + massage[i + 1] + "\n";
						}
						System.out.println("Game Over~\nscore list: \n" + msg);
						break;
					}

				}
				if (massage[0].equals("End")) {
					break;
				}
				board.show();

				System.out.println("It is your turn");
				// show handcards
				String hcs = "";
				for (String s : handCards) {
					hcs = hcs + s + " , ";
				}
				System.out.println("Your hand cards are:\n" + hcs);

				// show recomment movements
				StringBuffer reco = new StringBuffer();
				for (String card : handCards) {
					String recommend = board.recommand(card);
					reco.append("card:").append(card).append(" can Move ").
						append(recommend).append("\n");
				}
				System.out.println("Recommend:\n" + reco.toString());
				System.out.println(
						"the first charactor in tile encoding represents rotate degree."
						+ "\n 0->0   degree \t 1->60  degree \t 2->120 degree "
						+ "\n 3->180 degree \t 4->240 degree \t 5->300 degree \n ");

				// execute Move or Skip command
				String moveOrSkip = getInput(
						"Please decide move or skip, then type "
						+ "\"Move <index> <tile encoding>\"  Or  \"Skip [tile encoding]\"");
				while (!(isMoveLegal(moveOrSkip, handCards) || 
						moveOrSkip.split(" ")[0].equals("Skip"))) {
					moveOrSkip = getInput(
							"Your input is illegal, please retype "
							+ "\"Move <index> <tile encoding>\"  Or  \"Skip [tile encoding]\"");
				}

				String[] moveStr = moveOrSkip.split(" ");
				if (moveStr[0].equals("Move")) {
					clientCommand.move(moveStr[1], moveStr[2]);
					board.putTile(moveStr[2], Integer.parseInt(moveStr[1]), name);
					board.show();
					delFromHandCards(handCards, moveStr[2]);
					String[] strings = clientCommand.receive();
				} else if (moveStr[0].equals("Skip")) {
					if (moveStr.length > 1) {
						clientCommand.skip(moveStr[1]);
						delFromHandCards(handCards, moveStr[1]);
						massage = clientCommand.receive();
						if (massage[0].equals("Give") && massage[1].equals(name)) {
							handCards.add(massage[2]);
						} else {
							System.out.println("Broadcast Give:" + " " + massage.toString());
						}
						continue;
					} else {
						clientCommand.skip();
						// read other's turn
						massage = clientCommand.receive();
						if (massage[0].equals("End")) {
							String score_list = "";
							for (int i = 0; i < Integer.parseInt(num); i++) {
								score_list = score_list + massage[i + 1] + "\n";
							}
							System.out.println("Game Over~\nscore list: \n" + score_list);
							break;
						}
						continue;
					}
				}

				// receive Give command from server
				massage = clientCommand.receive();
				if (massage[0].equals("Give")) {
					String card = massage[2];
					bag.take(card);
					handCards.add(card);
					System.out.println("You have received card:" + card);
					card_in_bag--;
					if (card_in_bag == 0) {
						massage = clientCommand.receive();
					}
				} else {
					flag++;
					if (massage[0].equals("End")) {
						String score_list = "";
						for (int i = 0; i < Integer.parseInt(num); i++) {
							score_list = score_list + massage[i + 1] + "\n";
						}
						System.out.println("Game Over~\nscore list: \n" + score_list);
						break;
					}
				}

				Thread.sleep(500);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Game Error, please restart");
		} 

		
	}

	/**.
	 * check move
	 *
	 * @param str
	 * @return
	 */
	private static boolean isMoveLegal(String str, ArrayList handCards) {
		String[] moveStr = str.split(" ");
		if (moveStr != null && moveStr.length > 0) {
			if (moveStr[0].equals("Move") && moveStr.length == 3) {
				if (isCardLegal(moveStr[2], handCards)) {
					return true;
				}
			}
		}
		return false;
	}

	/**.
	 * check card
	 *
	 * @param card
	 * @param handCards
	 * @return
	 */
	private static boolean isCardLegal(String card, ArrayList<String> handCards) {
		for (String c : handCards) {
			if (c.substring(1, 5).equals(card.substring(1, 5))) {
				return true;
			}
		}
		return false;
	}

	/**.
	 * delect card from handcards
	 *
	 * @param handCards
	 * @param card
	 */
	private static void delFromHandCards(ArrayList<String> handCards, String card) {
		String tobedel = "";
		for (String s : handCards) {
			if (s.substring(1, 5).equals(card.substring(1, 5))) {
				tobedel = s;
			}
		}
		handCards.remove(tobedel);
	}

	public static String getInput(String hint) {
		System.out.println(hint + "\n");
		Scanner scanner = new Scanner(System.in);
		String info = scanner.nextLine();
//		scanner.close();
		return info;
	}

}
