package view;

public class GameoverException extends Exception{
	public GameoverException() {
		super();
	}
	public String getMessage() {
		return "The game is over now.";
	}
}
