package slimesoccer;

/**
 *
 * @author edvo
 */
public class Ball extends MoveAble {

    private float radius;

    public Ball(float radius) {
        this(0, 0, radius);
    }

    public Ball(int x, int y, float radius) {
        super(x, y);
        this.radius = radius > 0 ? radius : 1;
    }
}
