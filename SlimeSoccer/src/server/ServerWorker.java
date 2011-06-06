package server;

import client.Client;
import java.io.IOException;
import java.net.Socket;
import java.util.TimerTask;
import slimesoccer.*;

/**
 *  @todo known problem:
 *      ball bleibt manchmal an rechter wand hängen, wtf?
 *      tor is größer als eigentliches netz => daher problem beim zählen bzw abprallen
 *      slimes sollten im eigenen tor starten
 * @author TicTacMoe
 */
public class ServerWorker extends TimerTask {

    private Ball ball;
    private Player p1;
    private Player p2;
    private float BALL_SLIME_DIFF = Ball.BALL_RADIUS + Slime.SLIME_RADIUS;
    private float SQUARED_BALL_SLIME_DIFF = this.BALL_SLIME_DIFF * this.BALL_SLIME_DIFF;

    /**
     *
     * @param p1Socket Socket zum Spieler 1
     * @param p2Socket Socket zum Spieler 2
     * @throws IOException
     */
    public ServerWorker(Socket p1Socket, Socket p2Socket) throws IOException {
        this.ball = new Ball(Client.BOARD_WIDTH / 2, Client.BOARD_HEIGHT / 2, Ball.BALL_DIAGONALE);
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

            this.checkGoal();
            this.checkCollisions();

            // handle gravity
            if (this.ball.getYCoord() < Board.BALL_FLOOR) {
                this.ball.getVector().add(Vector2D.GRAVITY);
                this.ball.getVector().multiply(Vector2D.FRICTION_FACTOR_AIR);
            } else {
                // damit da ball ned "unendlich" lang herumhüpft, bzw dann genau auf floorlevel is
                // wenn ball langsam is, und in bodennähe => leg ihn ruhig am boden ^^
                // perfekten werte sollt ma durch ausprobieren finden ^^
                if (Math.abs(this.ball.getVector().getY()) < 3 && Math.abs(this.ball.getYCoord() - Board.BALL_FLOOR) < 0.5) {
                    this.ball.setYCoord(Board.BALL_FLOOR);
                    this.ball.getVector().setY(0);
                    this.ball.getVector().multiply(Vector2D.FRICTION_FACTOR_FLOOR);
                }

            }

            if (this.p1.slime.getYCoord() < Board.SLIME_FLOOR) {
                this.p1.slime.getVector().add(Vector2D.GRAVITY);
            } else if (this.p1.slime.getYCoord() >= Board.SLIME_FLOOR) {
                this.p1.slime.getVector().setY(0);
                this.p1.slime.setYCoord(Board.SLIME_FLOOR);
            }

            if (this.p2.slime.getYCoord() < Board.SLIME_FLOOR) {
                this.p2.slime.getVector().add(Vector2D.GRAVITY);
            } else if (this.p2.slime.getYCoord() >= Board.SLIME_FLOOR) {
                this.p2.slime.getVector().setY(0);
                this.p2.slime.setYCoord(Board.SLIME_FLOOR);
            }

            this.ball.update();     // move ball
            this.p1.update();       // update slime vector (key pressed)
            this.p2.update();       // update slime vector (key pressed)
            this.p1.slime.update(); // move slime
            this.p2.slime.update(); // move slime

//            System.out.println("Ball Vektor:" + ball.getVector());

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
        /* Vector2D v1 = new Vector2D((this.ball.getXCoord()+Ball.BALL_RADIUS/2) - (this.p1.slime.getXCoord()+Slime.SLIME_RADIUS/2),
        (this.ball.getYCoord()+Ball.BALL_RADIUS/2) - (this.p1.slime.getYCoord()+Slime.SLIME_RADIUS/2));
        Vector2D v2 = new Vector2D(this.ball.getXCoord()+Ball.BALL_RADIUS/2 - this.p2.slime.getXCoord()-Slime.SLIME_RADIUS/2,
        this.ball.getYCoord()+Ball.BALL_RADIUS/2 - this.p2.slime.getYCoord()-Slime.SLIME_RADIUS/2);*/
        Vector2D v1 = new Vector2D(this.ball.getMiddleX() - p1.slime.getMiddleX(), this.ball.getMiddleY() - p1.slime.getMiddleY());
        Vector2D v2 = new Vector2D(this.ball.getMiddleX() - p2.slime.getMiddleX(), this.ball.getMiddleY() - p2.slime.getMiddleY());

        // Kollission von Slime 1 mit Ball
        if (v1.squarelength() <= this.SQUARED_BALL_SLIME_DIFF) {
            this.reflectBallFromSlime(v1, p1.slime);
        }

        // Kollision von Slime 2 mit Ball
        if (v2.squarelength() <= this.SQUARED_BALL_SLIME_DIFF) {
            this.reflectBallFromSlime(v2, p2.slime);
        }

        // Kollision von Ball mit linker oder rechter Wand
        if (this.ball.getXCoord() <= 0 || this.ball.getXCoord() >= Client.BOARD_WIDTH) {
            this.ball.getVector().changeXDir();
        }

        // Kollision von Ball mit "Himmel" oder Boden
        if (this.ball.getYCoord() <= 0 || this.ball.getYCoord() >= Board.BALL_FLOOR) {
            this.ball.getVector().changeYDir();
        }

        // Kollision von Ball mit Torlatte links
        if (this.ball.getMiddleX() <= this.p1.goal.getXCoord() + this.p1.goal.getWidth() && Math.abs((this.p1.goal.getYCoord() - this.ball.getMiddleY())) < Ball.BALL_RADIUS / 2) {
            this.ball.getVector().changeYDir();
        } //Kollison von Ball mit Torstange rechts
        else if (this.ball.getMiddleX() >= this.p2.goal.getXCoord() && Math.abs((this.p2.goal.getYCoord() - this.ball.getMiddleY())) < Ball.BALL_RADIUS / 2) {
            this.ball.getVector().changeYDir();
        }

        // deine version ------------------------------------------------------------------------------------
        // im praxistest hat meine version besser funktioniert (bei deiner is manchmal random weit ÜBER dem tor reflektiert worden,
        // könnt allerdings auch an merge-conflicts liegen :/
        //Kollison von Ball mit Torstange rechts
//        Vector2D rightGoalBallVector = new Vector2D(rightGoal.getXCoord() - ball.getMiddleX(), rightGoal.getYCoord() - ball.getMiddleY());
//        if (rightGoalBallVector.squarelength() < Ball.BALL_RADIUS * Ball.BALL_RADIUS) {
//            ball.getVector().changeXDir();
//        }
//        //Von oben
//        if (ball.getMiddleX() >= this.rightGoal.getXCoord() && Math.abs((this.rightGoal.getYCoord() - this.ball.getMiddleY())) < Ball.BALL_RADIUS) {
//            this.ball.getVector().changeYDir();
//        }
//
//        //Kollision Ball -> Torstange links
//        Vector2D leftGoalBallVector = new Vector2D(this.leftGoal.getXCoord() + Goal.WIDTH-ball.getMiddleX(), this.leftGoal.getYCoord()-ball.getMiddleY());
//        if (leftGoalBallVector.squarelength() < Ball.BALL_RADIUS * Ball.BALL_RADIUS) {
//            ball.getVector().changeXDir();
//        }
//           if (ball.getMiddleX() <= Goal.WIDTH && Math.abs((this.rightGoal.getYCoord() - this.ball.getMiddleY())) < Ball.BALL_RADIUS) {
//            this.ball.getVector().changeYDir();
//        }
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
    private void reflectBallFromSlime(Vector2D slimeToBall, Slime collisionSlime) {
        //System.out.println("reflect ball! vektor vorher: " +ball.getVector() );
        //Wenn sich Ball nicht bewegt, und der Slime berührt den Ball -> Division durch null und so^^
        //Ich hab in diesem Fall den Ballvektor auf den Slimevektor gesetzt.
        System.out.println("Ball: " +ball.toString());
        System.out.println("Slime: "+ collisionSlime.toString());
        if (ball.getVector().squarelength() == 0) {
            ball.getVector().setXY(collisionSlime.getVector().getX(), collisionSlime.getVector().getY());
            return;
        }
        /*//Wenn der Ball genau von oben kommt, funktioniert der Pythagoras nicht.
        if(Math.abs(ball.getMiddleX()-collisionSlime.getMiddleX())<3&& this.ball.getVector().getX()<2){
            ball.getVector().changeYDir();
            return;
        } */

        Vector2D r = slimeToBall.einheitsVector().multiply(Slime.SLIME_DIAGONALE);
        double L = Math.abs(this.ball.getVector().scalarProduct(r) / this.ball.getVector().length());
        Vector2D lot = r.einheitsVector().multiply(L);
        //Changed: Berechnung allgemein hat gestimmt, aber die länge des austrittvektors nicht
        //Also vom Austrittsvektor den Einheitsvektor, multiplizieren mit der alten länge des balls
        //-> Länge eintrittsvektor = länge austrittsvektor            |Ab hier neu   
        Vector2D austritt = lot.multiply(2).add(this.ball.getVector()).einheitsVector().multiply(ball.getVector().length());
        //Bewegung des Slimes berücksichtigen
        austritt.add(collisionSlime.getVector());
        this.ball.setVector(austritt);
        //System.out.println("vektor nachher:" +austritt);
    }

    /**
     * 
     * @throws IOException 
     */
    private void checkGoal() throws IOException {
        // Ball muss > 50% im Tor sein
        if (this.ball.getMiddleX() < this.p1.goal.getXCoord() + this.p1.goal.getWidth() && this.ball.getMiddleY() > Client.BOARD_HEIGHT - this.p1.goal.getHeight()) {
            this.p1.goals++;

            this.p1.dos.writeByte(Constants.TYPE_GOAL);
            this.p1.dos.writeBoolean(true);                 // eigenes tor
            this.p2.dos.writeByte(Constants.TYPE_GOAL);
            this.p2.dos.writeBoolean(false);                // gegnerisches tor

           resetPositions();
            
        } else if (this.ball.getMiddleX() > this.p2.goal.getXCoord() && this.ball.getMiddleY() > Client.BOARD_HEIGHT - this.p2.goal.getHeight()) {
            this.p2.goals++;

            this.p2.dos.writeByte(Constants.TYPE_GOAL);
            this.p2.dos.writeBoolean(true);                 // eigenes tor
            this.p1.dos.writeByte(Constants.TYPE_GOAL);
            this.p1.dos.writeBoolean(false);                // gegnerisches tor

            resetPositions();
        }

        if (this.p1.goals == Constants.GOALS_REQUIRED) {
            this.p1.dos.writeByte(Constants.TYPE_GAME_WIN);
            this.p1.dos.writeBoolean(false);
            this.p2.dos.writeByte(Constants.TYPE_GAME_WIN);
            this.p2.dos.writeBoolean(true);
            this.cancel();      // spiel beendet
        } else if (this.p2.goals == Constants.GOALS_REQUIRED) {
            this.p1.dos.writeByte(Constants.TYPE_GAME_WIN);
            this.p1.dos.writeBoolean(true);
            this.p2.dos.writeByte(Constants.TYPE_GAME_WIN);
            this.p2.dos.writeBoolean(false);
            this.cancel();      // spiel beendet
        }
    }

    private void resetBall() {
        this.ball.setXCoord(Client.BOARD_WIDTH / 2);
        this.ball.setYCoord(Client.BOARD_HEIGHT / 2);
        this.ball.setVector(new Vector2D());
    }

    private void writeCoords(Player p) throws IOException {
        p.dos.writeByte(Constants.TYPE_COORDS);
        
        // x,y vom Ball
        p.dos.writeInt((int) this.ball.getXCoord());
        p.dos.writeInt((int) this.ball.getYCoord());
        
        // x,y vom eigenen Spieler
        p.dos.writeInt((int) p.slime.getXCoord());
        p.dos.writeInt((int) p.slime.getYCoord());

        // x,y vom anderen Spieler
        p.dos.writeInt((int) p.enemy.slime.getXCoord());
        p.dos.writeInt((int) p.enemy.slime.getYCoord());
    }
    
    private void resetPositions(){
            this.resetBall();
            this.p1.resetSlimePosition();
            this.p2.resetSlimePosition();
    }
}
