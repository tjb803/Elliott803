/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2013
 */
package elliott803.view.component;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

/**
 * Display the console loudspeaker grill.
 *
 * @author Baldwin
 */
public class DisplaySpeaker extends JPanel {
    private static final long serialVersionUID = 1L;

    static final Dimension SIZE = new Dimension(90, 24);
    
    public DisplaySpeaker() {
        setAlignmentX(CENTER_ALIGNMENT);
        setMaximumSize(SIZE);
        setPreferredSize(SIZE);
        setForeground(Color.LIGHT_GRAY);
    }
    
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (int y = 0; y < 24; y+=6) {
            g.fillRect(10, y, 30 ,3);  
            g.fillRect(50, y, 30, 3);
        }
    }
}
