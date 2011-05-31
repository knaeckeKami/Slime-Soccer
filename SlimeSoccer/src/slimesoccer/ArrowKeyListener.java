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
    private boolean keypressed[] = new boolean[3];

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
        if ((keycode == KeyEvent.VK_LEFT || keycode == KeyEvent.VK_RIGHT || keycode == KeyEvent.VK_UP) && this.keypressed[keycode-0x25] != pressed) {
            try {
                System.out.println("Debug: sending " + keycode + " , pressed:" + pressed);
                dout.writeByte(Constants.TYPE_KEY);
                dout.writeByte(keycode);
                dout.writeBoolean(pressed);
                this.keypressed[keycode-0x25] = pressed;
            } catch (IOException ex) {
                System.err.println(ex.getMessage());
            }
        }
    }
}
