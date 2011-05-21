package client;

import javax.swing.JFrame;
import slimesoccer.Board;

/**
 *
 * @author edvo
 */
public class Client extends JFrame {

    public Client() {
        this.initComponents();
    }
    
    private void initComponents() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.getContentPane().add(new Board());
        this.setSize(500, 400);
    }
    
    public static void main(String... args) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new Client().setVisible(true);
            }
        });
    }
}
