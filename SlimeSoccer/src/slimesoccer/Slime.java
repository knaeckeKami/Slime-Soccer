package slimesoccer;

import java.awt.Color;
import java.awt.Graphics;

/**
 * Last modified: 14.06.2011
 * @author Timo Hinterleitner
 * @author Martin Kamleithner
 */
public class Slime extends MoveAble {

    /**
     * Die Diagonale, also der doppelte Radius, des Slimes.
     */
    public static final float SLIME_DIAGONALE = 50;
    /**
     * Der Radius des Slimes.
     */
    public static final float SLIME_RADIUS = SLIME_DIAGONALE / 2;
    /**
     * Die Farbe des Slimes.
     */
    private Color color;

    /**
     * Erzeugt einen Slime mit 0,0 und einen Nullvektor.
     */
    public Slime() {
        this(0, 0);
    }

    /**
     * Erzeugt einen Slime mit x,y und einen Nullvektor
     * @param x
     * @param y 
     */
    public Slime(int x, int y) {
        this(x, y, new Vector2D());

    }

    /**
     * Erzeugt einen Slime mit x,y und dem übergebenen Vektor.
     * @param x
     * @param y
     * @param vector 
     */
    public Slime(int x, int y, Vector2D vector) {
        this(x, y, vector, Color.RED);
    }

    /**
     * Erzeugt einen Slime mit x,y und dem übergebenen Vektor und der übergebenen Farbe.
     * @param x
     * @param y
     * @param vector
     * @param color
     */
    public Slime(int x, int y, Vector2D vector, Color color) {
        super(x, y);
        this.vector = vector;
        this.color = color == null ? Color.RED : color;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * Zeichnet den Slime mit dem Graphics Objekt g, in der Farbe color
     * @param g Graphics Objekt mit dem gezeichnet wird
     */
    public void draw(Graphics g) {
        g.setColor(color);
        g.fillArc(Math.round(x), Math.round(y), Math.round(SLIME_DIAGONALE), Math.round(SLIME_DIAGONALE), 0, 180);
    }

    /**
     * Berechnet die neue Position des Slimes (nach der Bewegung)
     * Verhindert auch ein verschwinden am Bildschirmrand
     */
    @Override
    public void update() {
        super.update();
        if (this.x < 0) {
            this.x = 0;
        } else if (this.x > Constants.BOARD_WIDTH - Slime.SLIME_DIAGONALE) {
            this.x = Constants.BOARD_WIDTH - Slime.SLIME_DIAGONALE;
        }
    }

    /**
     * Liefert X Koordinate des Mittelpunkts
     * 
     */
    public float getMiddleX() {
        return x + Slime.SLIME_RADIUS;
    }

    /**
     * Liefert Y Koordinate des Mittelpunkts
     * 
     */
    public float getMiddleY() {
        return y + Slime.SLIME_RADIUS;
    }
}
