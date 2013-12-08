/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2013
 */
package elliott803.view.component;

import java.awt.Color;
import java.awt.event.ActionListener;
import java.util.StringTokenizer;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

/**
 * Display a console operation button - used for the non-word generator
 * buttons.  These can have up to two lines of text and an optional shroud.
 * Radio buttons are used to display these as they look most like the 
 * original console, although operation is sometimes more like a push button
 * or check box. 
 *
 * @author Baldwin
 */
public class ConsoleOpButton extends JPanel {
    private static final long serialVersionUID = 1L;
    
    PushButton button;

    public ConsoleOpButton(String name, boolean shroud, boolean push, ActionListener listener) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setAlignmentY(TOP_ALIGNMENT);
        setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 3));
        
        button = new PushButton(push);
        button.setAlignmentX(CENTER_ALIGNMENT);
        button.setFocusable(false);
        Border b = (shroud) ? new CircleBorder(Color.BLACK, 2) : BorderFactory.createEmptyBorder(2, 2, 2, 2);
        button.setBorder(BorderFactory.createCompoundBorder(b, BorderFactory.createEmptyBorder(3, 3, 3, 3)));
        button.setBorderPainted(true);
        button.setActionCommand(name);
        button.addActionListener(listener);
        add(button);
        StringTokenizer t = new StringTokenizer(name, "/");
        while (t.hasMoreTokens()) {
            JLabel label = new JLabel(t.nextToken());
            label.setAlignmentX(CENTER_ALIGNMENT);
            add(label);
        }
    }
    
    public ConsoleOpButton(String name, boolean selected, ActionListener listener, ButtonGroup group) {
        this(name, false, false, listener);
        button.setSelected(selected);
        group.add(button);
    }
}
