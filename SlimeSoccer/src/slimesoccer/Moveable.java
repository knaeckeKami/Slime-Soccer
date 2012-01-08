package slimesoccer;

/**
 * Last modified: 14.06.2011
 * @author Timo Hinterleitner
 * @author Martin Kamleithner
 */
public abstract class Moveable implements Drawable {

    protected float x, y;
    protected Vector2D vector;

    /**
     * Erzeugt ein neues Moveable Objekt auf (0/0) ohne Bewegung (Vector 0/0)
     */
    protected Moveable() {
        this(0, 0);
    }

    /**
     * Erzeugt ein neues Moveable Objekt auf (x/y) ohne Bewegung (Vector 0/0)
     * @param x x-Koordinate des Objekts
     * @param y y-Koordinate des Objekts
     */
    protected Moveable(float x, float y) {
        this(x, y, new Vector2D());
    }

    /**
     * Erzeugt ein neues Moveable Objekt auf (x/y) mit der von vector
     * beschriebenen Bewegungsrichtung und Bewegungsgeschwindigkeit
     * @param x x-Koordinate des Objekts
     * @param y y-Koordinate des Objekts
     * @param vector Bewegungsrichtung und Geschwindigkeit des Objekts in Form eines Vektors
     * 
     */
    protected Moveable(float x, float y, Vector2D vector) {
        this.x = x;
        this.y = y;
        this.vector = vector;
    }

    /**
     * Liefert den Vektor für die Bewegung des Objektes
     * @return Bewegungsvektor
     */
    public Vector2D getVector() {
        return vector;
    }

    /**
     * Setzt den Vektor für die Bewegung des Objektes, und somit
     * Bewegungsrichtung und -geschwindigkeit
     * @param vector Neuer Bewegungsvektor
     */
    public void setVector(Vector2D vector) {
        this.vector = vector;
    }

    /**
     * Liefert die x-Koordinate des Objektes
     * @return x-Koordinate
     */
    public double getXCoord() {
        return x;
    }

    /**
     * Setzt die x-Koordinate des Objektes
     * @param x x-Koordinate
     */
    public void setXCoord(float x) {
        this.x = x;
    }

    /**
     * Liefert die y-Koordinate des Objektes
     * @return y-Koordinate
     */
    public double getYCoord() {
        return y;
    }

    /**
     * Setzt die y-Koordinate des Objektes
     * @param y y-Koordinate
     */
    public void setYCoord(float y) {
        this.y = y;
    }

    /**
     * Addiert den Vektor zur aktuellen Position.
     * Verändert den Vektor nicht.
     */
    public void update() {
        this.x += vector.getX();
        this.y += vector.getY();
    }

 
}
