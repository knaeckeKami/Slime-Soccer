package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import slimesoccer.ArrowKeyListener;
import slimesoccer.Board;

/**
 * Last modified: 14.06.2011
 * @author Timo Hinterleitner
 * @author Martin Kamleithner
 */
public class Client extends JFrame {

    public static final int BOARD_WIDTH = 800;
    public static final int BOARD_HEIGHT = 500;
    private String serverIP = "localhost";
    private int serverPort = 1337;
    private Socket server;
    private Board board;

    /**
     * Fragt Server IP und Port ab und stellt eine Verbindung her
     * Fügt auch einen ArrowKeyListener am JFrame hinzu
     */
    public Client() {
        super("SlimeSoccer!!");
        this.initComponents();

        this.serverIP = JOptionPane.showInputDialog(this.getRootPane(), "Please enter Server IP: ", "localhost");
        String port = JOptionPane.showInputDialog(this.getRootPane(), "Please enter Server Port: ", "1337");
        try {
            this.serverPort = Integer.parseInt(port);
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this.getRootPane(), "Invalid value \"" + port + "\" for port, using standard value 1337", "Invalid value", JOptionPane.ERROR_MESSAGE);
            this.serverPort = 1337;
        }
        try {
            this.server = new Socket(this.serverIP, this.serverPort);
            this.server.setTcpNoDelay(true);
            this.addKeyListener(new ArrowKeyListener(new DataOutputStream(this.server.getOutputStream())));
        } catch (IOException ex) {
            System.err.println(ex);
        }

    }

    private void initComponents() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.getContentPane().add(this.board = new Board(Client.BOARD_WIDTH, Client.BOARD_HEIGHT));
        this.setSize(Client.BOARD_WIDTH+20, Client.BOARD_HEIGHT+50);
    }

    /**
     * Startet einen neuen Client und startet das Spiel
     * @param args 
     */
    public static void main(String... args) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                Client client = new Client();
                client.setVisible(true);
                try {
                    client.board.startGame(new DataInputStream(client.server.getInputStream()));
                } catch (IOException ex) {
                    System.out.println("Error startGame:" + ex.getMessage());
                }

            }
        });
    }
}
