package model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**.
 * board that players put their tiles
 */
public class GameBoard {
	// affirm three sides
	public static final int BOTTOM = 0;
	public static final int LEFT = 1;
	public static final int RIGHT = 2;

	/// the game board
	private String[] board;

	// record specified index which has bonus
	private int[] bonus;

	// record whether the place has been put
	private boolean[] put;

	
	Map<String, List<Integer>> userMap;

	// judge if it's the first tile to be put in the board
	private boolean first;

	public GameBoard() {
		init();
	}

	/**.
	 * init process,initialize the board and all bonus place
	 */
	public void init() {
		board = new String[36];
		bonus = new int[] {1, 1, 3, 1, 1, 1, 1, 1, 1, 1,
			2, 4, 1, 4, 2, 1, 1, 1, 1, 1, 4, 1, 1, 1, 1, 1, 3, 1, 1, 1, 2, 1, 1, 1, 3, 1};
		put = new boolean[36];
		userMap = new HashMap<>();
		first = true;
	}

	/**
	 * put the tile in the specified place,you should follow rules: 1. if it's the
	 * first tile,it should has the same shape with the index,and should not put in
	 * the place with bonus 2. if it isn't the first tile,it should be put in the
	 * place that has not been put as well as the index has the same shape,and has
	 * at least one adjacent edge which has the same color
	 *
	 * @param tile
	 * @param index
	 */
	public boolean putTile(String tile, int index, String userName) {
		if (first) {
			if (bonus[index] != 1) {
				System.out.println("illegal position, first cannot put");
				return false;
			}
			if (!isSameRotate(tile, index)) {
				System.out.println("first illegal rotate");
				return false;
			}
			board[index] = tile;
			put[index] = true;
			List<Integer> list = new LinkedList<>();
			list.add(index);
			userMap.put(userName, list);
			first = false;
		} else {
			if (isPut(index)) {
				System.out.println("illegal position, taken already");
				return false;
			}
			if (!isSameRotate(tile, index)) {
				System.out.println("illegal rotate");
				return false;
			}
			int[] count = new int[1];
			count(tile, index, count);
			if (count[0] == 0) {
				System.out.println("no match");
				return false;
			}
			board[index] = tile;
			put[index] = true;
			List<Integer> list = (userMap.get(userName) == null) ? 
					new LinkedList<>() : userMap.get(userName);
			list.add(index);
			userMap.put(userName, list);
		}
		return true;
	}

	/**.
	 * judge whether the tile and index have the same shape note: if index's y is
	 * even, it will be a upward triangle if the tile encoding start will odd, it
	 * will be a upward triangle
	 *
	 * @param tile
	 * @param index
	 * @return
	 */
	public boolean isSameRotate(String tile, int index) {
		Point point = index2point(index);
		if (point != null && (point.getY() % 2 != Integer.valueOf(tile.substring(0, 1)) % 2)) {
			return true;
		}
		return false;
	}

	/**.
	 * count how many adjacent edges with same color the tile get, if put in the
	 * specified index
	 *
	 * @param tile
	 * @param index
	 * @param count
	 */
	public void count(String tile, int index, int[] count) {
		Point point = index2point(index);
		if (point != null) {
			if (point.getY() % 2 == 1) {
				// if tile has upward triangle shape
				countUpLeft(tile, index, count, point);
				countUpRight(tile, index, count, point);
				countUpDown(tile, index, count, point);
			} else {
				// if tile has down triangle shape
				countDownLeft(tile, index, count, point);
				countDownRight(tile, index, count, point);
				countDownUp(tile, index, count, point);
			}
		}
	}

	/**.
	 * count how many left adjacent edges with same color a tile get, which has
	 * upward triangle shape
	 *
	 * @param tile
	 * @param index
	 * @param count
	 * @param point
	 */
	public void countUpLeft(String tile, int index, int[] count, Point point) {
		if (point.getY() > 1) {
			int left = index - 1;
			if (isPut(left)) {
				String other = board[left];
				if (isJoker(tile) || isJoker(other) || 
						getUpLeft(tile).equals(getDownRight(other))) {
					count[0]++;
				}
			}
		}
	}

	/**.
	 * count how many right adjacent edges with same color a tile get, which has
	 * upward triangle shape
	 * 
	 * @param tile
	 * @param index
	 * @param count
	 * @param point
	 */
	public void countUpRight(String tile, int index, int[] count, Point point) {
		if (point.getY() < point.getX() * 2 - 1) {
			int right = index + 1;
			if (isPut(right)) {
				String other = board[right];
				if (isJoker(tile) || isJoker(other) || 
						getUpRight(tile).equals(getDownLeft(other))) {
					count[0]++;
				}
			}
		}
	}

	/**.
	 * count how many down adjacent edges with same color a tile get, which has
	 * upward triangle shape
	 *
	 * @param tile
	 * @param index
	 * @param count
	 * @param point
	 */
	public void countUpDown(String tile, int index, int[] count, Point point) {
		if (point.getX() < 6) {
			int down = point2index(new Point(point.getX() + 1, point.getY() + 1));
			if (isPut(down)) {
				String other = board[down];
				if (isJoker(tile) || isJoker(other) || getUpDown(tile).equals(getDownUp(other))) {
					count[0]++;
				}
			}
		}
	}

	/**.
	 * count how many left adjacent edges with same color a tile get, which has down
	 * triangle shape
	 *
	 * @param tile
	 * @param index
	 * @param count
	 * @param point
	 */
	public void countDownLeft(String tile, int index, int[] count, Point point) {
		int left = index - 1;
		if (isPut(left)) {
			String other = board[left];
			if (isJoker(tile) || isJoker(other) || getDownLeft(tile).equals(getUpRight(other))) {
				count[0]++;
			}
		}
	}

	/**.
	 * count how many right adjacent edges with same color a tile get, which has
	 * down triangle shape
	 *
	 * @param tile
	 * @param index
	 * @param count
	 * @param point
	 */
	public void countDownRight(String tile, int index, int[] count, Point point) {
		int right = index + 1;
		if (isPut(right)) {
			String other = board[right];
			if (isJoker(tile) || isJoker(other) || getDownRight(tile).equals(getUpLeft(other))) {
				count[0]++;
			}
		}
	}

	/**.
	 * count how many up adjacent edges with same color a tile get, which has down
	 * triangle shape
	 *
	 * @param tile
	 * @param index
	 * @param count
	 * @param point
	 */
	public void countDownUp(String tile, int index, int[] count, Point point) {
		int up = point2index(new Point(point.getX() - 1, point.getY() - 1));
		if (isPut(up)) {
			String other = board[up];
			if (isJoker(tile) || isJoker(other) || getDownUp(tile).equals(getUpDown(other))) {
				count[0]++;
			}
		}
	}

	/**.
	 * juudge whether a tile is joker, which has three White colors
	 *
	 * @param tile
	 * @return
	 */
	public boolean isJoker(String tile) {
		if (tile.substring(1, 4).equals("WWW")) {
			return true;
		}
		return false;
	}

	/**.
	 * get the left color from tile, which has upward triangle shape
	 *
	 * @param tile
	 * @return
	 */
	public String getUpLeft(String tile) {
		int rotate = Integer.parseInt(tile.substring(0, 1));
		if (rotate == 0) {
			return tile.substring(3, 4);
		} else if (rotate == 2) {
			return tile.substring(2, 3);
		} else {
			return tile.substring(1, 2);
		}
	}

	/**.
	 * get the right color from tile, which has upward triangle shape
	 *
	 * @param tile
	 * @return
	 */
	public String getUpRight(String tile) {
		int rotate = Integer.parseInt(tile.substring(0, 1));
		if (rotate == 0) {
			return tile.substring(1, 2);
		} else if (rotate == 2) {
			return tile.substring(3, 4);
		} else {
			return tile.substring(2, 3);
		}
	}

	/**.
	 * get the down color from tile, which has upward triangle shape
	 *
	 * @param tile
	 * @return
	 */
	public String getUpDown(String tile) {
		int rotate = Integer.parseInt(tile.substring(0, 1));
		if (rotate == 0) {
			return tile.substring(2, 3);
		} else if (rotate == 2) {
			return tile.substring(1, 2);
		} else {
			return tile.substring(3, 4);
		}
	}

	/**.
	 * get the left color from tile, which has down triangle shape
	 *
	 * @param tile
	 * @return
	 */
	public String getDownLeft(String tile) {
		int rotate = Integer.parseInt(tile.substring(0, 1));
		if (rotate == 1) {
			return tile.substring(2, 3);
		} else if (rotate == 3) {
			return tile.substring(1, 2);
		} else {
			return tile.substring(3, 4);
		}
	}

	/**.
	 * get the right color from tile, which has down triangle shape
	 *
	 * @param tile
	 * @return
	 */
	public String getDownRight(String tile) {
		int rotate = Integer.parseInt(tile.substring(0, 1));
		if (rotate == 1) {
			return tile.substring(1, 2);
		} else if (rotate == 3) {
			return tile.substring(3, 4);
		} else {
			return tile.substring(2, 3);
		}
	}

	/**.
	 * get the up color from tile, which has down triangle shape
	 *
	 * @param tile
	 * @return
	 */
	public String getDownUp(String tile) {
		int rotate = Integer.parseInt(tile.substring(0, 1));
		if (rotate == 1) {
			return tile.substring(3, 4);
		} else if (rotate == 3) {
			return tile.substring(2, 3);
		} else {
			return tile.substring(1, 2);
		}
	}

	/**.
	 * switch index to point that record location in board
	 *
	 * @param index
	 * @return
	 */
	public Point index2point(int index) {
		for (int i = 0; i < 6; i++) {
			if (i * i <= index && (i + 1) * (i + 1) > index) {
				Point point = new Point(i + 1, index - i * i + 1);
				return point;
			}
		}
		return null;
	}

	/**.
	 * switch point to index in an array
	 *
	 * @param point
	 * @return
	 */
	public int point2index(Point point) {
		return (point.getX() - 1) * (point.getX() - 1) + point.getY() - 1;
	}

	/**.
	 * calculate the score in the specified place following rules: count = adjacent
	 * edges that have same color value = the tile's value bonus = the index int
	 * board with bonus score = count * value * bonus
	 *
	 * @param index
	 * @return
	 */
	public int score(int index) {
		if (!isPut(index)) {
			return 0;
		}
		int[]count = new int[1];
		String tile = board[index];
		count(tile, index, count);
		int score = count[0] * Integer.parseInt(tile.substring(4, 5)) * bonus[index];
		System.out.println("index: " + index + "\tcount: " + count + "\tscore: " + score);
		return score;
	}

	/**.
	 * score all user's score
	 *
	 * @return
	 */
	public Map<String, Integer> scoreAllUsers() {
		Map<String, Integer> result = new HashMap<>();
		for (String name : userMap.keySet()) {
			int sum = 0;
			List<Integer> list = userMap.get(name);
			for (Integer index : list) {
				sum += score(index);
			}
			result.put(name, sum);
		}
		return result;
	}

	/**.
	 * judge whether index has been put
	 *
	 * @param index
	 * @return
	 */
	public boolean isPut(int index) {
		if (put[index] == true) {
			return true;
		}
		return false;
	}

	/**.
	 * judge whether the game is over,which has all 36 put tiles
	 *
	 * @return
	 */
	public boolean isGameOver() {
		for (int i = 0; i < 36; i++) {
			if (put[i] == false) {
				return false;
			}
		}
		return true;
	}

	/**.
	 * return value list in board
	 *
	 * @return
	 */
	public List<Integer> getValuesList() {
		List<Integer> result = new LinkedList<>();
		for (int i = 0; i < 36; i++) {
			if (isPut(i)) {
				result.add(Integer.valueOf(board[i].substring(4, 5)));
			} else {
				result.add(null);
			}
		}
		return result;
	}

	/**.
	 * return vertical list in board
	 *
	 * @return
	 */
	public List<Character> getVerticalList() {
		List<Character> result = new LinkedList<>();
		for (int i = 0; i < 36; i++) {
			if (isPut(i)) {
				String tile = board[i];
				if ((Integer.parseInt(tile.substring(0, 1))) % 2 == 0) {
					result.add(getUpDown(tile).toCharArray()[0]);
				} else {
					result.add(getDownUp(tile).toCharArray()[0]);
				}
			} else {
				result.add(null);
			}
		}
		return result;
	}

	/**.
	 * return left list in board
	 *
	 * @return
	 */
	public List<Character> getLeftList() {
		List<Character> result = new LinkedList<>();
		for (int i = 0; i < 36; i++) {
			if (isPut(i)) {
				String tile = board[i];
				if ((Integer.parseInt(tile.substring(0, 1))) % 2 == 0) {
					result.add(getUpLeft(tile).toCharArray()[0]);
				} else {
					result.add(getDownLeft(tile).toCharArray()[0]);
				}
			} else {
				result.add(null);
			}
		}
		return result;
	}

	/**.
	 * return right list in board
	 *
	 * @return
	 */
	public List<Character> getRightList() {
		List<Character> result = new LinkedList<>();
		for (int i = 0; i < 36; i++) {
			if (isPut(i)) {
				String tile = board[i];
				if ((Integer.parseInt(tile.substring(0, 1))) % 2 == 0) {
					result.add(getUpRight(tile).toCharArray()[0]);
				} else {
					result.add(getDownRight(tile).toCharArray()[0]);
				}
			} else {
				result.add(null);
			}
		}
		return result;
	}

	/**.
	 * display the board
	 */
	public void show() {
		List<Integer> values = getValuesList();
		List<Character> vertical = getVerticalList();
		List<Character> left = getLeftList();
		List<Character> right = getRightList();
		System.out.println(SpectrangleBoardPrinter.getBoardString(values, vertical, left, right));
	}

	/**.
	 * recommmand the possible index list where tile could be put in current board
	 *
	 * @param tile
	 * @return
	 */
	public String recommand(String tile) {
		int num = 0;
		StringBuffer sb = new StringBuffer();
		for (int j = 0; j < 6; j++) {
			String temp = j + tile.substring(1, 5);
			for (int i = 0; i < 36; i++) {
				if (first) {
					if (bonus[i] != 1) {
						continue;
					}
					if (!isSameRotate(temp, i)) {
						continue;
					}
					sb.append(i).append(" ").append(temp).append(", ");
					if (num++ > 5) {
						return sb.toString().trim();
					}

				} else {
					if (isPut(i)) {
						continue;
					}
					if (!isSameRotate(temp, i)) {
						continue;
					}
					int[] count = new int[1];
					count(temp, i, count);
					if (count[0] == 0) {
						continue;
					}
					sb.append(i).append(" ").append(temp).append(", ");
					if (num++ > 5) {
						return sb.toString().trim();
					}
				}
			}
		}
		if (num == 0) {
			return "";
		}
		return sb.toString().trim();
	}
}
