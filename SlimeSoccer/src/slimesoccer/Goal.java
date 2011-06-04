package slimesoccer;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.ImageObserver;
import javax.swing.ImageIcon;

/**
 *
 * @author edvo
 */
public class Goal extends MoveAble {

    private Image img;

    public Goal(boolean left) {
        if (left) {
            this.img = new ImageIcon(this.getClass().getResource("../res/left.png")).getImage();
            this.x = 0;
        } else {
            this.img = new ImageIcon(this.getClass().getResource("../res/right.png")).getImage();
            this.x = client.Client.BOARD_WIDTH - this.img.getWidth(null);
        }
        this.y = Board.FLOOR - this.img.getHeight(null);
    }

    public void draw(Graphics g, ImageObserver io) {
        g.drawImage(this.img, Math.round(x), Math.round(y), io);
    }

    public void draw(Graphics g) {
        this.draw(g, null);
    }


}
