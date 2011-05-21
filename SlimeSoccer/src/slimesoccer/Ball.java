package client;

/**
 *
 * @author edvo
 */
public class Ball extends MoveAble {

    private float radius;

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
}
