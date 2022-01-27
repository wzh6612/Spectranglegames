package model;


import static model.Color.*;


public class SpectrangleTile {
    //affirm three sides
    public static final int BOTTOM = 0;
    public static final int LEFT = 1;
    public static final int RIGHT = 2;

    //shape
    private Color[] originPattern;

    //value
    private int value;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public SpectrangleTile(Color bottom, Color left, Color right, int value) {
        originPattern = new Color[3];
        originPattern[BOTTOM] = bottom;
        originPattern[LEFT] = left;
        originPattern[RIGHT] = right;
        this.value = value;
    }

    @Override
    public String toString() {
        return toString(0);
    }

    /**.
     * encode tile
     *
     * @param rotate
     * @return
     */
    public String toString(int rotate) {
        String result = "";
        switch (rotate) {
            case 0:
                result = "0";
                break;
            case 60:
                result = "1";
                break;
            case 120:
                result = "2";
                break;
            case 180:
                result = "3";
                break;
            case 240:
                result = "4";
                break;
            case 300:
                result = "5";
                break;
        }
        return result + originPattern[RIGHT].name().substring(0, 1) + 
        		originPattern[BOTTOM].name().substring(0, 1) + 
        		originPattern[LEFT].name().substring(0, 1) + value;
    }

    /**.
     * judge whether two tile is the same
     *
     * @param otherTile
     * @return
     */
    public boolean isEquivalent(SpectrangleTile otherTile) {
        if (this.originPattern[BOTTOM] == otherTile.originPattern[BOTTOM]
                && this.originPattern[LEFT] == otherTile.originPattern[LEFT]
                && this.originPattern[RIGHT] == otherTile.originPattern[RIGHT]) {
            return true;
        }
        return false;
    }

    /**.
     * judge if current tile is joker
     *
     * @return
     */
    public boolean isJoker() {
        if (originPattern[LEFT] == WHITE &&
                originPattern[RIGHT] == WHITE &&
                originPattern[BOTTOM] == WHITE) {
            return true;
        }
        return false;
    }
}
