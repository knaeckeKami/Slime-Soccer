package slimesoccer;

/**
 * @todo mehr konstanten in diese datei tun, übersichtlichkeit und so ^^
 * Last modified: 14.06.2011
 * @author Timo Hinterleitner
 * @author Martin Kamleithner
 */
public interface Constants {

    /**
     * Breite des Spielfeldes
     */
    public static final int BOARD_WIDTH = 800;
    /**
     * Höhe des Spielfeldes
     */
    public static final int BOARD_HEIGHT = 500;
    /**
     * g gefolgt von boolean (true=spieler, false=gegner)
     */
    public static final char TYPE_GOAL = 'g';
    /**
     * c gefolgt von 3 Integerpaaren:
     *  x,y für Ball
     *  x,y für eigenen Spieler
     *  x,y für gegnerischen Spieler
     */
    public static final char TYPE_COORDS = 'c';
    /**
     * k gefolgt von
     *  KeyEvent.VK_UP, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT
     * sowie boolean (true=pressed, false=released)
     */
    public static final char TYPE_KEY = 'k';
    /**
     * w gefolgt von boolean (true=spieler, false=gegner);
     */
    public static final char TYPE_GAME_WIN = 'w';
    /**
     * Anzahl der benötigten Tore für einen Sieg
     */
    public static final int GOALS_REQUIRED = 5;
    /**
     * Die Anzahl der Frames, die ein Slime innerhalb des eigenen Tores sein darf
     */
    public static final int ALLOWED_FRAMES_IN_OWN_GOAL = 100;
    /**
     * Die Seite, auf der der Spieler spielt
     * s gefolgt von l=left, r=right
     */
    public static final int TYPE_SIDE = 's';
}
