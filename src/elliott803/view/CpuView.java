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
import javax.swing.JCheckBox;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import elliott803.hardware.CPU;
import elliott803.view.component.DeviceLight;
import elliott803.view.component.DisplayAddr;
import elliott803.view.component.DisplayLight;
import elliott803.view.component.DisplayWord;

/**
 * A visual representation of the CPU state.
 *
 * Shows the contents of the various CPU registers
 */
public class CpuView extends JInternalFrame implements ActionListener {
    private static final long serialVersionUID = 1L;

    static final String CPU_DUMP = "Dump";
    static final String CPU_TRACE = "Trace";

    CPU cpu;

    DisplayWord acc;
    DisplayWord ar;
    DisplayWord br;
    DisplayAddr scr;
    DisplayWord ir;
    DeviceLight overflow;
    DeviceLight fpOverflow;

    public CpuView(CPU cpu) {
        super("CPU", false, false, false, true);
        this.cpu = cpu;

        acc = new DisplayWord("ACC", DisplayWord.Type.OCTAL);
        ar = new DisplayWord("AR", DisplayWord.Type.OCTAL);
        br = new DisplayWord("B", DisplayWord.Type.OCTAL);
        scr = new DisplayAddr();
        ir = new DisplayWord(DisplayWord.Type.INSTRUCTION);
        overflow = new DeviceLight("Overflow", DisplayLight.CYAN);
        fpOverflow = new DeviceLight("FP Oveflow", DisplayLight.CYAN);

        JPanel p0 = new JPanel();
        p0.setLayout(new BoxLayout(p0, BoxLayout.X_AXIS));
        p0.setAlignmentX(LEFT_ALIGNMENT);
        p0.setBorder(BorderFactory.createTitledBorder("Regsiters"));
        JPanel p1 = new JPanel();
        p1.setLayout(new BoxLayout(p1, BoxLayout.Y_AXIS));
        p1.setAlignmentX(LEFT_ALIGNMENT);
        p1.add(acc);
        p1.add(ar);
        p1.add(br);
        p0.add(p1);
        p0.add(Box.createHorizontalGlue());

        JPanel p2 = new JPanel();
        p2.setLayout(new BoxLayout(p2, BoxLayout.X_AXIS));
        p2.setBorder(BorderFactory.createTitledBorder("State"));
        p2.setAlignmentX(LEFT_ALIGNMENT);
        overflow.setAlignmentX(LEFT_ALIGNMENT);
        fpOverflow.setAlignmentX(RIGHT_ALIGNMENT);
        p2.add(overflow);
        p2.add(Box.createHorizontalGlue());
        p2.add(fpOverflow);

        JPanel p3 = new JPanel();
        p3.setLayout(new BoxLayout(p3, BoxLayout.X_AXIS));
        p3.setAlignmentX(LEFT_ALIGNMENT);
        p3.setBorder(BorderFactory.createTitledBorder("Instruction"));
        p3.add(scr);
        p3.add(Box.createHorizontalStrut(5));
        p3.add(ir);
        p3.add(Box.createHorizontalGlue());

        JPanel p4 = new JPanel();
        p4.setLayout(new BoxLayout(p4, BoxLayout.X_AXIS));
        p4.setBorder(BorderFactory.createTitledBorder("Debug"));
        p4.setAlignmentX(LEFT_ALIGNMENT);
        JRadioButton db = new JRadioButton(CPU_DUMP);
        db.addActionListener(this);
        p4.add(db);
        p4.add(Box.createHorizontalGlue());
        JCheckBox tb = new JCheckBox(CPU_TRACE);
        tb.addActionListener(this);
        p4.add(tb);

        if (cpu != null)
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
        if (e.getActionCommand().equals(CPU_DUMP)) {
            cpu.computer.dump();
            ((JRadioButton)e.getSource()).setSelected(false);
        } else if (e.getActionCommand().equals(CPU_TRACE)) {
            if (((JCheckBox)e.getSource()).isSelected())
                cpu.computer.traceStart();
            else
                cpu.computer.traceStop();
        }
    }

    /*
     * GUI Visualisation
     */

    public void updateRegisters(long a, long x, long b, int pc, long instr) {
        acc.setValue(a);
        ar.setValue(x);
        br.setValue(b);
        scr.setValue(pc);
        ir.setValue(instr);
    }

    public void updateFlags(boolean over, boolean fpOver) {
        overflow.setValue(over);
        fpOverflow.setValue(fpOver);
    }
}
