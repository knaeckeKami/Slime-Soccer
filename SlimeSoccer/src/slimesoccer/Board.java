package slimesoccer;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 * @todo FPS anzeige wär cool :D
 * @author 3BHDV - Timo Hinterleitner
 * @author 3BHDV - Martin Kamleithner
 */
public class Board extends JPanel {

    public static final int FLOOR = 480;
    public static final int SLIME_FLOOR = (int) (Board.FLOOR - Slime.SLIME_DIAGONALE / 2);      // weil der anscheinend a rechteck übern slime legt, und des eck links oben 0/0 is..
    // anscheinend zeichnet der in GANZEN kreis, bzw legt zumindest des rechteck so drüber als wärs a ganzer.. FAIL
    public static final int BALL_FLOOR = (int) (Board.FLOOR - Ball.BALL_DIAGONALE);
    public static final int GOAL_DISPLAY_HEIGHT = 40;
    private Ball ball;
    private Player ownPlayer;
    private Player enemyPlayer;

    public Board() {
        this(600, 400);
    }

    /**
     * 
     * @param width
     * @param height
     * @param din
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
//        this.addKeyListener(new ArrowKeyListener(this.dout));
    }

    public void startGame(DataInputStream din) {
        this.ownPlayer.dis = din;
        new Game().start();
    }

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

        g.drawLine((int) (this.ownPlayer.slime.x + Slime.SLIME_DIAGONALE / 2), (int) (this.ownPlayer.slime.y + Slime.SLIME_DIAGONALE / 2), (int) (this.ball.x + Ball.BALL_DIAGONALE / 2), (int) (this.ball.y + Ball.BALL_DIAGONALE / 2));



        //Toranzahl zeichnen
        g.setColor(Color.BLACK);
        //Font.decode = Performancekiller
        g.setFont(Font.decode("GoalString-COURIER_NEW-36"));
        g.drawString(Integer.toString(this.ownPlayer.goals), 50, Board.GOAL_DISPLAY_HEIGHT);
        g.drawString(Integer.toString(this.enemyPlayer.goals), this.getWidth() - 50, Board.GOAL_DISPLAY_HEIGHT);
    }

    /**
     * Thread, der von din liest, die Daten interpretiert und Positonen, Spielstand etc aktualisiert.
     * Noch komplett ungetestet!
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
                    serverCommand = din.read();
//                    System.out.println("Debug:  readbyte " + serverCommand);
                    switch (serverCommand) {
                        case Constants.TYPE_COORDS:
//                            System.out.println("Debug: Update Coords");
                            /**
                             * PROBLEM: 
                             * Wie liest man jetzt am besten
                             * Lösung:
                             ***** http://download.oracle.com/javase/1.4.2/docs/api/java/nio/ByteBuffer.html
                             ***** Nicht Blockende Byte-Buffer, die als Int-Buffer betrachtet werden können!
                             ***** IST IMPLEMENTIERT, ABER NOCH UNGETESTET!!
                             * 
                            
                             */
                            Board.this.ball.x = din.readInt();
                            Board.this.ball.y = din.readInt();
                            Board.this.ownPlayer.slime.x = din.readInt();
                            Board.this.ownPlayer.slime.y = din.readInt();
                            Board.this.enemyPlayer.slime.x = din.readInt();
                            Board.this.enemyPlayer.slime.y = din.readInt();
                            Board.this.repaint();
                            break;
//                            readBytes = din.read(positions);
//                            System.out.println("Debug: readByte: " +readBytes);
//                            if (readBytes != positonsSize) {
//                                //I glaub es is besser, das Package wegzuschmeißen und das nächste zu lesen wenn was schiefgangen is
//                                //Als dann extra Fehlerbehandlung zu machen was Zeit kostet und evtl Performance runterzieht
//                                System.err.println("Fehlerhaftes Package! Gelesene bytes: " + readBytes + "Inhalt:" + Arrays.toString(positions));
//                                continue;
//                            }else{
//                                //Zurücksetzen der LesePosition
//                                intBuf.clear();
//                                //Updaten der Positonen
//                                ball.x=  intBuf.get();
//                                ball.y=  intBuf.get();
//                                player.x=intBuf.get();
//                                player.y=intBuf.get();
//                                enemy.x= intBuf.get();
//                                enemy.y= intBuf.get();
//                                Board.this.repaint();
//                                break;
//                            }

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
                        case Constants.TYPE_NAME:
                            System.out.println("Debug: reading names");
                            Board.this.enemyPlayer.name = new BufferedReader(new InputStreamReader(din)).readLine();
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
                    System.err.println("Fehler GameLoop: " + ex.getMessage());
                    this.gameRunning = false;            // weil nach IO Error kein sinnvolles spielen mehr möglich ist
                }
            }
        }

        private void addGoal(boolean eigenerSpieler) {
            if (eigenerSpieler) {
                Board.this.ownPlayer.goals++;
            } else {
                Board.this.enemyPlayer.goals++;
            }

        }
    }
}
