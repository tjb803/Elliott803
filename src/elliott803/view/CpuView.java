/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2009,2010
 */
package elliott803.view;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.Timer;

import elliott803.hardware.CPU;
import elliott803.view.component.DeviceLight;
import elliott803.view.component.DisplayAddress;
import elliott803.view.component.DisplayInstruction;
import elliott803.view.component.DisplayLight;
import elliott803.view.component.DisplayWord;

/**
 * A visual representation of the CPU state.
 *
 * Shows the contents of the various CPU registers
 */
public class CpuView extends JInternalFrame implements ActionListener {
    private static final long serialVersionUID = 1L;

    CPU cpu;

    DisplayWord acc;
    DisplayWord ar;
    DisplayInstruction ir;
    DisplayAddress scr;
    DisplayWord iw;
    DeviceLight overflow;
    DeviceLight fpOverflow;
    JRadioButton dump;
    JCheckBox trace;
    Timer dumpTimer;

    public CpuView(CPU cpu) {
        super("CPU", false, false, false, true);
        this.cpu = cpu;

        acc = new DisplayWord("ACC", DisplayWord.Type.OCTAL);
        ar = new DisplayWord("AR", DisplayWord.Type.OCTAL);
        ir = new DisplayInstruction("IR", 6);
        scr = new DisplayAddress();
        iw = new DisplayWord(DisplayWord.Type.INSTRUCTION);
        overflow = new DeviceLight("Overflow", DisplayLight.CYAN);
        fpOverflow = new DeviceLight("FP Overflow", DisplayLight.CYAN);

        JPanel p0 = new JPanel();
        p0.setLayout(new BoxLayout(p0, BoxLayout.X_AXIS));
        p0.setAlignmentX(LEFT_ALIGNMENT);
        p0.setBorder(BorderFactory.createTitledBorder("Registers"));
        JPanel p1 = new JPanel();
        p1.setLayout(new BoxLayout(p1, BoxLayout.Y_AXIS));
        p1.setAlignmentX(LEFT_ALIGNMENT);
        p1.add(acc);
        p1.add(ar);
        p1.add(ir);
        p0.add(p1);
        p0.add(Box.createHorizontalGlue());

        JPanel p2 = new JPanel();
        p2.setLayout(new BoxLayout(p2, BoxLayout.X_AXIS));
        p2.setBorder(BorderFactory.createTitledBorder("State"));
        p2.setAlignmentX(LEFT_ALIGNMENT);
        overflow.setAlignmentX(LEFT_ALIGNMENT);
        fpOverflow.setAlignmentX(RIGHT_ALIGNMENT);
        p2.add(overflow);
        p2.add(Box.createHorizontalStrut(10));
        p2.add(Box.createHorizontalGlue());
        p2.add(fpOverflow);

        JPanel p3 = new JPanel();
        p3.setLayout(new BoxLayout(p3, BoxLayout.X_AXIS));
        p3.setAlignmentX(LEFT_ALIGNMENT);
        p3.setBorder(BorderFactory.createTitledBorder("Instruction"));
        p3.add(scr);
        p3.add(Box.createHorizontalStrut(5));
        p3.add(iw);
        p3.add(Box.createHorizontalGlue());

        JPanel p4 = new JPanel();
        p4.setLayout(new BoxLayout(p4, BoxLayout.X_AXIS));
        p4.setBorder(BorderFactory.createTitledBorder("Debug"));
        p4.setAlignmentX(LEFT_ALIGNMENT);
        dump = new JRadioButton("Dump");
        dump.addActionListener(this);
        p4.add(dump);
        p4.add(Box.createHorizontalGlue());
        trace = new JCheckBox("Trace");
        trace.addActionListener(this);
        p4.add(trace);
        
        // dumpTimer handles 'unclicking' the dump button
        dumpTimer = new Timer(250, this);
        dumpTimer.setRepeats(false);

        cpu.setView(this);

        Container content = getContentPane();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.add(p0);
        content.add(p2);
        content.add(p3);
        content.add(Box.createVerticalStrut(10));
        content.add(p4);
        pack();
        setVisible(true);
    }

    /*
     * Button actions
     */

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == dumpTimer) {
            dump.setSelected(false);
        } else if (e.getSource() == dump) {
            if (dump.isSelected()) { 
                cpu.computer.dump();
                dumpTimer.start();
            }    
        } else if (e.getSource() == trace) {
            if (trace.isSelected())
                cpu.computer.traceStart();
            else
                cpu.computer.traceStop();
        }
    }

    /*
     * GUI Visualisation
     */

    // These updates slow down processing a lot, so ensure they do nothing if the
    // view window is not displayed.
    
    public void updateRegisters(long a, long x, int i, int pc, long instr) {
        if (!isIcon()) {
            acc.setValue(a);
            ar.setValue(x);
            ir.setValue(i);
            scr.setValue(pc);
            iw.setValue(instr);
        }
    }

    public void updateFlags(boolean over, boolean fpOver) {
        if (!isIcon()) {
            overflow.setValue(over);
            fpOverflow.setValue(fpOver);
        }
    }
    
    public void updateTrace(boolean enabled) {
        if (!isIcon()) {
            trace.setSelected(enabled);
        }
    }
}
