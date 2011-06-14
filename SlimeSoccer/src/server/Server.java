package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Timer;

/**
 * Last modified: 14.06.2011
 * @author Timo Hinterleitner
 * @author Martin Kamleithner
 */
public class Server {

    /**
     * Startet einenen neuen paralellen Server, der für jeweils 2 Spieler (Connections)
     * ein SlimeSoccer-Game startet.
     * Der Spieler, der sich zuerst verbindet, ist Player 1, der andere Player 2
     * Standardmäßig wird der Server auf Port 1337 gestartet, durch Parameterübergabe beim Start
     * kann dies allerdings geändert werden.
     * Das Spiel wird mit 25 fps gespielt, eine geeignete Netzwerkverbindung muss dafür vorhanden sein.
     * @param args 1. Parameter: Port
     */
    public static void main(String[] args) {
        int fps = 25;
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
                player1.setTcpNoDelay(true);
                System.out.println("Awaiting second connection on port " + port);
                Socket player2 = ss.accept();
                player2.setTcpNoDelay(true);
                System.out.println("Creating game for " + player1.getInetAddress().getHostAddress() + " and " + player2.getInetAddress().getHostAddress());
                new Timer().scheduleAtFixedRate(new ServerWorker(player1, player2), 10, 1000 / fps);
            }
        } catch (IOException ioe) {
            System.err.println(ioe);
        }
    }
}
