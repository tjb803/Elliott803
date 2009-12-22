/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2009
 */
package elliott803.view.component;

import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Display a console panel light.
 *
 * @author Baldwin
 */
public class ConsoleLight extends JPanel {
    private static final long serialVersionUID = 1L;

    static final Dimension size = new Dimension(35, 9);

    DisplayLight light;

    public ConsoleLight(String name, String name2) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setAlignmentX(CENTER_ALIGNMENT);

        light = new DisplayLight(size, DisplayLight.CYAN);
        light.setAlignmentX(CENTER_ALIGNMENT);
        add(light);

        JLabel t = new JLabel(name);
        t.setAlignmentX(CENTER_ALIGNMENT);
        add(t);
        if (name2 != null) {
            t = new JLabel(name2);
            t.setAlignmentX(CENTER_ALIGNMENT);
            add(t);
        }
    }

    public void setValue(boolean on) {
        light.setValue(on);
    }
}
