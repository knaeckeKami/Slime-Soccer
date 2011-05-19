package slimesoccer;

/**
 *
 * @author edvo
 */
public class Vector2D {

    private double x, y; //X is ume, Y is aufe


    /**
     * Addiert den übergebenen Vektor.
     * @param v2 
     */
      public void add(Vector2D v2) {
        this.x += v2.x;
        this.y += v2.y;
    }


    public Vector2D(int x, int y) {
        this.x = x;
        this.y = y;
    }


  


    /**
     * Subtrahiert den übergeben Vektor.
     * @param v2 
     */
    public void subtract(Vector2D v2) {
        this.x -= v2.x;
        this.y -= v2.y;

    }
    
    /**
     * Liefert die Länge des Vektors.
     * @return 
     */
    public double length() {
        return Math.sqrt(x * x + y * y);
    }
    /**
     * Liefert die quadratische Länge des Vektors (aus Performancegründen da sqrt langsam ist)
     * @return 
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
        double l=this.length();
        this.x/=l;
        this.y/=l;
        //Länge muss 1 sein
        assert(Math.abs(this.length()-1)<1E-5);
        return this;
        
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

    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }
    /**
     * Erzeugt einen neuen Vektor mit x und y auf 0.
     */
    public Vector2D(){
        
    }
    /**
     * Erzeugt einen neuen Vector, der eine Kopie des übergebenen ist.
     * @param anotherVector 
     */
    public Vector2D(Vector2D anotherVector) {
        this.x = anotherVector.x;
        this.y = anotherVector.y;
    }
    
    @Override
    public String toString(){
        return String.format("X: %.3f, Y: %.3f, Länge: %.3f", x,y,length());
    }
            
}
