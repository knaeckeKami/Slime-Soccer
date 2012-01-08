package slimesoccer;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Last modified: 14.06.2011
 * @author Timo Hinterleitner
 * @author Martin Kamleithner
 */
public class ArrowKeyListener extends KeyAdapter {

    private DataOutputStream dout;
    private boolean keypressed[] = new boolean[3];

    /**
     * Erzeugt einen neuen ArrowKeyListener der die Tastendrücke
     * auf dout schreibt 
     * @param dout DataOutputStream auf den die Tastendrücke geschrieben werden sollen
     */
    public ArrowKeyListener(DataOutputStream dout) {
        this.dout = dout;
    }

    /**
     * Taste gedrückt => sende Tastendruck
     * @param e 
     */
    @Override
    public void keyPressed(KeyEvent e) {
        keySend(e.getKeyCode(), true);
    }

    /**
     * Taste losgelassen => sende Tastenloslassen
     * @param e 
     */
    @Override
    public void keyReleased(KeyEvent e) {
        keySend(e.getKeyCode(), false);
    }

    /**
     * Überprüft ob es sich um einen wichtigen keycode handelt (VK_LEFT, VK_RIGHT, VK_UP)
     * und ob sich sein Status seit dem letzten Aufruf verändert hat (um Tastendruck-Spam zu vermeiden)
     * Ist dies der Fall wird der neue Status an dout geschrieben
     * @param keycode KeyCode der gedrückten Taste
     * @param pressed Status der Taste (true = gedrückt, false = nicht gedrückt)
     */
    private void keySend(int keycode, boolean pressed) {
        if ((keycode == KeyEvent.VK_LEFT || keycode == KeyEvent.VK_RIGHT || keycode == KeyEvent.VK_UP) && this.keypressed[keycode-0x25] != pressed) {
            try {
                dout.writeByte(Constants.TYPE_KEY);
                dout.writeByte(keycode);
                dout.writeBoolean(pressed);
                dout.flush();
                this.keypressed[keycode-0x25] = pressed; //0x25: Offest der VK_xxx Konstanten von 0

            } catch (IOException ex) {
                System.err.println(ex.getMessage());
            }
        }
    }
}
