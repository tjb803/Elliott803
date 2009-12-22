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

    public ConsoleButton(String name, boolean selected) {
        super(name, selected);
        setHorizontalTextPosition(CENTER);
        setVerticalTextPosition(BOTTOM);
    }

    public ConsoleButton(String name, Color colour, int bit) {
        this(name, false);
        setForeground(colour);
        setActionCommand(Integer.toString(bit));
    }
}
