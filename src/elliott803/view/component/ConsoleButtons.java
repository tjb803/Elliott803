/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2009
 */
package elliott803.view.component;

import java.awt.Color;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

/**
 * Display a set of console buttons used as part of the word generator function.  Use
 * radio buttons for these as they look better (although really these act more like
 * check boxes).
 *
 * @author Baldwin
 */
public class ConsoleButtons extends JPanel {
    private static final long serialVersionUID = 1L;

    public ConsoleButtons(String title, String[] names, int len, int bit, ActionListener action) {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setAlignmentX(LEFT_ALIGNMENT);
        setBorder(BorderFactory.createTitledBorder(title));
        for (int i = 0; i < len; i++) {
            Color colour = names[i].equals("B") ? Color.RED.darker() : Color.BLACK;
            ConsoleButton b = new ConsoleButton(names[i], colour, bit--);
            b.addActionListener(action);
            add(b);
        }
    }
}
