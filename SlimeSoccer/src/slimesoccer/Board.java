package slimesoccer;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.Socket;
import javax.swing.JPanel;

/**
 *
 * @author 3BHDV - Timo Hinterleitner
 * @author 3BHDV - Martin Kamleithner
 */
public class Board extends JPanel {

    private Ball ball;
    private Goal leftGoal;
    private Goal rightGoal;
    private Slime player;
    private Slime enemy;

    public Board() {
        this(600, 400);
    }

    public Board(int width, int height) {
        super(true);    // enable double buffering
        this.setSize(width, height);

        this.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
            }
        });
    }
}
