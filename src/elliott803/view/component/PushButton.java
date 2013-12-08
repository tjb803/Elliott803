/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2013
 */
package elliott803.view.component;

import java.awt.event.ActionEvent;

import javax.swing.JRadioButton;

/**
 * This is an attempt to make a JRadioButton behave more like a push button.
 *
 * @author Baldwin
 */
public class PushButton extends JRadioButton {
    private static final long serialVersionUID = 1L;

    public PushButton(boolean push) {
        this(null, push);
    }
    
    public PushButton(String text, boolean push) {
        super(text);
        if (push) {
            setModel(new PushButtonModel());
        }   
    }
    
    // Custom ButtonModel to simulate the push button function 
    static class PushButtonModel extends ToggleButtonModel {
        private static final long serialVersionUID = 1L;

        // Button becomes selected and performs its action whenever it is pressed.
        public void setPressed(boolean b) {
            setSelected(b);
            if (isSelected()) {
                fireActionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, getActionCommand()));
            }
        }
    }
}
