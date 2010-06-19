/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2009
 */
package elliott803.view.component;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Display a light on reader/punch device.
 *
 * @author Baldwin
 */
public class DeviceLight extends JPanel {
    private static final long serialVersionUID = 1L;

    static final Dimension size = new Dimension(9, 9);

    DisplayLight light;

    public DeviceLight(String name, Color colour) {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setAlignmentX(RIGHT_ALIGNMENT);

        light = new DisplayLight(size, colour);

        add(new JLabel(name + ":"));
        add(Box.createHorizontalStrut(5));
        add(light);
        setMaximumSize(getMinimumSize());
    }

    public void setValue(boolean on) {
        light.setValue(on);
    }
}
