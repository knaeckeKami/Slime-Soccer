package slimesoccer;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 * @todo goal, slime, name, etc in Player-Objekt auslagern! (übersichtlichkeit und so ^^) aber nur wenn speed dadurch nicht beeinflusst wird
 * 
 * @author 3BHDV - Timo Hinterleitner
 * @author 3BHDV - Martin Kamleithner
 */
public class Board extends JPanel {

    public static final int FLOOR = 480;
    public static final int SLIME_FLOOR = (int)(Board.FLOOR - Slime.SLIME_DIAGONALE/2);      // weil der anscheinend a rechteck übern slime legt, und des eck links oben 0/0 is..
                                                                                          // anscheinend zeichnet der in GANZEN kreis, bzw legt zumindest des rechteck so drüber als wärs a ganzer.. FAIL
    public static final int BALL_FLOOR = (int)(Board.FLOOR - Ball.BALL_DIAGONALE);
    public static final int GOAL_DISPLAY_HEIGHT = 40;
    private Ball ball;
    private Goal leftGoal;
    private Goal rightGoal;
    private Slime player;
    private Slime enemy;
    private Socket server;
    private DataInputStream din;
    private DataOutputStream dout;
    private int ownGoals, enemyGoals;
    private String ownName, enemyName;

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
        this.player = new Slime();
        this.enemy = new Slime();
        this.ball = new Ball(Ball.BALL_DIAGONALE);
        this.leftGoal = new Goal(true);
        this.rightGoal = new Goal(false);
        this.setOpaque(true);
        this.setBackground(Color.DARK_GRAY);
//        this.addKeyListener(new ArrowKeyListener(this.dout));
    }

    public Board(int width, int height, Socket server) {
        super(true);    // enable double buffering
        this.setSize(width, height);
        this.server = server;
        this.player = new Slime();
        this.enemy = new Slime();
        this.ball = new Ball(Ball.BALL_DIAGONALE);
        this.leftGoal = new Goal(true);
        this.rightGoal = new Goal(false);
        
        this.player.setColor(Color.BLUE);
        this.enemy.setColor(Color.GREEN);
        
        try {
            this.din = new DataInputStream(server.getInputStream());
            this.dout = new DataOutputStream(server.getOutputStream());
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    public void startGame(DataInputStream din) {
        this.din = din;
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
        this.enemy.draw(g);
        this.player.draw(g);
        this.ball.draw(g);
        this.leftGoal.draw(g, this);
        this.rightGoal.draw(g, this);
        
        g.drawLine((int)(this.player.x+Slime.SLIME_DIAGONALE/2), (int)(this.player.y+Slime.SLIME_DIAGONALE/2), (int)(this.ball.x+Ball.BALL_DIAGONALE/2), (int)(this.ball.y+Ball.BALL_DIAGONALE/2));

       
        
        //Toranzahl zeichnen
        g.setColor(Color.BLACK);
        //Font.decode = Performancekiller
        g.setFont(Font.decode("GoalString-COURIER_NEW-36"));
        g.drawString(Integer.toString(ownGoals), 50, Board.GOAL_DISPLAY_HEIGHT);
        g.drawString(Integer.toString(enemyGoals), this.getWidth() - 50, Board.GOAL_DISPLAY_HEIGHT);
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

            int readBytes;   //Gelesende Bytes zB für din.read(positions);
            while (gameRunning) {

                try {
                    //Commandobyte lesen
                    serverCommand = Board.this.din.read();
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
                            ball.x = din.readInt();
                            ball.y = din.readInt();
                            player.x = din.readInt();
                            player.y = din.readInt();
                            enemy.x = din.readInt();
                            enemy.y = din.readInt();
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
                                JOptionPane.showMessageDialog(Board.this, "Sie haben Gewonnen!");
                            } else {
                                JOptionPane.showMessageDialog(Board.this, "Sie haben verloren!");
                            }
                            gameRunning = false;
                            break;
                        case Constants.TYPE_NAME:
                            System.out.println("Debug: reading names");
                            Board.this.enemyName = new BufferedReader(new InputStreamReader(din)).readLine();
                            break;
                        default:
                            if(serverCommand == -1) {
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
                Board.this.ownGoals++;
            } else {
                Board.this.enemyGoals++;
            }

        }
    }
}
