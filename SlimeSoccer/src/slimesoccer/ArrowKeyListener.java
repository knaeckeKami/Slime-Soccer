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
        keySend(e.getKeyCode(), true);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        keySend(e.getKeyCode(), false);
    }

    private void keySend(int keycode, boolean pressed) {
        if ( keycode  == KeyEvent.VK_LEFT || keycode  == KeyEvent.VK_RIGHT || keycode == KeyEvent.VK_UP) {
            try {

                dout.writeByte(Constants.TYPE_KEY);
                dout.writeByte(keycode);
                dout.writeBoolean(pressed);
            } catch (IOException ex) {
                System.err.println(ex.getMessage());
            }
        }
    }
}
