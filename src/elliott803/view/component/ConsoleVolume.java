/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2013
 */
package elliott803.view.component;

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
    
    public JSlider slider;
    
    public ConsoleVolume(Console console) {
        this.console = console;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setAlignmentX(RIGHT_ALIGNMENT);
        setAlignmentY(CENTER_ALIGNMENT);
        JLabel title = new JLabel("Volume");
        title.setAlignmentX(CENTER_ALIGNMENT);
        slider = new JSlider(0, 100, console.getVolume());
        slider.setAlignmentX(CENTER_ALIGNMENT);
        slider.setMajorTickSpacing(10);
        slider.setPaintTicks(true);
        slider.setFocusable(false);
        slider.addChangeListener(this);
        add(Box.createVerticalGlue());
        add(title);
        add(slider);
    }

    /*
     * Slider state changed
     */
    public void stateChanged(ChangeEvent e) {
        JSlider slider = (JSlider)e.getSource();
        if (!slider.getValueIsAdjusting()) {
            int volume = slider.getValue();
            console.setVolume(volume);
        }
    }
}
