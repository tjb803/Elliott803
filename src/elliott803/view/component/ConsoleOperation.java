/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2009,2013
 */
package elliott803.view.component;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;

import elliott803.hardware.Console;

/**
 * Group of console buttons that define the "operation", i.e. Read/Normal/Obey
 *
 * @author Baldwin
 */
public class ConsoleOperation extends JPanel implements ActionListener {
    private static final long serialVersionUID = 1L;

    static final String OPERATION_READ = "Read";
    static final String OPERATION_NORMAL = "Normal";
    static final String OPERATION_OBEY = "Obey";

    Console console;

    public ConsoleOperation(Console console) {
        this.console = console;
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setAlignmentX(CENTER_ALIGNMENT);
        ButtonGroup group = new ButtonGroup();
        add(new ConsoleOpButton(OPERATION_READ, true, this, group));
        add(Box.createHorizontalGlue());
        add(new ConsoleOpButton(OPERATION_NORMAL, false, this, group));
        add(Box.createHorizontalGlue());
        add(new ConsoleOpButton(OPERATION_OBEY, false, this, group));
    }

    /*
     * Operation button clicked, so forward function to Console
     */
    public void actionPerformed(ActionEvent e) {
        String action = e.getActionCommand();
        if (action.equals(OPERATION_READ)) {
            console.setAction(Console.CONSOLE_READ);
        } else if (action.equals(OPERATION_NORMAL)) {
            console.setAction(Console.CONSOLE_NORMAL);
        } else if (action.equals(OPERATION_OBEY)) {
            console.setAction(Console.CONSOLE_OBEY);
        }    
    }
}
