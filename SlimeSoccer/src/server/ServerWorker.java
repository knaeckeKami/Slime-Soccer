package server;

import client.Client;
import java.awt.event.KeyEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.TimerTask;
import slimesoccer.*;

/**
 *
 * @author TicTacMoe
 */
public class ServerWorker extends TimerTask {

    private Ball ball;
    private Slime player1Slime;
    private Slime player2Slime;
    private Goal player1Goal;
    private Goal player2Goal;
    private Socket player1;
    private Socket player2;
    private DataInputStream dis1;
    private DataInputStream dis2;
    private DataOutputStream dos1;
    private DataOutputStream dos2;
    private String player1Name = "Player 1";
    private String player2Name = "Player 2";
    private boolean[][] keys = new boolean[2][3];
    private int[] goals = new int[2];

    public ServerWorker(Socket p1, Socket p2) throws IOException {
        this.ball = new Ball(Client.BOARD_WIDTH / 2, Client.BOARD_HEIGHT / 2, Ball.STANDARD_RADIUS);
        this.player1Slime = new Slime(Board.WIDTH / 4, Board.FLOOR);
        this.player2Slime = new Slime(Board.WIDTH / 4 * 3, Board.FLOOR);
        this.player1Goal = new Goal(0);
        this.player2Goal = new Goal(1);

        this.player1 = p1;
        this.player2 = p2;
        this.dis1 = new DataInputStream(p1.getInputStream());
        this.dis2 = new DataInputStream(p2.getInputStream());
        this.dos1 = new DataOutputStream(p1.getOutputStream());
        this.dos2 = new DataOutputStream(p2.getOutputStream());


        // Sending names of competitors
        this.dos1.writeByte(Constants.TYPE_NAME);
        this.dos1.writeBytes(this.player2Name);
        this.dos2.writeByte(Constants.TYPE_NAME);
        this.dos2.writeBytes(this.player1Name);

    }

    @Override
    public void run() {
        try {
            // Enough data received to handle key event?
            if (this.dis1.available() > 2) {
                byte eventtype;
                switch ((eventtype = this.dis1.readByte())) {
                    case Constants.TYPE_KEY:
                        byte key = this.dis1.readByte();
                        boolean pressed = this.dis1.readBoolean();
                        this.setKeystatus(0, key, pressed);
                        break;
                    default:
                        System.err.println("Wrong keycode from : " + eventtype);
                }
            }
            if (this.dis2.available() > 2) {
                byte eventtype;
                switch ((eventtype = this.dis2.readByte())) {
                    case Constants.TYPE_KEY:
                        byte key = this.dis2.readByte();
                        boolean pressed = this.dis2.readBoolean();
                        this.setKeystatus(1, key, pressed);
                        break;
                    default:
                        System.err.println("Wrong keycode: " + eventtype);
                }
            }

            // handle gravity
            if (this.ball.getYCoord() < Board.FLOOR) {
                this.ball.getVector().add(Vector2D.GRAVITY);
            }
            if (this.player1Slime.getYCoord() < Board.FLOOR) {
                this.player1Slime.getVector().add(Vector2D.GRAVITY);
            }
            if (this.player2Slime.getYCoord() < Board.FLOOR) {
                this.player2Slime.getVector().add(Vector2D.GRAVITY);
            }

            this.checkCollisions();

            this.ball.update();
            this.player2Slime.update();
            this.player2Slime.update();
        } catch (IOException ioe) {
            System.err.println(ioe);
        }
    }

    private void checkCollisions() {
        float ball_slime_diff = Ball.STANDARD_RADIUS + Slime.SLIME_RADIUS;
        Vector2D v1 = new Vector2D(this.ball.getXCoord() - this.player1Slime.getXCoord(),
                this.ball.getYCoord() - this.player1Slime.getYCoord());

        if (v1.squarelength() < ball_slime_diff * ball_slime_diff) {
        }
    }

    private boolean getKeystatus(int player, int keycode) {
        return this.keys[player][keycode - 25];
    }

    private void pressKey(int player, int keycode) {
        this.keys[player][keycode - 0x25] = true;       // 0x25 = offset zu VK_LEFT, VK_UP, VK_RIGHT
    }

    private void releaseKey(int player, int keycode) {
        this.keys[player][keycode - 0x25] = false;      // 0x25 = offset zu VK_LEFT, VK_UP, VK_RIGHT
    }

    private void setKeystatus(int player, int keycode, boolean pressed) {
        System.out.println("Debug: Player(" + player + ") set key status " + keycode + " to " + pressed);
        this.keys[player][keycode - 0x25] = pressed;    // 0x25 = offset zu VK_LEFT, VK_UP, VK_RIGHT
    }
}
