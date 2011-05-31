package slimesoccer;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import javax.swing.JPanel;

/**
 *
 * @author edvo
 */
public class Goal extends JPanel {
    private Image img;
    private int x;
    private int y;

    public Goal(boolean left) {
        if(left) {
            this.img = Toolkit.getDefaultToolkit().getImage("res/left.png");
            this.x = 0;
        } else {
            this.img = Toolkit.getDefaultToolkit().getImage("res/right.png");
            this.x = client.Client.BOARD_WIDTH - this.img.getWidth(null);
        }
        this.y = 480-this.img.getHeight(null);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(this.img, this.x, this.y, this);
    }
    
    
}
