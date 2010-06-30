/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2009
 */
package elliott803.view.component;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import elliott803.hardware.Console;

/**
 * Display a set of console buttons used as part of the word generator function.  Use
 * radio buttons for these as they look better (although really these act more like
 * check boxes).
 *
 * @author Baldwin
 */
public class ConsoleButtons extends JPanel implements ActionListener {
    private static final long serialVersionUID = 1L;
    
    Console console;
    
    ConsoleButton release;
    ConsoleButton[] buttons;
    
    public ConsoleButtons(String title, String[] names, int len, int bit, Console console) {
        this.console = console;
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setAlignmentX(LEFT_ALIGNMENT);
        setBorder(BorderFactory.createTitledBorder(title));
        
        release = new ConsoleButton("R", ConsoleButton.RED, true);
        release.addActionListener(this);
        add(release);
        add(Box.createHorizontalStrut(5));
        
        buttons = new ConsoleButton[len];
        for (int i = 0; i < len; i++) {
            Color colour = names[i].equals("B") ? ConsoleButton.RED : ConsoleButton.BLACK;
            buttons[i] = new ConsoleButton(names[i], colour, bit--);
            buttons[i].addActionListener(this);
            add(buttons[i]);
        }
    }
    
    /*
     * Word Generator button actions
     */

    public void actionPerformed(ActionEvent e) {
        ConsoleButton b = (ConsoleButton)e.getSource();
        if (!b.isSelected()) {
            b.setSelected(true);
        } else {
            if (b == release) {
                for (ConsoleButton c : buttons) {
                    console.clearWordGenBit(c.getBit());
                    c.setSelected(false);
                }
            } else {
                console.setWordGenBit(b.getBit());
                release.setSelected(false);
            }
        }
    }
}
