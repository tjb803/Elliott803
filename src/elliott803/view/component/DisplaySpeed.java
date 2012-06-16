/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2012
 */
package elliott803.view.component;

import java.awt.Font;

import javax.swing.JLabel;

/**
 * Display the CPU speed factor.
 */
public class DisplaySpeed extends JLabel {
    private static final long serialVersionUID = 1L;

    static Font speedFont = Font.decode("monospaced-bold-18");
    
    public DisplaySpeed(float value) {
        setFont(speedFont);
        setValue(value);
    }

    public void setValue(float value) {
        setText(String.format("%6.1f", value));
    }
}
