/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2009,2013
 */
package elliott803.view.component;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JRadioButton;

/**
 * Display a console word generator button.  This is just a standard radio button 
 * but with the text displayed beneath the button image.
 *
 * @author Baldwin
 */
public class ConsoleBitButton extends JRadioButton {
    private static final long serialVersionUID = 1L;
    
    public static final Color RED = Color.RED.darker();
    public static final Color BLACK = Color.BLACK;
    
    int bit;

    public ConsoleBitButton(String name, Color colour, int bit) {
        super(name, (bit==0));
        setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));
        setFocusable(false);
        setForeground(colour);
        setHorizontalTextPosition(CENTER);
        setVerticalTextPosition(BOTTOM);
        setIconTextGap(1);
        this.bit = bit;
    }

    public int getBit() {
        return bit;
    }
}
