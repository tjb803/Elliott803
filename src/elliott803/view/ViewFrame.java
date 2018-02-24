/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2015
 */
package elliott803.view;

import javax.swing.BorderFactory;
import javax.swing.JInternalFrame;

/**
 * Base class for the internal window elements/
 */
public class ViewFrame extends JInternalFrame {
    private static final long serialVersionUID = 1L;
    
    public ViewFrame(String title, boolean resize) {
        super(title, resize, false, resize, true);

        // Only affects Mac: removes attempts to draw shadows under the windows
        // which doesn't work properly on many OS X versions and/or JDK levels.
        if (ComputerView.isMac) {
            putClientProperty("JInternalFrame.frameType", "normal");
            setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(1,1,1,1), getBorder()));
        }    
    }
}
