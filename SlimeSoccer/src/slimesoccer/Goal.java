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
    private int height;
    private int width;

    public Goal(boolean left) {
        if (left) {
            this.img = new ImageIcon(this.getClass().getResource("../res/left.png")).getImage();
            this.x = 0;
        } else {
            this.img = new ImageIcon(this.getClass().getResource("../res/right.png")).getImage();
            this.x = Constants.BOARD_WIDTH - this.img.getWidth(null);
        }
        this.height = img.getHeight(null);
        this.width = img.getWidth(null);
        this.y = Board.FLOOR - this.height;
    }

    public void draw(Graphics g, ImageObserver io) {
        g.drawImage(this.img, Math.round(x), Math.round(y), io);
    }

    public void draw(Graphics g) {
        this.draw(g, null);
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }
}
