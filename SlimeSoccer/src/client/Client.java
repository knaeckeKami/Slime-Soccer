package client;

import java.io.IOException;
import java.net.Socket;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import slimesoccer.Board;

/**
 * @TODO evtl JOptionPane erst anzeigen, wenn fenster geladen?
 * 
 * @author 3BHDV - Timo Hinterleitner
 * @author 3BHDV - Martin Kamleithner
 */
public class Client extends JFrame {

    private String serverIP = "localhost";
    private int serverPort = 1337;
    private Socket server;

    public Client() {
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
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }

    private void initComponents() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.getContentPane().add(new Board());
        this.setSize(500, 400);
    }

    public static void main(String... args) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                Client client = new Client();
                client.setVisible(true);
            }
        });
    }
}
