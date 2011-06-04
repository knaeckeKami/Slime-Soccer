/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package slimesoccer;

import java.awt.event.KeyEvent;
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
    public boolean[] keys = new boolean[3];

    public Player(int number, Socket socket) throws IOException {
        this.number = number;
        this.goal = new Goal(number == 1 ? true : false);
        this.socket = socket;
        this.name = "Player " + number;
        this.socket = socket;

        switch (number) {
            case 1:
                this.slime = new Slime(Board.WIDTH / 4, Board.SLIME_FLOOR);
                break;
            case 2:
                this.slime = new Slime(Board.WIDTH / 4 * 3, Board.SLIME_FLOOR);
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

    /**
     * Setzt den Keystatus der Tasten
     * VK_LEFT, VK_UP, oder VK_RIGHT
     * true wenn gedrückt, false wenn nicht
     * @param keycode muss VK_LEFT, VK_UP, oder VK_RIGHT sein!!
     * @param pressed true wenn gedrückt, false wenn nicht
     */
    public void setKeystatus(int keycode, boolean pressed) {
//        System.out.println("Debug: Player(" + number + ") set key status " + keycode + " to " + pressed);
        this.keys[keycode - 0x25] = pressed;    // 0x25 = offset zu VK_LEFT, VK_UP, VK_RIGHT
    }


    /**
     * Liefert den Keystatus einer Taste (VK_LEFT, VK_UP, oder VK_RIGHT)
     * true wenn gedrückt, false wenn nicht
     * @param keycode Code der gedrückten Taste (muss VK_LEFT, VK_UP, oder VK_RIGHT sein!!)
     * @return true wenn gedrückt, false wenn nicht
     */
    public boolean getKeystatus(int keycode) {
        return this.keys[keycode - 0x25];
    }

    public void update() {
        if(this.keys[KeyEvent.VK_LEFT - 0x25] && !this.keys[KeyEvent.VK_RIGHT - 0x25]) {
            this.slime.getVector().setX(-4);
        } else if(!this.keys[KeyEvent.VK_LEFT - 0x25] && this.keys[KeyEvent.VK_RIGHT - 0x25]) {
            this.slime.getVector().setX(4);
        } else {
            this.slime.getVector().setX(0);
        }
        if(this.keys[KeyEvent.VK_UP - 0x25] && Math.abs(this.slime.getYCoord() - Board.SLIME_FLOOR) < 0.01) {     // springen nur erlauben, wenn slime am boden
            this.slime.getVector().setY(-4);
        }
    }
}
