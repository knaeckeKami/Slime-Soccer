package slimesoccer;

import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import javax.swing.JPanel;

/**
 *
 * @author 3BHDV - Timo Hinterleitner
 * @author 3BHDV - Martin Kamleithner
 */
public class Board extends JPanel {



    public static final int FLOOR = 480;

    private Ball ball;
    private Goal leftGoal;
    private Goal rightGoal;
    private Slime player;
    private Slime enemy;
    private Socket server;
    private DataInputStream din;
    private DataOutputStream dout;

    public Board() {
        this(600, 400);
    }
    /**
     * @deprecated kein server!
     * @param width
     * @param height
     */
    public Board(int width, int height) {
        super(true);    // enable double buffering
        this.setSize(width, height);

        this.addKeyListener(new ArrowKeyListener(this.dout));
    }

    public Board(int width, int height, Socket server) {
        super(true);    // enable double buffering
        this.setSize(width, height);
        this.server = server;
        this.player = new Slime();
        this.enemy = new Slime();
        this.ball = new Ball(Ball.STANDARD_RADIUS);
        try {
            this.din = new DataInputStream(server.getInputStream());
            this.dout = new DataOutputStream(server.getOutputStream());
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }



    }



    @Override
    public void paint(Graphics g) {
        super.paint(g);
        enemy.draw(g);
        player.draw(g);
        ball.draw(g);

    }
}
