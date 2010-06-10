/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2009
 */
package elliott803.view.component;

import java.awt.Color;

import javax.swing.JRadioButton;

/**
 * Display a console button.  This is just a standard radio button but with the
 * text displayed beneath the button image.
 *
 * @author Baldwin
 */
public class ConsoleButton extends JRadioButton {
    private static final long serialVersionUID = 1L;
    
    public static final Color RED = Color.RED.darker();
    public static final Color BLACK = Color.black;
    
    int bit;

    public ConsoleButton(String name, Color colour, boolean selected) {
        super(name, selected);
        setFocusable(false);
        setForeground(colour);
        setHorizontalTextPosition(CENTER);
        setVerticalTextPosition(BOTTOM);
        setIconTextGap(1);
    }

    public ConsoleButton(String name, Color colour, int bit) {
        this(name, colour, false);
        this.bit = bit;
    }
    
    public int getBit() {
        return bit;
    }
}
