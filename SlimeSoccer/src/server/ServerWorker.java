package server;

import client.Client;
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
    private int[] goals = new int[2];

    /**
     *
     * @param p1Socket Socket zum Spieler 1
     * @param p2Socket Socket zum Spieler 2
     * @throws IOException
     */
    public ServerWorker(Socket p1Socket, Socket p2Socket) throws IOException {
        this.ball = new Ball(Client.BOARD_WIDTH / 2, Client.BOARD_HEIGHT / 2, Ball.STANDARD_RADIUS);

        this.p1 = new Player(1, p1Socket);
        this.p2 = new Player(2, p2Socket);

        this.p1.enemy = this.p2;
        this.p2.enemy = this.p1;


        // Sending names of competitors
        this.p1.dos.writeByte(Constants.TYPE_NAME);
        this.p1.dos.writeBytes(this.p1.name + "\n");
        this.p2.dos.writeByte(Constants.TYPE_NAME);
        this.p2.dos.writeBytes(this.p2.name + "\n");

        // Sending initial coordinates
        this.writeCoords(this.p1);
        this.writeCoords(this.p2);
    }

    /**
     * Liest (sofern vorhanden) Tastendrücke von den Clients, behandelt danach
     * Kollisionen von Ball
     */
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
                        this.p1.setKeystatus(key, pressed);
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
                        this.p2.setKeystatus(key, pressed);
                        break;
                    default:
                        System.err.println("Wrong keycode: " + eventtype);
                }
            }

            this.checkCollisions();

            // handle gravity
            if (this.ball.getYCoord() < Board.FLOOR) {
                this.ball.getVector().add(Vector2D.GRAVITY);
                this.ball.getVector().multiply(Vector2D.FRICTION_FACTOR_AIR);
            } else if(this.ball.getYCoord() >= Board.FLOOR) {
                this.ball.getVector().changeYDir();
            }
                
            if (this.p1.slime.getYCoord() < Board.SLIME_FLOOR) {
                this.p1.slime.getVector().add(Vector2D.GRAVITY);
            } else if(this.p1.slime.getYCoord() >= Board.SLIME_FLOOR) {
                this.p1.slime.getVector().setY(0);
            }
            
            if (this.p2.slime.getYCoord() < Board.SLIME_FLOOR) {
                this.p2.slime.getVector().add(Vector2D.GRAVITY);
            }else if(this.p2.slime.getYCoord() >= Board.SLIME_FLOOR) {
                this.p2.slime.getVector().setY(0);
            }

            this.ball.update();     // move ball
            this.p1.update();       // update slime vector (key pressed)
            this.p2.update();       // update slime vector (key pressed)
            this.p1.slime.update(); // move slime
            this.p2.slime.update(); // move slime

            this.writeCoords(this.p1);
            this.writeCoords(this.p2);
        } catch (IOException ioe) {
            System.err.println(ioe);
            this.cancel();              // Timer abbrechen, nach IO-Error whs keine vernünftige Kommunikation mehr möglich

            try {
                this.p1.socket.close();
                this.p2.socket.close();
            } catch (IOException ioe2) {
                System.err.println(ioe2);
            }
        }
    }

    /**
     * Behandelt Kollisionen des Balles mit verschiedenen Objekten am Spielfeld
     *  Reihenfolge: Slime1, Slime2, linke und rechte Wand, Himmel und Boden
     * Dabei wird der Vector des Balles entsprechend angepasst
     */
    private void checkCollisions() {
        float ball_slime_diff = Ball.STANDARD_RADIUS + Slime.SLIME_RADIUS;
        Vector2D v1 = new Vector2D(this.ball.getXCoord() - this.p1.slime.getXCoord(),
                this.ball.getYCoord() - this.p1.slime.getYCoord());
        Vector2D v2 = new Vector2D(this.ball.getXCoord() - this.p2.slime.getXCoord(),
                this.ball.getYCoord() - this.p2.slime.getYCoord());

        // Kollission von Slime 1 mit Ball
        if (v1.squarelength() <= ball_slime_diff * ball_slime_diff) {
            this.reflectBallFromSlime(v1);
        }

        // Kollision von Slime 2 mit Ball
        if (v2.squarelength() <= ball_slime_diff * ball_slime_diff) {
            this.reflectBallFromSlime(v2);
        }

        // Kollision von Ball mit linker oder rechter Wand
        if (this.ball.getXCoord() <= 0 || this.ball.getXCoord() >= Client.BOARD_WIDTH) {
            this.ball.getVector().changeXDir();
        }

        // Kollision von Ball mit "Himmel" oder Boden
        if (this.ball.getYCoord() <= 0 || this.ball.getYCoord() >= Board.SLIME_FLOOR) {
            this.ball.getVector().changeYDir();
            this.ball.getVector().multiply(Vector2D.FRICTION_FACTOR_FLOOR);
        }

    }

    /**
     * slimeToBall = Vector vom Mittelpunkt des Slimes zum Mittelpunkt des Balls
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
    private void reflectBallFromSlime(Vector2D slimeToBall) {
        System.out.println("reflect ball! vektor vorher: " +ball.getVector() );
        Vector2D r = slimeToBall.einheitsVector().multiply(Slime.SLIME_RADIUS);
        double L = Math.abs(this.ball.getVector().scalarProduct(r) / this.ball.getVector().length());
        Vector2D lot = r.einheitsVector().multiply(L);
        //Changed: Berechnung allgemein hat gestimmt, aber die länge des austrittvektors nicht
        //Also vom Austrittsvektor dein Einheitsvektor, multiplizieren mit der alten länge des balls
        //-> Länge eintrittsvektor = länge austrittsvektor            |Ab hier neu   
        Vector2D austritt = lot.multiply(2).add(this.ball.getVector()).einheitsVector().multiply(ball.getVector().length());
        this.ball.setVector(austritt);
        System.out.println("vektor nachher:" +austritt);
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
}
