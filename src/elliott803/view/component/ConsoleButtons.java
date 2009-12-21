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
    
    ConsoleButton clear;
    ConsoleButton[] buttons;
    
    public ConsoleButtons(String title, String[] names, int len, int bit, Console console) {
        this.console = console;
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setAlignmentX(LEFT_ALIGNMENT);
        setBorder(BorderFactory.createTitledBorder(title));
        
        clear = new ConsoleButton("clear", ConsoleButton.RED, false);
        clear.addActionListener(this);
        add(clear);
        add(Box.createHorizontalStrut(3));
        
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
        if (b == clear) {
            for (ConsoleButton c : buttons) {
                c.setSelected(false);
                console.clearWordGenBit(c.getBit());
            }
            clear.setSelected(false);
        } else {
            if (!b.isSelected()) {
                b.setSelected(true); 
            } else {
                console.setWordGenBit(b.getBit());
            }
        }
    }
}
