package slimesoccer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private int ownGoals, otherGoals;

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
        this.player = new Slime();
        this.enemy = new Slime();
        this.ball = new Ball(Ball.STANDARD_RADIUS);
//        this.addKeyListener(new ArrowKeyListener(this.dout));
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
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); //Enable AntiAliasing
        //Floor zeichnen
        //x=0, y= Höhe des Panels- Höhe des Floors (y geht nach unten!), Volle Breite, Definierte Höhe
        g.fillRect(0, Board.FLOOR, this.getWidth(), Board.FLOOR);

        //Slimes, Ball zeichnen
        enemy.draw(g);
        player.draw(g);
        ball.draw(g);

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

        /**
         * Wah manchmal hasse ich es das Java keine Pointer hat... in C wäre das um einiges einfacher, binär lesen
         * dann halt als int oder whatever interpretieren.. </flame>
         */
        @Override
        public void run() {

            int serverCommand; //1. Byte, das vom Server kommt, zB Constants.TYPE_COORDS
            
            int readBytes;   //Gelesende Bytes zB für din.read(positions);
            while (gameRunning) {

                try {
                    //Commandobyte lesen
                    serverCommand = Board.this.din.read();
                    switch (serverCommand) {
                        case Constants.TYPE_COORDS:
                            /**
                             * PROBLEM: 
                             * Wie liest man jetzt am besten?
                             * Wenn man ein byte[] liest, in dem die x/y Coords liegen, hat man das Problem, wie macht man performant ints daraus.
                             * Zum Lesen eines int[] gibt es keine Methode,
                             * Wenn man einzelne ints liest, hat man wieder das Problem, was ist wenn der Stream blockt?)
                             * 
                             *****eventuell Lösung:
                             ***** http://download.oracle.com/javase/1.4.2/docs/api/java/nio/ByteBuffer.html
                             ***** Nicht Blockende Byte-Buffer, die als Int-Buffer betrachtet werden können!
                             ***** IST IMPLEMENTIERT, ABER NOCH UNGETESTET!!
                             * 
                             * 
                             * Andere Lösung:
                             * In einem separaten Thread lesen, da ist es egal wenn er geblockt wird.
                             * Problem: Synchronisation, Performance...
                             */
                            din.read(positions);
                            readBytes = din.read(positions);
                            if (readBytes != positonsSize) {
                                //I glaub es is besser, das Package wegzuschmeißen und das nächste zu lesen wenn was schiefgangen is
                                //Als dann extra Fehlerbehandlung zu machen was Zeit kostet und evtl Performance runterzieht
                                System.err.println("Fehlerhaftes Package! Gelesene bytes: " + readBytes + "Inhalt:" + Arrays.toString(positions));
                                continue;
                            }else{
                                //Zurücksetzen der LesePosition
                                intBuf.clear();
                                //Updaten der Positonen
                                ball.x=intBuf.get();
                                ball.y=intBuf.get();
                                player.x=intBuf.get();
                                player.y=intBuf.get();
                                enemy.x=intBuf.get();
                                enemy.y=intBuf.get();
                                        
                            }
                            
                        case Constants.TYPE_GOAL:
                            addGoal();
                        case Constants.TYPE_GAME_WIN:
                            break;
                        default:
                            System.err.println("Kommunikaktionsfehler: Commando =" + serverCommand);
                            break;
                    }

                } catch (IOException ex) {
                    System.err.println("Fehler GameLoop: " + ex.getMessage());
                }
            }
        }

        private void addGoal() {
        }
    }
}
