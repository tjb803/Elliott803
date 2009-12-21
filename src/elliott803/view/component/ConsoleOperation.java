/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2009
 */
package elliott803.view.component;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;

/**
 * Group of console buttons that define the "operation", i.e. Read/Normal/Obey
 *
 * @author Baldwin
 */
public class ConsoleOperation extends JPanel implements ActionListener {
    private static final long serialVersionUID = 1L;

    public static final String OPERATION_READ = "Read";
    public static final String OPERATION_NORMAL = "Normal";
    public static final String OPERATION_OBEY = "Obey";

    String action = OPERATION_READ;

    public ConsoleOperation() {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setAlignmentX(CENTER_ALIGNMENT);
        ConsoleButton rb = new ConsoleButton(OPERATION_READ, ConsoleButton.BLACK, true);
        ConsoleButton nb = new ConsoleButton(OPERATION_NORMAL, ConsoleButton.BLACK, false);
        ConsoleButton ob = new ConsoleButton(OPERATION_OBEY, ConsoleButton.BLACK, false);
        ButtonGroup group = new ButtonGroup();
        group.add(rb);  group.add(nb);  group.add(ob);
        rb.addActionListener(this);
        nb.addActionListener(this);
        ob.addActionListener(this);
        add(rb);
        add(nb);
        add(ob);
    }

    public String getOperation() {
        return action;
    }

    /*
     * Action button clicked, so save state
     */
    public void actionPerformed(ActionEvent e) {
        action = e.getActionCommand();
    }
}
