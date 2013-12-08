/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2013
 */
package elliott803.view.component;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.border.LineBorder;

/**
 * A circular swing border. 
 *
 * @author Baldwin
 */
public class CircleBorder extends LineBorder {
    private static final long serialVersionUID = 1L;

    public CircleBorder(Color colour, int thinkness) {
        super(colour, thinkness);
    }
    
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Color cc = g.getColor();
        g.setColor(lineColor);
        for (int i = 0; i < thickness; i++)
            g.drawOval(x+i, y+i, width-i-i-1, height-i-i-1);
        g.setColor(cc);
    }
}
