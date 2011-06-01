package server;

import client.Client;
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
    private Player p1;
    private Player p2;
    private boolean[][] keys = new boolean[2][3];
    private int[] goals = new int[2];

    public ServerWorker(Socket p1Socket, Socket p2Socket) throws IOException {
        this.ball = new Ball(Client.BOARD_WIDTH / 2, Client.BOARD_HEIGHT / 2, Ball.STANDARD_RADIUS);
        
        this.p1 = new Player(1, p1Socket);
        this.p2 = new Player(2, p2Socket);
        
        this.p1.enemy = this.p2;
        this.p2.enemy = this.p1;


        // Sending names of competitors
        this.p1.dos.writeByte(Constants.TYPE_NAME);
        this.p1.dos.writeBytes(this.p1.name);
        this.p2.dos.writeByte(Constants.TYPE_NAME);
        this.p2.dos.writeBytes(this.p2.name);

        // Sending initial coordinates
        this.writeCoords(this.p1);
        this.writeCoords(this.p2);
    }

    @Override
    public void run() {
        try {
            // Enough data received to handle key event?
            if (this.p1.dis.available() > 2) {
                byte eventtype;
                switch ((eventtype = this.p1.dis.readByte())) {
                    case Constants.TYPE_KEY:
                        byte key = this.p1.dis.readByte();
                        boolean pressed = this.p1.dis.readBoolean();
                        this.setKeystatus(0, key, pressed);
                        break;
                    default:
                        System.err.println("Wrong keycode from : " + eventtype);
                }
            }
            if (this.p2.dis.available() > 2) {
                byte eventtype;
                switch ((eventtype = this.p2.dis.readByte())) {
                    case Constants.TYPE_KEY:
                        byte key = this.p2.dis.readByte();
                        boolean pressed = this.p2.dis.readBoolean();
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
            if (this.p1.slime.getYCoord() < Board.FLOOR) {
                this.p1.slime.getVector().add(Vector2D.GRAVITY);
            }
            if (this.p2.slime.getYCoord() < Board.FLOOR) {
                this.p2.slime.getVector().add(Vector2D.GRAVITY);
            }

            this.checkCollisions();

            this.ball.update();
            this.p2.slime.update();
            this.p2.slime.update();
        } catch (IOException ioe) {
            System.err.println(ioe);
        }
    }

    private void checkCollisions() {
        float ball_slime_diff = Ball.STANDARD_RADIUS + Slime.SLIME_RADIUS;
        Vector2D v1 = new Vector2D(this.ball.getXCoord() - this.p1.slime.getXCoord(),
                this.ball.getYCoord() - this.p1.slime.getYCoord());

        if (v1.squarelength() < ball_slime_diff * ball_slime_diff) {
            /*
             * v1 = Vector vom Mittelpunkt des Slimes zum Mittelpunkt des Balls
             * r  = Vector vom Mittelpunkt des Slimes zum Aufprallpunkt auf der Oberfläche des Slimes
             * L  = Länge des Lot-Vectors
             * austritt = Austrittsvector des Balls nach dem Aufprall
             *
             * r = einheitsvector von r * SLIME_RADIUS
             * r = ( (->r) / |(->r)| )  * SLIME_RADIUS
             *
             * L = (Scalarprodukt von Einfallsvector und Vector des Slimes) / (Länge des Einfallsvectors)
             * L = ( (->this.ball.getVector()) * (->r) )                    / |(->this.ball.getVector())|
             *
             * lot = (Länge * Einheitsvector r)         // weil lot und r parallel sind!
             * lot = (L * ( (->r) / |(->r)| )
             *
             * austritt = (2 * lot + Einfallsvector)
             * austritt = 2 * lot + this.ball.getVector()
             */
            Vector2D r = v1.einheitsVector().multiply(Slime.SLIME_RADIUS);
            double L = Math.abs(this.ball.getVector().scalarProduct(r) / this.ball.getVector().length());
            Vector2D lot = r.einheitsVector().multiply(L);
            Vector2D austritt = lot.multiply(2).add(this.ball.getVector());
            this.ball.setVector(austritt);
        }
    }

    private void writeCoords(Player p) throws IOException {
        p.dos.writeByte(Constants.TYPE_COORDS);

        // x,y vom Ball
        p.dos.writeInt(this.ball.getXCoord());
        p.dos.writeInt(this.ball.getYCoord());

        // x,y vom eigenen Spieler
        p.dos.writeInt(p.slime.getXCoord());
        p.dos.writeInt(p.slime.getYCoord());

        // x,y vom anderen Spieler
        p.dos.writeInt(p.enemy.slime.getXCoord());
        p.dos.writeInt(p.enemy.slime.getYCoord());
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
