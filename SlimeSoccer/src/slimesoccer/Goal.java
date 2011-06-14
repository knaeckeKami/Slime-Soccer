package slimesoccer;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.ImageObserver;
import javax.swing.ImageIcon;

/**
 * Last modified: 14.06.2011
 * @author Timo Hinterleitner
 * @author Martin Kamleithner
 */
public class Goal extends MoveAble {

    private Image img;
    private int height;
    private int width;

    /**
     * Erzeugt ein neues Tor, welches abhängig von seiner Position (links oder rechts)
     * ein anderes Aussehen (Image) erhält
     * @param left Ist das Tor links? wenn false wird es als Tor am rechten Rand dargestellt
     */
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

    /**
     * Zeichnet das Tor mit Hilfe des Graphics Objekts g und dem ImageObserver io
     * @param g Graphics Objekt mit dem gezeichnet werden soll
     * @param io ImageObserver für das Bild
     */
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
