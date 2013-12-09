/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2013
 */
package elliott803.view.component;

import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import elliott803.hardware.Console;

/**
 * Console speaker volume control
 * 
 * @author Baldwin
 */
public class ConsoleVolume extends JPanel implements ChangeListener {
    private static final long serialVersionUID = 1L;
    
    Console console;
    JSlider slider;
            
    public ConsoleVolume(Console console) {
        this.console = console;        
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setAlignmentX(CENTER_ALIGNMENT);
                
        DisplaySpeaker grill = new DisplaySpeaker();
        add(grill);
        add(Box.createVerticalStrut(5));
        
        slider = new JSlider(0, 100, console.getVolume());
        slider.addChangeListener(this);
        slider.setAlignmentX(CENTER_ALIGNMENT);
        slider.setMajorTickSpacing(20);
        slider.setPaintTicks(true);
        slider.setFocusable(false);
        Dimension size = new Dimension(grill.getPreferredSize().width, slider.getPreferredSize().height);
        slider.setMaximumSize(size);
        slider.setPreferredSize(size);
        add(slider);
        JLabel t = new JLabel("Volume");
        t.setAlignmentX(CENTER_ALIGNMENT);
        add(t);
    }
    
    public void setEnabled(boolean enabled) {
        slider.setEnabled(enabled);
    }
    
    /*
     * Slider state changed
     */
    public void stateChanged(ChangeEvent e) {
        JSlider slider = (JSlider)e.getSource();
        if (!slider.getValueIsAdjusting()) {
            console.setVolume(slider.getValue());
        }
    }
}
