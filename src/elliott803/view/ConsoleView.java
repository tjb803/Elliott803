/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2009, 2012
 */
package elliott803.view;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;

import elliott803.hardware.Console;
import elliott803.view.component.ConsoleButton;
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
public class ConsoleView extends JInternalFrame implements ActionListener, FocusListener {
    private static final long serialVersionUID = 1L;

    static final String CONSOLE_CLEAR = "Clear Store";
    static final String CONSOLE_RESET = "Reset";
    static final String CONSOLE_OPERATE = "Operate";
    static final String CONSOLE_MANUAL = "Manual Data";

    static final String[] FN_NAMES = { "4", "2", "1", "4", "2", "1" };
    static final String[] ADDR_NAMES = { "4096", "2048", "1024", "512", "256", "128", "64", "32", "16", "8", "4", "2", "1", "B" };

    Console console;

    ConsoleOperation function;
    ConsoleLight step, busy, overflow, fpOverflow;
    ConsoleButton manualData;
    JButton operate;
    DisplayWord wordgen;

    public ConsoleView(Console console) {
        super("Operator Console", false, false, false, true);
        this.console = console;
        setFocusable(true);
        addFocusListener(this);

        JPanel wg = new JPanel();
        wg.setLayout(new BoxLayout(wg, BoxLayout.Y_AXIS));
        wg.add(new ConsoleButtons("Function 1", FN_NAMES, 6, 39, console));
        wg.add(Box.createVerticalStrut(5));
        wg.add(new ConsoleButtons("Address 1", ADDR_NAMES, 14, 33, console));
        wg.add(Box.createVerticalStrut(5));
        wg.add(new ConsoleButtons("Function 2", FN_NAMES, 6, 19, console));
        wg.add(Box.createVerticalStrut(5));
        wg.add(new ConsoleButtons("Address 2", ADDR_NAMES, 13, 13, console));
        wg.add(Box.createVerticalGlue());

        JPanel wv = new JPanel();
        wv.setLayout(new BoxLayout(wv, BoxLayout.X_AXIS));
        wv.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
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
        
        manualData = new ConsoleButton(CONSOLE_MANUAL, ConsoleButton.BLACK, false);
        manualData.setAlignmentX(CENTER_ALIGNMENT);
        manualData.addActionListener(this);
        controls.add(manualData);
        controls.add(Box.createVerticalStrut(5));
        
        JButton rb = new JButton(CONSOLE_RESET);
        rb.setAlignmentX(CENTER_ALIGNMENT);
        rb.addActionListener(this);
        controls.add(rb);
        controls.add(Box.createVerticalStrut(10));

        function = new ConsoleOperation(console);
        controls.add(function);
        controls.add(Box.createVerticalStrut(5));

        operate = new JButton(CONSOLE_OPERATE);
        operate.setMaximumSize(cb.getMaximumSize());
        operate.setPreferredSize(cb.getPreferredSize());
        operate.setAlignmentX(CENTER_ALIGNMENT);
        operate.addActionListener(this);
        controls.add(operate);
        controls.add(Box.createVerticalStrut(5));
        
        console.setView(this);

        Container content = getContentPane();
        content.add(wg, BorderLayout.WEST);
        content.add(controls, BorderLayout.EAST);
        pack();
        setVisible(true);
    }

    /*
     * Console operation button actions.
     */

    public void actionPerformed(ActionEvent e) {
        String action = e.getActionCommand();
        if (action.equals(CONSOLE_CLEAR)) {
            console.clear();
        } else if (action.equals(CONSOLE_RESET)) {
            console.reset();
        } else if (action.equals(CONSOLE_OPERATE)) {
            console.operate();
        } else if (action.equals(CONSOLE_MANUAL)) {
            console.setManualData(manualData.isSelected());
        }
    }

    public void focusGained(FocusEvent e) {
        getRootPane().setDefaultButton(operate);
    }

    public void focusLost(FocusEvent e) {
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
