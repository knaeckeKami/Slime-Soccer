package slimesoccer;

import java.awt.Color;
import java.awt.Graphics;

/**
 * Last modified: 14.06.2011
 * @author Timo Hinterleitner
 * @author Martin Kamleithner
 */
public class Ball extends Moveable {

    /**
     * Die Standard-Diagonale,  also der doppelte Radius, des Balls.
     */
    public static final float BALL_DIAGONALE = 25;
    /**
     * Der Standard-Radius des Balls.
     */
    public static final float BALL_RADIUS = BALL_DIAGONALE / 2;
    private float radius;
    private Color color = Color.RED;

    /**
     * Erzeugt einen neuen Ball, der auf (0/0) platziert ist und keine Bewegung
     * aufweist (Vector 0/0)
     * @param radius Radius des Balls
     */
    public Ball(float radius) {
        this(0, 0, radius);
    }

    /**
     * Erzeugt einen neuen Ball, der auf (0/0) platziert ist und keine Bewegung
     * aufweist (Vector 0/0)
     * Die Farbe entspricht color
     * @param radius Radius des Balls
     * @param color Farbe des Balls
     */
    public Ball(float radius, Color color) {
        this.radius = radius;
        this.color = color;
    }

    /**
     * Erzeugt einen neuen Ball, der auf (x/y) platziert ist und keine Bewegung
     * aufweist (Vector 0/0). Der Radius wird durch radius definiert
     * @param x x-Koordinate des Balls
     * @param y y-Koordinate des Balls
     * @param radius Radius des Balls
     */
    public Ball(int x, int y, float radius) {
        super(x, y);
        this.radius = radius > 0 ? radius : 1;
    }

    /**
     * Erzeugt einen neuen Ball, der auf (x/y) platziert ist und keine Bewegung
     * aufweist (Vector 0/0). Der Radius wird durch radius definiert.
     * Die Farbe wird auf color gesetzt.
     * @param x x-Koordinate des Balls
     * @param y y-Koordinate des Balls
     * @param radius Radius des Balls
     * @param color Farbe des Balls
     */
    public Ball(int x, int y, float radius, Color color) {
        super(x, y);
        this.radius = radius > 0 ? radius : 1;
        this.color = color == null ? Color.RED : color;
    }

    /**
     * Zeichnet den Ball
     * @param g Graphics Objekt mit dem gezeichnet werden soll
     */
    public void draw(Graphics g) {
        g.setColor(this.color);
        int roundedRadius = Math.round(this.radius);

        g.fillArc(Math.round(x), Math.round(y), roundedRadius, roundedRadius, 0, 360);
    }

    /**
     * Liefert X Koordinate des Mittelpunkts.
     * @return x Koordinate des Mittelpunkts
     */
    public float getMiddleX() {
        return x + Ball.BALL_RADIUS;
    }

    /**
     * Liefert Y Koordinate des Mittelpunkts.
     * @return y Koordinate des Mittelpuntks
     */
    public float getMiddleY() {
        return y + Ball.BALL_RADIUS;
    }
}
