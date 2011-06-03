package slimesoccer;

/**
 *
 * @author edvo
 */
public class Vector2D {

    private double x, y; //X is ume, Y is aufe
    public static final Vector2D GRAVITY = new Vector2D(0.0, 0.25);
    public static final double FRICTION_FACTOR_AIR = 0.995;
    public static final double FRICTION_FACTOR_FLOOR = 0.87;

    /**
     * Erzeugt einen neuen Vektor mit x und y auf 0.
     */
    public Vector2D() {
    }

    /**
     * Erzeugt einen neuen Vektor aus dem übergebenen (Kopie)
     * @param anotherVector Vektor der kopiert werden soll
     */
    public Vector2D(Vector2D anotherVector) {
        this.x = anotherVector.x;
        this.y = anotherVector.y;
    }

    /**
     * Erzeugt einen neuen Vektor mit den Werten x und y
     * @param x x-Wert des Vektors
     * @param y y-Wert des Vektors
     */
    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Addiert den übergebenen Vektor.
     * @param v2 Vektor der addiert werden soll
     */
    public Vector2D add(Vector2D v2) {
        this.x += v2.x;
        this.y += v2.y;
        return this;
    }

    /**
     * Subtrahiert den übergeben Vektor.
     * @param v2 Vektor der subtrahiert werden soll
     */
    public Vector2D subtract(Vector2D v2) {
        this.x -= v2.x;
        this.y -= v2.y;
        return this;
    }

    /**
     * Liefert die Länge des Vektors.
     * @return Länge des Vektors
     */
    public double length() {
        return Math.sqrt(x * x + y * y);
    }

    /**
     * Liefert die quadratische Länge des Vektors (aus Performancegründen da sqrt langsam ist)
     * @return Quadrierte Länge des Vektors
     */
    public double squarelength() {
        return (x * x + y * y);
    }

    /**
     * Setzt den Vector auf seinen Einheitsvektor. 
     * 
     * @return Eineheitsvektor (this) 
     */
    public Vector2D einheitsVector() {
        double l = this.length();
        this.x /= l;
        this.y /= l;
        //Länge muss 1 sein
        assert (Math.abs(this.length() - 1) < 1E-5);
        return this;

    }

    /**
     * Negiert den x-Wert des Vektors
     * x = -x
     */
    public void changeXDir() {
        this.x = -this.x;
    }

    /**
     * Kürzt den Vektor um den Faktor factor (für Reibung oda so)
     * @param factor 
     */
    public Vector2D multiply(double factor) {
        this.x *= factor;
        this.y *= factor;
        return this;
    }

    /**
     * Negiert den y-Wert des Vektors
     * y = -y
     */
    public void changeYDir() {
        this.y = -this.y;
    }

    /**
     * Liefert den x-Wert des Vektors
     * @return x-Wert
     */
    public double getX() {
        return x;
    }

    /**
     * Liefert den y-Wert des Vektors
     * @return y-Wert
     */
    public double getY() {
        return y;
    }

    /**
     * Setzt den x-Wert des Vektors
     * @param x neuer x-Wert
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * Setzt den y-Wert des Vektors
     * @param y neuer y-Wert
     */
    public void setY(double y) {
        this.y = y;
    }

    public double scalarProduct(Vector2D v2) {
        return this.x * v2.x + this.y + v2.y;
    }

    /**
     * Setzt x- und y-Wert des Vektors
     * @param x neuer x-Wert
     * @param y neuer y-Wert
     */
    public void setXY(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Stringdarstellung des Vektorobjektes
     * Format:
     *  X: x-Wert, Y: y-Wert, Länge: ...
     * @return String in der oben beschriebenen Darstellung
     */
    @Override
    public String toString() {
        return String.format("X: %.3f, Y: %.3f, Länge: %.3f", x, y, length());
    }
}
