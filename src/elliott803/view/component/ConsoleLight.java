/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2009,2013
 */
package elliott803.view.component;

import java.awt.Dimension;
import java.util.StringTokenizer;

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

    static final Dimension SIZE = new Dimension(35, 8);

    DisplayLight light;

    public ConsoleLight(String name) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setAlignmentX(CENTER_ALIGNMENT);

        light = new DisplayLight(SIZE, DisplayLight.CYAN);
        light.setAlignmentX(CENTER_ALIGNMENT);
        add(light);
        StringTokenizer t = new StringTokenizer(name, "/");
        while (t.hasMoreTokens()) {
            JLabel label = new JLabel(t.nextToken());
            label.setAlignmentX(CENTER_ALIGNMENT);
            add(label);
        }
    }

    public void setValue(boolean on) {
        light.setValue(on);
    }
}
