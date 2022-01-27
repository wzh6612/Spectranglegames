//package test;
//
//import org.junit.Before;
//import org.junit.Test;
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertTrue;
//import model.Point;
//import model.*;
//public class ModelTest {
//	private Bag bag;
//	SpectrangleTile joker;
//	Point point;
//	public GameBoard board;
//	@Before
//	public void setUp() {
//		bag = new Bag();
//		bag.init();
//		joker = bag.allTiles[35];
//		point = new Point(1, 2);
//		board = new GameBoard();
//	}
//	@Test
//	public void testInit() {
//		assertEquals(36, bag.allTiles.length);
//	}
//	@Test
//	public void testJoker() {
//		assertTrue(joker.isJoker());
//	}
//	@Test
//	public void testEmpty() {
//		Bag deepCopy = new Bag();
//		for (int i = 0; i < 36; i++) {
//			deepCopy.randomTakeOne();
//		}
//		assertTrue(deepCopy.isEmpty());
//	}
//	@Test
//	public void testPoint() {
//		assertEquals(1, point.getX());
//		assertEquals(2, point.getY());
//		point.setX(3);
//		point.setY(4);
//		assertEquals(3, point.getX());
//		assertEquals(4, point.getY());
//	}
//	@Test
//	public void testToindex() {
//		Point test = new Point(2,2);
//		assertEquals(board.point2index(test),2);
//	}
//	@Test
//	public void testPlaced() {
//		board.putTile("0RYP2", 3, "Garen");
//		assertTrue(board.isPut(3));
//	}
//}
