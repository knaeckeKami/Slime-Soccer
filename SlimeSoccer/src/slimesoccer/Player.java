/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package slimesoccer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * @author dv20080204 - Timo Hinterleitner - 3BHDV
 */
public class Player {

    /**
     * Variablen sind public um Zugriffe zu beschleunigen
     */
    public int number;
    public Slime slime;
    public Goal goal;
    public Socket socket;
    public DataInputStream dis;
    public DataOutputStream dos;
    public String name;
    public Player enemy;

    public Player(int number, Socket socket) throws IOException {
        this.number = number;
        this.goal = new Goal(number == 1 ? true : false);
        this.socket = socket;
        this.name = "Player " + number;
        this.socket = socket;

        switch (number) {
            case 1:
                this.slime = new Slime(Board.WIDTH / 4, Board.FLOOR);
                break;
            case 2:
                this.slime = new Slime(Board.WIDTH / 4 * 3, Board.FLOOR);
            default:
                break;
        }
        
        this.dis = new DataInputStream(socket.getInputStream());
        this.dos = new DataOutputStream(socket.getOutputStream());
    }

    public Player getGegner() {
        return enemy;
    }

    public void setGegner(Player gegner) {
        this.enemy = gegner;
    }

    public DataInputStream getDis() {
        return dis;
    }

    public void setDis(DataInputStream dis) {
        this.dis = dis;
    }

    public DataOutputStream getDos() {
        return dos;
    }

    public void setDos(DataOutputStream dos) {
        this.dos = dos;
    }

    public Goal getGoal() {
        return goal;
    }

    public void setGoal(Goal goal) {
        this.goal = goal;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Slime getSlime() {
        return slime;
    }

    public void setSlime(Slime slime) {
        this.slime = slime;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }
}
