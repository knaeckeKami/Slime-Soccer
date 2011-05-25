package server;

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
        this.ball = new Ball(Client.BOARD_WIDTH/2, Client.BOARD_HEIGHT/2, Ball.STANDARD_RADIUS);

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
            if (this.dis1.available() > 2) {
                byte eventtype;
                switch ((eventtype=this.dis1.readByte())) {
                    case Constants.TYPE_KEY:
                        byte key = this.dis1.readByte();
                        boolean pressed = this.dis1.readBoolean();
                        this.setKeystatus(0,key, pressed);
                        break;
                    default:
                        System.err.println("Wrong keycode from : " + eventtype);
                }
            }
            if (this.dis2.available() > 2) {
                byte eventtype;
                switch ((eventtype=this.dis2.readByte())) {
                    case Constants.TYPE_KEY:
                        byte key = this.dis2.readByte();
                        boolean pressed = this.dis2.readBoolean();
                        this.setKeystatus(1,key, pressed);
                        break;
                    default:
                        System.err.println("Wrong keycode: " + eventtype);
                }
            }


        } catch (IOException ioe) {
            System.err.println(ioe);
        }
    }

    private boolean getKeystatus(int player, int keycode) {
        return this.keys[player][keycode-25];
    }

    private void pressKey(int player, int keycode) {
        this.keys[player][keycode-25] = true;
    }

    private void releaseKey(int player, int keycode) {
        this.keys[player][keycode-25] = false;
    }

    private void setKeystatus(int player, int keycode, boolean pressed) {
        this.keys[player][keycode-25] = pressed;
    }
}
