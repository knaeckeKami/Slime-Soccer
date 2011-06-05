package slimesoccer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.ImageObserver;

/**
 *
 * @author edvo
 */
public class Ball extends MoveAble {

    /**
     * Die Standard-Diagonale, also der doppelte Radius, des Balls.
     */
    public static final float BALL_DIAGONALE = 25;
    /**
     * Der Standard-Radius des Balls.
     */
    public static final float BALL_RADIUS = BALL_DIAGONALE / 2;
    private float radius;
    private Color color;

    /**
     * Erzeugt einen neuen Ball, der auf (0/0) platziert ist und keine Bewegung
     * aufweist (Vector 0/0)
     * @param radius Radius des Balls
     */
    public Ball(float radius) {
        this(0, 0, radius);
    }

    /**
     * Erzeugt einen neuen Ball, der auf (x/y) platziert ist und keine Bewegung
     * aufweist (Vector 0/0)
     * @param x x-Koordinate des Balls
     * @param y y-Koordinate des Balls
     * @param radius Radius des Balls
     */
    public Ball(int x, int y, float radius) {
        super(x, y);
        this.radius = radius > 0 ? radius : 1;
    }

    public Ball(int x, int y, float radius, Color color) {
        super(x, y);
        this.radius = radius > 0 ? radius : 1;
        this.color = color == null ? Color.RED : color;
    }

    public void draw(Graphics g) {
        g.setColor(color);
        int roundedRadius = Math.round(radius);

        g.fillArc(Math.round(x), Math.round(y), roundedRadius, roundedRadius, 0, 360);
    }

    public void draw(Graphics g, ImageObserver io) {
        draw(g);
    }
    /**
     * Liefert X Koordinate des Mittelpunkts.
     * @return 
     */
    public float getMiddleX() {
        return x + Ball.BALL_RADIUS;
    }
    /**
     * Liefert Y Koordinate des Mittelpunkts.
     * @return 
     */
    public float getMiddleY() {
        return y + Ball.BALL_RADIUS;
    }
}
