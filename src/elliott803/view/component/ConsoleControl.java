/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2013
 */
package elliott803.view.component;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import elliott803.hardware.Console;

/**
 * Group of console buttons that define the "controls", i.e. 
 * Clear Store/Manual Data/Reset
 *
 * @author Baldwin
 */
public class ConsoleControl extends JPanel implements ActionListener {
    private static final long serialVersionUID = 1L;

    static final String OPERATION_CLEAR = "Clear/Store";
    static final String OPERATION_MANUAL = "Manual/Data";
    static final String OPERATION_RESET = "Reset";

    Console console;

    public ConsoleControl(Console console) {
        this.console = console;
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setAlignmentX(CENTER_ALIGNMENT);
        add(new ConsoleOpButton(OPERATION_CLEAR, true, false, this));
        add(Box.createHorizontalGlue());
        add(new ConsoleOpButton(OPERATION_MANUAL, false, false, this));
        add(Box.createHorizontalGlue());
        add(new ConsoleOpButton(OPERATION_RESET, true, true, this));
    }

    /*
     * Control button clicked, so forward function to Console
     */
    public void actionPerformed(ActionEvent e) {
        String action = e.getActionCommand();
        PushButton button = (PushButton)e.getSource(); 
        if (action.equals(OPERATION_CLEAR)) {
            console.setClearStore(button.isSelected());
        } else if (action.equals(OPERATION_MANUAL)) {
            console.setManualData(button.isSelected());
        } else if (action.equals(OPERATION_RESET)) { 
            console.reset();
        }    
    }
}
