package slimesoccer;

/**
 *
 * @author TicTacMoe
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //diverse Tests
        Vector2D v = new Vector2D(4,0);
        v.einheitsVector();
        System.out.println(v);
        
        Ball b = new Ball(100, 100, 15);
        b.vector = new Vector2D();
        
    }

}
