/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2012,2013
 */
package elliott803.view;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import elliott803.machine.Computer;
import elliott803.view.component.DisplaySpeed;

/**
 * The simulation control panel.
 * 
 * This is used to implement simulator control functions that are not 
 * part of any of the real hardware devices.
 */
public class ControlView extends JInternalFrame implements ActionListener {
    private static final long serialVersionUID = 1L;
    
    Computer computer;
    
    Timer speedTimer;
    DisplaySpeed speed;
    JCheckBox realTime;
    
    public ControlView(Computer computer, ComputerView computerView) {
        super("Simulation Control", false, false, false, true);
        this.computer = computer;
        
        // Load/Save of machine image
        JPanel ip = new JPanel();
        ip.setLayout(new BoxLayout(ip, BoxLayout.X_AXIS));
        ip.setBorder(BorderFactory.createTitledBorder("Machine Image"));
        JButton lb = new JButton(ComputerView.IMAGE_LOAD);
        lb.addActionListener(computerView);
        JButton sb = new JButton(ComputerView.IMAGE_SAVE);
        sb.addActionListener(computerView);        
        ip.add(Box.createHorizontalStrut(5));
        ip.add(lb);
        ip.add(Box.createHorizontalStrut(10));
        ip.add(sb);
        ip.add(Box.createHorizontalStrut(5));
        
        // CPU Speed
        JPanel sp = new JPanel();
        sp.setLayout(new BoxLayout(sp, BoxLayout.X_AXIS));
        sp.setBorder(BorderFactory.createTitledBorder("CPU Speed"));
        speed = new DisplaySpeed(1.0f);
        realTime = new JCheckBox("Real time", true);
        realTime.addActionListener(this);
        sp.add(Box.createHorizontalStrut(5));
        sp.add(speed);
        sp.add(Box.createHorizontalStrut(10));
        sp.add(Box.createHorizontalGlue());
        sp.add(realTime);
        
        // Timer to update CPU actual speed
        speedTimer = new Timer(2500, this);
        speedTimer.setInitialDelay(5000);
        speedTimer.setRepeats(true);
        speedTimer.start();
        
        Container content = getContentPane();
        content.add(ip, BorderLayout.EAST);
        content.add(sp, BorderLayout.WEST);
        pack();
        setVisible(true);
    }
    
    /*
     * Button actions
     */

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == speedTimer) {
            float sp = computer.cpu.getSpeed();
            if (sp > 0.1)
                speed.setValue(sp);
        } else if (e.getSource() == realTime) {
            computer.setRealTime(realTime.isSelected());
        }
    }
}
