/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2009,2013
 */
package elliott803.view;

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
import elliott803.view.component.ConsoleButtons;
import elliott803.view.component.ConsoleControl;
import elliott803.view.component.ConsoleLights;
import elliott803.view.component.ConsoleOperation;
import elliott803.view.component.ConsoleVolume;
import elliott803.view.component.DisplayWord;
import elliott803.view.component.DisplayWord.Type;

/**
 * A visual representation of the operator console.
 *
 * This has all the buttons for the word generator and various other buttons for
 * operating the computer.  It is intended to look and work roughly like the real
 * console, but it is not meant to be a completely accurate simulation.
 * 
 * It also contains the loudspeaker that produces sounds by firing a pulse when
 * certain instructions are executed.
 */
public class ConsoleView extends JInternalFrame implements ActionListener, FocusListener {
    private static final long serialVersionUID = 1L;

    static final String CONSOLE_OPERATE = "Operate";
    static final String[] FN_NAMES = { "4", "2", "1", "4", "2", "1" };
    static final String[] ADDR_NAMES = { "4096", "2048", "1024", "512", "256", "128", "64", "32", "16", "8", "4", "2", "1", "B" };

    Console console;
    Loudspeaker speaker;
    
    ConsoleLights lights;
    ConsoleVolume volume;
    JButton operate;
    DisplayWord wordgen;

    public ConsoleView(Console console) {
        super("Operator Console", false, false, false, true);
        this.console = console;
        setFocusable(true);
        addFocusListener(this);
        
        // Create the loudspeaker
        speaker = new Loudspeaker();
        speaker.setVolume(console.getVolume());
        volume = new ConsoleVolume(console);

        // Word generator 
        JPanel wg = new JPanel();
        wg.setLayout(new BoxLayout(wg, BoxLayout.Y_AXIS));
        wg.add(new ConsoleButtons("Function 1", FN_NAMES, 6, 39, console));
        wg.add(Box.createVerticalStrut(10));
        wg.add(new ConsoleButtons("Address 1", ADDR_NAMES, 14, 33, console));
        wg.add(Box.createVerticalStrut(10));
        wg.add(new ConsoleButtons("Function 2", FN_NAMES, 6, 19, console));
        wg.add(Box.createVerticalStrut(10));
        wg.add(new ConsoleButtons("Address 2", ADDR_NAMES, 13, 13, console));
        wg.add(Box.createVerticalStrut(20));
        wordgen = new DisplayWord("Word", Type.INSTRUCTION);
        wordgen.setAlignmentX(LEFT_ALIGNMENT);
        wordgen.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        wg.add(wordgen);

        // Indicator lights
        lights = new ConsoleLights();
        
        // Control buttons etc
        JPanel controls = new JPanel();
        controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));
        controls.add(Box.createVerticalGlue());
        controls.add(volume);
        controls.add(Box.createVerticalGlue());
        controls.add(new ConsoleControl(console));
        controls.add(Box.createVerticalStrut(10));
        controls.add(new ConsoleOperation(console));
        controls.add(Box.createVerticalStrut(10));

        // Operate bar
        operate = new JButton(CONSOLE_OPERATE);
        operate.setAlignmentX(CENTER_ALIGNMENT);
        operate.addActionListener(this);
        controls.add(operate);
        controls.add(Box.createVerticalStrut(10));

        console.setView(this);

        Container content = getContentPane();
        content.setLayout(new BoxLayout(content, BoxLayout.X_AXIS));
        content.add(wg);
        content.add(Box.createHorizontalStrut(5));
        content.add(lights);
        content.add(Box.createHorizontalStrut(5));
        content.add(controls);
        pack();
        setVisible(true);
    }

    /*
     * Console operation button actions.
     */

    public void actionPerformed(ActionEvent e) {
        String action = e.getActionCommand();
        if (action.equals(CONSOLE_OPERATE)) {
            console.operate();
        }
    }

    public void focusGained(FocusEvent e) {
        getRootPane().setDefaultButton(operate);
    }

    public void focusLost(FocusEvent e) {
    }

    /*
     * GUI visualisation (and auralisation!)
     */

    public void updateWordGen(long value) {
        wordgen.setValue(value);
    }

    public void updateLights(boolean isStep, boolean isBusy, boolean isOverflow, boolean isFpOver) {
        lights.setStep(isStep);
        lights.setBusy(isBusy);
        lights.setOverflow(isOverflow);
        lights.setFpOverflow(isFpOver);
    }
    
    public void updateVolume(boolean on, int vol) {
        speaker.setVolume(on ? vol : 0);
        volume.setEnabled(on);
    }
    
    public void soundSpeaker(boolean click, int count) {
        speaker.sound(click, count);
    }
}
