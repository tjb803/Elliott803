/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2009
 */
package elliott803.view.component;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
 * A component to display a small light.
 *
 * @author Baldwin
 */
public class DisplayLight extends JPanel {
    private static final long serialVersionUID = 1L;

    public static final Color CYAN = Color.CYAN.darker();
    public static final Color GREEN = Color.GREEN.darker();
    public static final Color RED = Color.RED.darker();

    boolean lightOn;
    
    public DisplayLight(Dimension size, Color colour) {
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        setAlignmentX(LEFT_ALIGNMENT);
        setMaximumSize(size);
        setPreferredSize(size);
        setForeground(colour);
    }

    public void setValue(boolean on) {
        lightOn = on;
        repaint();
    }
    
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (lightOn) {
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}
