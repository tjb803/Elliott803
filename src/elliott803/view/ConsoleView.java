/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2009
 */
package elliott803.view;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import elliott803.hardware.Console;
import elliott803.machine.Computer;
import elliott803.view.component.ConsoleButtons;
import elliott803.view.component.ConsoleLight;
import elliott803.view.component.ConsoleOperation;
import elliott803.view.component.DisplayWord;
import elliott803.view.component.DisplayWord.Type;

/**
 * A visual representation of the operator console.
 *
 * This has all the buttons for the word generator and various other buttons for
 * operating the computer.  It is intended to look and work roughly like the real
 * console, but it is not meant to be a completely accurate simulation.
 */
public class ConsoleView extends JInternalFrame implements ActionListener {
    private static final long serialVersionUID = 1L;

    static final String CONSOLE_CLEAR = "Clear Store";
    static final String CONSOLE_RESET = "Reset";
    static final String CONSOLE_OPERATE = "Operate";

    static final String[] fnNames = { "4", "2", "1", "4", "2", "1" };
    static final String[] adNames = { "4096", "2048", "1024", "512", "256", "128", "64", "32", "16", "8", "4", "2", "1", "B" };

    Console console;

    ConsoleOperation function;
    ConsoleLight step, busy, overflow, fpOverflow;
    DisplayWord wordgen;

    public ConsoleView(Console console) {
        super("Operator Console", false, false, false, true);
        this.console = console;

        JPanel wg = new JPanel();
        wg.setLayout(new BoxLayout(wg, BoxLayout.Y_AXIS));
        wg.add(new ConsoleButtons("Function 1", fnNames, 6, 39, this));
        wg.add(Box.createVerticalStrut(5));
        wg.add(new ConsoleButtons("Address 1", adNames, 14, 33, this));
        wg.add(Box.createVerticalStrut(5));
        wg.add(new ConsoleButtons("Function 2", fnNames, 6, 19, this));
        wg.add(Box.createVerticalStrut(5));
        wg.add(new ConsoleButtons("Address 2", adNames, 13, 13, this));
        wg.add(Box.createVerticalGlue());

        JPanel wv = new JPanel();
        wv.setLayout(new BoxLayout(wv, BoxLayout.X_AXIS));
        wv.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        wv.setAlignmentX(LEFT_ALIGNMENT);
        wordgen = new DisplayWord("Word", Type.INSTRUCTION);
        wv.add(wordgen);
        wg.add(wv);

        JPanel lights = new JPanel();
        lights.setLayout(new BoxLayout(lights, BoxLayout.Y_AXIS));
        step = new ConsoleLight("Step by Step", null);
        busy = new ConsoleLight("Busy", null);
        overflow = new ConsoleLight("Overflow", null);
        fpOverflow = new ConsoleLight("Floating Point", "Overflow");
        lights.add(Box.createVerticalGlue());
        lights.add(busy);
        lights.add(Box.createVerticalStrut(10));
        lights.add(fpOverflow);
        lights.add(Box.createVerticalStrut(10));
        lights.add(step);
        lights.add(Box.createVerticalStrut(10));
        lights.add(overflow);
        lights.add(Box.createVerticalGlue());
     
        JPanel controls = new JPanel();
        controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));
        controls.add(Box.createVerticalStrut(5));
        JButton cb = new JButton(CONSOLE_CLEAR);
        cb.setAlignmentX(CENTER_ALIGNMENT);
        cb.addActionListener(this);
        controls.add(cb);
        controls.add(Box.createVerticalStrut(15));
        controls.add(lights);
        controls.add(Box.createVerticalStrut(15));
        JButton rb = new JButton(CONSOLE_RESET);
        rb.setMaximumSize(cb.getMaximumSize());
        rb.setPreferredSize(cb.getPreferredSize());
        rb.setAlignmentX(CENTER_ALIGNMENT);
        rb.addActionListener(this);
        controls.add(rb);
        controls.add(Box.createVerticalStrut(5));

        function = new ConsoleOperation();
        controls.add(function);
        controls.add(Box.createVerticalStrut(5));

        JButton ob = new JButton(CONSOLE_OPERATE);
        ob.setMaximumSize(cb.getMaximumSize());
        ob.setPreferredSize(cb.getPreferredSize());
        ob.setAlignmentX(CENTER_ALIGNMENT);
        ob.addActionListener(this);
        controls.add(ob);
        controls.add(Box.createVerticalStrut(5));
        
        console.setView(this);

        Container content = getContentPane();
        content.setLayout(new BoxLayout(content, BoxLayout.X_AXIS));
        content.add(wg);
        content.add(controls);
        pack();
        setVisible(true);
    }

    /*
     * Console button actions.
     */

    public void actionPerformed(ActionEvent e) {
        String action = e.getActionCommand();
        if (action.equals(CONSOLE_CLEAR)) {
            console.computer.run(Computer.ACT_CLEAR);
        } else if (action.equals(CONSOLE_RESET)) {
            console.computer.cpu.reset();
        } else if (action.equals(CONSOLE_OPERATE)) {
            // "Operate" bar pressed.  Need to use the methods on Computer to perform
            // these actions (rather than using the Console) as we need to ensure the
            // long running simulations don't happen on the event dispatch thread.
            String op = function.getOperation();
            if (op.equals(ConsoleOperation.OPERATION_READ))
                console.computer.setInstruction(console.readWordGen());
            else if (op.equals(ConsoleOperation.OPERATION_OBEY))
                console.computer.run(Computer.ACT_STEP);
            else if (op.equals(ConsoleOperation.OPERATION_NORMAL))
                console.computer.run(Computer.ACT_RUN);
        } else {
            // Must be a word generator button
            int bit = Integer.parseInt(action);
            if (((JRadioButton)e.getSource()).isSelected())
                console.setWordGenBit(bit);
            else
                console.clearWordGenBit(bit);
        }
    }


    /*
     * GUI visualisation
     */

    public void updateWordGen(long value) {
        wordgen.setValue(value);
    }

    public void updateLights(boolean isStep, boolean isBusy, boolean isOverflow, boolean isFpOver) {
        step.setValue(isStep);
        busy.setValue(isBusy);
        overflow.setValue(isOverflow);
        fpOverflow.setValue(isFpOver);
    }
}
