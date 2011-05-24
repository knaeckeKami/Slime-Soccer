package slimesoccer;

/**
 * @TODO   richtige Dimension für Slime.SLIME_RADIUS finden
 * @author 3BHDV - Timo Hinterleitner
 * @author 3BHDV - Martin Kamleithner
 */
public class Slime extends MoveAble {
    
    /* Der Radius des Halbkreises, der den Slime darstellt 
     * 50 ist eine "Hausnummer", richtiger Wert muss noch gefunden werden.
     * @TODO 
     */
    public static final float SLIME_RADIUS=50; 
    
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
        super(x,y);
        this.vector=vector;
    }
}
