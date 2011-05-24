/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sonstiges;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;
import slimesoccer.Slime;
import slimesoccer.Vector2D;

/**
 *
 * @author Martin Kamleithner
 * Diese Klasse habe ich nur zu Testzwecken, wie man den Halbkreis am besten Zeichnet, erstellt.
 * Nicht so implementieren, da sind einige unschöne Abkürzungen dabei.
 * Es flackert trotz DoubleBuffering, das ist irgendwie net gut...
 */
public class SlimePanelGravity extends JPanel {

    private Slime slime = new Slime(20, 120, new Vector2D(3,-5));
    private static final Vector2D gravity = new Vector2D(0.0,0.15);

 
    public SlimePanelGravity() {
    }

    public SlimePanelGravity(boolean doublebuffering) {
        super(doublebuffering);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2D = (Graphics2D) g; //Graphics2D bietet Anti-Aliasing
        g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); //Wirkt durch AA um einiges schöner!
        g2D.fillArc(slime.getXCoord() % this.getWidth(), slime.getYCoord() % this.getHeight(), (int) Slime.SLIME_RADIUS, (int) Slime.SLIME_RADIUS, 0, 180); //Halbkreis zeichnen
        this.slime.getVector().add(gravity);
        this.slime.getVector().shorten(0.9995);
        slime.update();



    }
}
