package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Timer;

/**
 *
 * @author edvo
 */
public class Server {

    /**
     *
     * @param args
     */
    public static void main(String args) {
        int fps = 20;
        boolean running = true;
        try {
            ServerSocket ss = new ServerSocket(1337);
            while (running) {
                Socket player1 = ss.accept();
                Socket player2 = ss.accept();
                new Timer().scheduleAtFixedRate(new ServerWorker(player1, player2), 10, 1000 / fps);
            }
        } catch (IOException ioe) {
            System.err.println(ioe);
        }
    }
}
