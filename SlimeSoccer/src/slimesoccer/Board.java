package slimesoccer;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 * @author 3BHDV - Timo Hinterleitner
 * @author 3BHDV - Martin Kamleithner
 */
public class Board extends JPanel {

    public static final int FLOOR = 480;
    public static final int SLIME_FLOOR = (int) (Board.FLOOR - Slime.SLIME_DIAGONALE / 2);      // weil der anscheinend a rechteck übern slime legt, und des eck links oben 0/0 is..
    // anscheinend zeichnet der in GANZEN kreis, bzw legt zumindest des rechteck so drüber als wärs a ganzer.. FAIL
    public static final int BALL_FLOOR = (int) (Board.FLOOR - Ball.BALL_DIAGONALE);
    public static final int GOAL_DISPLAY_HEIGHT = 40;
    public static final int FPS_DISPLAY_HEIGHT = 30;
    public static final Font GOAL_FONT = Font.decode("COURIER NEW-PLAIN-36");
    public static final Font FPS_FONT = Font.decode("COURIER NEW-PLAIN-24");
    private Ball ball;
    private Player ownPlayer;
    private Player enemyPlayer;
    private boolean rightSide;
    private long oldtime;

    public Board() {
        this(600, 400);
    }

    /**
     * Erzeugt ein neues Board auf welchem das Spiel stattfindet.
     * Auf dem Board werden alle Elemente (Slime, Goal, Ball) gezeichnet
     * @param width Breite des Boards
     * @param height Höhe des Boards
     */
    public Board(int width, int height) {
        super(true);    // enable double buffering
        this.setSize(width, height);
        try {
            this.ownPlayer = new Player(1, null);
            this.enemyPlayer = new Player(2, null);
        } catch (IOException ioe) {
            // tritt nie auf weil socket = null
        }
        this.ball = new Ball(Ball.BALL_DIAGONALE, Color.RED);
        this.setOpaque(true);
        this.setBackground(Color.DARK_GRAY);
    }

    /**
     * Startet das Spiel mit der Verbindung din
     * @param din InputStream zur Verbindung mit dem Server
     */
    public void startGame(DataInputStream din) {
        this.ownPlayer.dis = din;
        new Game().start();
    }

    /**
     * Zeichnet Floor, Slimes, Ball, Tore, Toranzahl und Anzahl der Frames pro Sekunde
     * Die optimale Frameanzahl wird durch den Server definiert und kann evtl durch langsame 
     * Netzwerkverbindung oder seeeeeehr langsame CPU vom Optimum abweichen.
     * @param g 
     */
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); //Enable AntiAliasing
        //Floor zeichnen
        //x=0, y= Höhe des Panels- Höhe des Floors (y geht nach unten!), Volle Breite, Definierte Höhe
        g.fillRect(0, Board.FLOOR, this.getWidth(), Board.FLOOR);

        //Slimes, Ball zeichnen
        this.enemyPlayer.slime.draw(g);
        this.ownPlayer.slime.draw(g);
        this.ball.draw(g);
        this.ownPlayer.goal.draw(g, this);
        this.enemyPlayer.goal.draw(g, this);

        //Toranzahl zeichnen
        g.setColor(Color.BLACK);
        g.setFont(Board.GOAL_FONT);
        if (this.rightSide) {
            g.drawString(Integer.toString(this.ownPlayer.goals), this.getWidth() - 50, Board.GOAL_DISPLAY_HEIGHT);
            g.drawString(Integer.toString(this.enemyPlayer.goals), 50, Board.GOAL_DISPLAY_HEIGHT);
        } else {
            g.drawString(Integer.toString(this.ownPlayer.goals), 50, Board.GOAL_DISPLAY_HEIGHT);
            g.drawString(Integer.toString(this.enemyPlayer.goals), this.getWidth() - 50, Board.GOAL_DISPLAY_HEIGHT);
        }

        // FPS
        long timediff = System.currentTimeMillis() - this.oldtime;  // Differenz in ms
        g.setColor(Color.WHITE);
        g.setFont(Board.FPS_FONT);
        g.drawString(1000 / timediff + " fps", Constants.BOARD_WIDTH / 2 - 10, Board.FPS_DISPLAY_HEIGHT);
        this.oldtime = System.currentTimeMillis();
    }

    /**
     * Thread, der von din liest, die Daten interpretiert und Positonen, Spielstand etc aktualisiert.
     */
    class Game extends Thread {

        private boolean gameRunning = true;
        private static final int positonsSize = 6 * Integer.SIZE; //1 Kommandobyte, 3 x/y Integer
        private byte[] positions = new byte[positonsSize];
        private ByteBuffer byteBuf = ByteBuffer.wrap(positions);
        private IntBuffer intBuf = byteBuf.asIntBuffer(); //Damit kann man das byte[] mehr oder weniger als int[] betrachten

        @Override
        public void run() {

            int serverCommand; //1. Byte, das vom Server kommt, zB Constants.TYPE_COORDS
            DataInputStream din = Board.this.ownPlayer.dis;
            int readBytes;   //Gelesende Bytes zB für din.read(positions);
            while (gameRunning) {

                try {
                    //Commandobyte lesen
                    serverCommand = din.readByte();
                    switch (serverCommand) {
                        case Constants.TYPE_COORDS:
                            Board.this.ball.x = din.readInt();
                            Board.this.ball.y = din.readInt();
                            Board.this.ownPlayer.slime.x = din.readInt();
                            Board.this.ownPlayer.slime.y = din.readInt();
                            Board.this.enemyPlayer.slime.x = din.readInt();
                            Board.this.enemyPlayer.slime.y = din.readInt();
                            Board.this.repaint();
                            break;
                        case Constants.TYPE_GOAL:
                            this.addGoal(din.readBoolean());
                            break;
                        case Constants.TYPE_GAME_WIN:
                            if (din.readBoolean()) {
                                JOptionPane.showMessageDialog(Board.this, "Sie haben gewonnen!\n" + ownPlayer.goals + ":" + enemyPlayer.goals);
                            } else {
                                JOptionPane.showMessageDialog(Board.this, "Sie haben verloren!\n" + ownPlayer.goals + ":" + enemyPlayer.goals);
                            }
                            gameRunning = false;
                            break;
                        case Constants.TYPE_SIDE:
                            char side = (char) din.readByte();
                            if (side == 'r') {
                                Board.this.rightSide = true;
                            } else if (side == 'l') {
                                Board.this.rightSide = false;
                            } else {
                                System.out.println("Connection Error: side: " + side);
                            }
                            break;
                        default:
                            if (serverCommand == -1) {
                                System.err.println("Connection error!");
                                this.gameRunning = false;
                                break;
                            }
                            System.err.println("Kommunikaktionsfehler: Commando =" + serverCommand);
                            break;
                    }

                } catch (IOException ex) {
                    System.err.println("Fehler GameLoop: " + ex);
                    JOptionPane.showMessageDialog(Board.this, ex, "Connection error", JOptionPane.ERROR_MESSAGE);
                    this.gameRunning = false;            // weil nach IO Error kein (sinnvolles) spielen mehr möglich ist
                }
            }
        }

        /**
         * Erhöht den Torcounter des eigenen bzw gegnerischen Spielers
         * @param eigenerSpieler Gibt an ob der eigene Spieler ein Tor bekommen hat, oder der gegnerische
         */
        private void addGoal(boolean eigenerSpieler) {
            if (eigenerSpieler) {
                Board.this.ownPlayer.goals++;
            } else {
                Board.this.enemyPlayer.goals++;
            }

        }
    }
}
