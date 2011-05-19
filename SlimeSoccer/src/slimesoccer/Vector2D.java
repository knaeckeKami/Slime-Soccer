package slimesoccer;

/**
 *
 * @author edvo
 */
public class Vector2D {

    private double x, y; //X is ume, Y is aufe

    public Vector2D() {
        this(0, 0);
    }

    public Vector2D(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void add(Vector2D v2) {
        this.x += v2.x;
        this.y += v2.y;
    }

    public double length() {
        return Math.sqrt(x * x + y * y);
    }

    public void einheitsVector() {
    }

    public void changeXDir() {
        x = -x;
    }

    public void changeYDir() {
        y = -y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }
}
