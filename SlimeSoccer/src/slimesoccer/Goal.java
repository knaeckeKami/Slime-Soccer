package slimesoccer;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;

/**
 *
 * @author edvo
 */
public class Goal extends MoveAble {

    private Image img;

    public Goal(boolean left) {
        if (left) {
            this.img = Toolkit.getDefaultToolkit().getImage("res/left.png");
            this.x = 0;
        } else {
            this.img = Toolkit.getDefaultToolkit().getImage("res/right.png");
            this.x = client.Client.BOARD_WIDTH - this.img.getWidth(null);
        }
        this.y = Board.FLOOR - this.img.getHeight(null);
    }

    public void draw(Graphics g) {
        g.drawImage(this.img, this.x, this.y, null);
    }
}
