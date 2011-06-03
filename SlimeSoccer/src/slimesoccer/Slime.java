package slimesoccer;

import java.awt.Color;
import java.awt.Graphics;

/**
 * @TODO   richtige Dimension für Slime.SLIME_RADIUS finden
 * @TODO   verschiedene farben ^^
 * @author 3BHDV - Timo Hinterleitner
 * @author 3BHDV - Martin Kamleithner
 */
public class Slime extends MoveAble {
    
    /* Der Radius des Halbkreises, der den Slime darstellt 
     * 50 ist eine "Hausnummer", richtiger Wert muss noch gefunden werden.
     * @TODO 
     */
    public static final float SLIME_RADIUS=50; 
    //Die Farbe des Slimes.
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
        this(x,y,new Vector2D());
        
    }
    /**
     * Erzeugt einen Slime mit x,y und dem übergebenen Vektor.
     * @param x
     * @param y
     * @param vector 
     */
    public Slime(int x, int y, Vector2D vector){
        this(x,y,vector,Color.RED);
    }
    /**
     * Erzeugt einen Slime mit x,y und dem übergebenen Vektor und der übergebenen Farbe.
     * @param x
     * @param y
     * @param vector
     * @param color
     */
    public Slime(int x, int y, Vector2D vector, Color color){
        super(x,y);
        this.vector=vector;
        this.color = color == null ? Color.RED : color;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
    
    public void draw(Graphics g) {
        g.setColor(color);
        g.fillArc(x, y, Math.round(SLIME_RADIUS), Math.round(SLIME_RADIUS), 0, 180);
    }
}
