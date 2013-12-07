/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2013
 */
package elliott803.view.component;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JRadioButton;
import javax.swing.UIManager;

/**
 * This is an attempt to make a JRadioButton behave more like a push button.
 *
 * @author Baldwin
 */
public class PushButton extends JRadioButton implements MouseListener {
    private static final long serialVersionUID = 1L;
    
    boolean selectOnMouse;
   
    public PushButton(boolean push) {
        this(null, push);
    }
    
    public PushButton(String text, boolean push) {
        super(text);
        if (push) {
            addMouseListener(this);
            // This is a hack but I can't find a better way to make the button 
            // paint the way I want.  In most look-and-feels we need to select
            // it when the mouse presses it so it paints as a 'selected' button, 
            // but this doesn't work in Motif where it need to paint in the 
            // default 'pressed' state instead.  Must be a better way to do this.
            selectOnMouse = !UIManager.getLookAndFeel().getID().equals("Motif");
        }   
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
        if (selectOnMouse)
            setSelected(true);
    }

    public void mouseReleased(MouseEvent e) {
        setSelected(false);
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }
}
