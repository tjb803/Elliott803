/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2009
 */
package elliott803.view.component;

import java.awt.Font;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Display the current mode of a reader/punch device.
 *
 * @author Baldwin
 */
public class DeviceMode extends JPanel {
    private static final long serialVersionUID = 1L;

    public final static String MODE_AUTO = "Auto";
    public final static String MODE_ELLIOTT = "Elliott";
    public final static String MODE_SYSTEM = "System";
    public final static String MODE_ASCII = "US-ASCII";

    JLabel mode;

    protected DeviceMode() {
        // Empty constructor for subclasses
    }

    public DeviceMode(String style) {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setAlignmentX(RIGHT_ALIGNMENT);

        mode = new JLabel();
        mode.setFont(Font.decode("monospaced"));
        mode.setText(style);

        add(new JLabel("Mode:"));
        add(Box.createHorizontalStrut(5));
        add(mode);
    }

    public void setMode(String style) {
        mode.setText(style);
    }
}
