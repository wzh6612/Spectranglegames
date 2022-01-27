package gameController;

import java.util.ArrayList;

import model.Bag;
import model.GameBoard;
import view.ServerThread;

public class Game {
	public String game_name;
	public int player_num;
	public int round;
	public ArrayList players;
	public ServerThread game_thread;
	public Bag bag;
	public GameBoard board;

	Game(String userName, int playerNum, ServerThread thread) {
		this.game_name = userName;
		this.player_num = playerNum;
		this.game_thread = thread;
		this.players = new ArrayList<String>();
		this.players.add(userName);
		this.bag = new Bag();
		this.board = new GameBoard();
		this.round = 0;
	}
	//@ensures userName != null;
	public Boolean add_one_player(String userName) {
		this.players.add(userName);

		System.out.println("The room has" + this.players.size());
		if (this.players.size() == this.player_num) {
			return true;
		} else {
			return false;
		}
	}

	public int getRound() {
		return round;
	}
	//@ ensures round > 0;
	public void setRound(int round) {
		this.round = round;
	}
	//@ pure;
	public ArrayList getPlayers() {
		return players;
	}
	//@ ensures players != null;
	public void setPlayers(ArrayList players) {
		this.players = players;
	}
	//@ pure;
	public Bag getBag() {
		return bag;
	}

	public void setBag(Bag bag) {
		this.bag = bag;
	}

	public GameBoard getBoard() {
		return board;
	}
	//@ensures board != null;
	public void setBoard(GameBoard board) {
		this.board = board;
	}
	//@ pure;
	public String getGame_name() {
		return game_name;
	}
	
	public void setGame_name(String game_name) {
		this.game_name = game_name;
	}
	//@ requires \result <= 4;
	public int getPlayer_num() {
		return player_num;
	}

	@Override
	public String toString() {
		return "Game{" + "game_name='" + game_name + '\'' + ", player_num=" + player_num + '}';
	}
	//@ ensures player_num >=2 && player_num <= 4;
	public void setPlayer_num(int player_num) {
		this.player_num = player_num;
	}

	public Thread getGame_thread() {
		return game_thread;
	}
	//@ ensures game_thread != null;
	public void setGame_thread(ServerThread game_thread) {
		this.game_thread = game_thread;
	}
}
