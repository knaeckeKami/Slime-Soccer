/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package slimesoccer;

/**
 *
 * @author edvo
 */
public abstract class MoveAble {

    protected int x, y;
    protected Vector2D vector;

    public MoveAble() {
        this(0, 0);
    }

    public MoveAble(int x, int y) {
        this(x, y, new Vector2D());
    }

    public MoveAble(int x, int y, Vector2D vector) {
        this.x = x;
        this.y = y;
        this.vector = vector;
    }

    public Vector2D getVector() {
        return vector;
    }

    public void setVector(Vector2D vector) {
        this.vector = vector;
    }

    public int getXCoord() {
        return x;
    }

    public void setXCoord(int x) {
        this.x = x;
    }

    public int getYCoord() {
        return y;
    }

    public void setYCoord(int y) {
        this.y = y;
    }
}
