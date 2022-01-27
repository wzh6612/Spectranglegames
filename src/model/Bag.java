package model;

import static model.Color.*;

import java.util.Random;

/**.
 * The bag that holds 36 SpectrangleTiles
 */
public class Bag {
    //mmaintain an array to hold all SpectrangleTiles
    public SpectrangleTile[] allTiles;

    //record the index from 0 to 35 where tile has been taken
    private boolean[] taken;

    private static Random random = new Random();

    public Bag() {
        init();
    }

    /**.
     * init process, in which 36 SpectrangleTiles are initialized
     */
    public void init() {
        allTiles = new SpectrangleTile[36];
        //6 points each
        allTiles[0] = new SpectrangleTile(RED, RED, RED, 6);
        allTiles[1] = new SpectrangleTile(BLUE, BLUE, BLUE, 6);
        allTiles[2] = new SpectrangleTile(GREEN, GREEN, GREEN, 6);
        allTiles[3] = new SpectrangleTile(YELLOW, YELLOW, YELLOW, 6);
        allTiles[4] = new SpectrangleTile(PURPLE, PURPLE, PURPLE, 6);

        //5 points each
        allTiles[5] = new SpectrangleTile(RED, RED, YELLOW, 5);
        allTiles[6] = new SpectrangleTile(RED, RED, PURPLE, 5);
        allTiles[7] = new SpectrangleTile(BLUE, BLUE, RED, 5);
        allTiles[8] = new SpectrangleTile(BLUE, BLUE, PURPLE, 5);
        allTiles[9] = new SpectrangleTile(GREEN, GREEN, RED, 5);
        allTiles[10] = new SpectrangleTile(GREEN, GREEN, BLUE, 5);
        allTiles[11] = new SpectrangleTile(YELLOW, YELLOW, GREEN, 5);
        allTiles[12] = new SpectrangleTile(YELLOW, YELLOW, BLUE, 5);
        allTiles[13] = new SpectrangleTile(PURPLE, PURPLE, YELLOW, 5);
        allTiles[14] = new SpectrangleTile(PURPLE, PURPLE, GREEN, 5);

        //4 points each
        allTiles[15] = new SpectrangleTile(RED, RED, BLUE, 4);
        allTiles[16] = new SpectrangleTile(RED, RED, GREEN, 4);
        allTiles[17] = new SpectrangleTile(BLUE, BLUE, GREEN, 4);
        allTiles[18] = new SpectrangleTile(BLUE, BLUE, YELLOW, 4);
        allTiles[19] = new SpectrangleTile(GREEN, GREEN, YELLOW, 4);
        allTiles[20] = new SpectrangleTile(GREEN, GREEN, PURPLE, 4);
        allTiles[21] = new SpectrangleTile(YELLOW, YELLOW, RED, 4);
        allTiles[22] = new SpectrangleTile(YELLOW, YELLOW, PURPLE, 4);
        allTiles[23] = new SpectrangleTile(PURPLE, PURPLE, RED, 4);
        allTiles[24] = new SpectrangleTile(PURPLE, PURPLE, BLUE, 4);

        //3 points each
        allTiles[25] = new SpectrangleTile(YELLOW, BLUE, PURPLE, 3);
        allTiles[26] = new SpectrangleTile(RED, GREEN, YELLOW, 3);
        allTiles[27] = new SpectrangleTile(BLUE, GREEN, PURPLE, 3);
        allTiles[28] = new SpectrangleTile(GREEN, RED, BLUE, 3);

        //2 points each
        allTiles[29] = new SpectrangleTile(BLUE, RED, PURPLE, 2);
        allTiles[30] = new SpectrangleTile(YELLOW, PURPLE, RED, 2);
        allTiles[31] = new SpectrangleTile(YELLOW, PURPLE, GREEN, 2);

        //1 point
        allTiles[32] = new SpectrangleTile(GREEN, RED, PURPLE, 1);
        allTiles[33] = new SpectrangleTile(BLUE, YELLOW, GREEN, 1);
        allTiles[34] = new SpectrangleTile(RED, YELLOW, BLUE, 1);
        allTiles[35] = new SpectrangleTile(WHITE, WHITE, WHITE, 1);

        taken = new boolean[36];
    }

    /**.
     * random taken one tile from the bag
     *
     * @return
     */
    public SpectrangleTile randomTakeOne() {
        int i = random.nextInt(36);
        while (taken[i] == true) {
            i = random.nextInt(36);
        }
        taken[i] = true;
        return allTiles[i];
    }

    /**.
     * take the tile from bag which has the specified encoding
     *
     * @param tile
     */
    public void take(String tile) {
        for (int i = 0; i < allTiles.length; i++) {
            if (allTiles[i].toString(0).equals(tile) ||
                    allTiles[i].toString(60).equals(tile) ||
                    allTiles[i].toString(120).equals(tile) ||
                    allTiles[i].toString(180).equals(tile) ||
                    allTiles[i].toString(240).equals(tile) ||
                    allTiles[i].toString(300).equals(tile)) {

                taken[i] = true;
                return;
            }
        }
    }

    /**.
     * put back the tile into bag with the specified tile
     *
     * @param tile
     */
    public void putBack(SpectrangleTile tile) {
        for (int i = 0; i < allTiles.length; i++) {
            if (allTiles[i].isEquivalent(tile) || allTiles[i].equals(tile)) {
                taken[i] = false;
                return;
            }
        }
    }

    /**.
     * put back the tile into bag with the specified encoding
     *
     * @param tile
     */
    public void putBack(String tile) {
        for (int i = 0; i < allTiles.length; i++) {
            if (allTiles[i].toString(0).equals(tile) ||
                    allTiles[i].toString(60).equals(tile) ||
                    allTiles[i].toString(120).equals(tile) ||
                    allTiles[i].toString(180).equals(tile) ||
                    allTiles[i].toString(240).equals(tile) ||
                    allTiles[i].toString(300).equals(tile)) {

                taken[i] = false;
                return;
            }
        }
    }

    /**.
     * judge if the bag is empty
     *
     * @return
     */
    public boolean isEmpty() {
        for (int i = 0; i < 36; i++) {
            if (taken[i] == false) {
                return false;
            }
        }
        return true;
    }
}
