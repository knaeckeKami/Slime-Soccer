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
    public static void main(String[] args) {
        int fps = 20;        // just for debugging
        int port = 1337;
        boolean running = true;
        
        if(args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch(NumberFormatException nfe) {
                System.err.println("Invalid port number " + args[0] + ", using standard 1337");
            }
        }
        
        try {
            ServerSocket ss = new ServerSocket(port);
            while (running) {
                System.out.println("Awaiting first connection on port " + port);
                Socket player1 = ss.accept();
                System.out.println("Awaiting second connection on port " + port);
                Socket player2 = ss.accept();
                System.out.println("Creating game for " + player1.getInetAddress().getHostAddress() + " and " + player2.getInetAddress().getHostAddress());
                new Timer().scheduleAtFixedRate(new ServerWorker(player1, player2), 10, 1000 / fps);
            }
        } catch (IOException ioe) {
            System.err.println(ioe);
        }
    }
}
