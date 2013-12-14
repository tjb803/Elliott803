/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2013
 */
package elliott803.view.component;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

/**
 * Display and operate the set of console indicator lights.
 *
 * @author Baldwin
 */
public class ConsoleLights extends JPanel {
    private static final long serialVersionUID = 1L;
    
    ConsoleLight step, blockTr, busy, overflow, fpOverflow;
    
    public ConsoleLights() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setAlignmentY(CENTER_ALIGNMENT);
        
        blockTr = new ConsoleLight("Block Transfer");
        step = new ConsoleLight("Step by Step");
        busy = new ConsoleLight("Busy");
        overflow = new ConsoleLight("Overflow");
        fpOverflow = new ConsoleLight("Floating Point/Overflow");
        
        add(Box.createVerticalGlue());
        add(new ConsoleLight("Parity"));            // Never used
        add(Box.createVerticalStrut(10));
        add(blockTr);
        add(Box.createVerticalStrut(10));
        add(busy);
        add(Box.createVerticalStrut(10));
        add(fpOverflow);
        add(Box.createVerticalStrut(10));
        add(step);
        add(Box.createVerticalStrut(10));
        add(overflow);
        add(Box.createVerticalGlue());
    }
    
    /*
     * Set lights on or off
     */
    public void setStep(boolean on) {
        step.setValue(on);
    }
    
    public void setBlockTr(boolean on) {
        blockTr.setValue(on);
    }
    
    public void setBusy(boolean on) {
        busy.setValue(on);
    }
    
    public void setOverflow(boolean on) {
        overflow.setValue(on);
    }
    
    public void setFpOverflow(boolean on) {
        fpOverflow.setValue(on);
    }
}
