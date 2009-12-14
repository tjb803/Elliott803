/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2009
 */
package elliott803.view.component;

import java.awt.Color;
import java.awt.Dimension;

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

    public DisplayLight(Dimension size, Color colour) {
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        setAlignmentX(LEFT_ALIGNMENT);
        setMaximumSize(size);
        setPreferredSize(size);
        setBackground(colour);
        setOpaque(false);
    }

    public void setValue(boolean on) {
        setOpaque(on);
        repaint();
    }
}
