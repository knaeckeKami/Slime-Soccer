/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package slimesoccer;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 *
 * @author edvo
 */
public class ArrowKeyListener extends KeyAdapter {

    private DataOutputStream dout;

    public ArrowKeyListener(DataOutputStream dout) {
        this.dout = dout;
    }



    @Override
    public void keyPressed(KeyEvent e) {
        keySend(e, true);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        keySend(e, false);
    }

    private void keySend(KeyEvent e, boolean pressed) {
        if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT) {
            try {

                dout.writeByte(Constants.TYPE_KEY);
                dout.writeByte(e.getKeyCode());
                dout.writeBoolean(pressed);
            } catch (IOException ex) {
                System.err.println(ex.getMessage());
            }
        }
    }
}
