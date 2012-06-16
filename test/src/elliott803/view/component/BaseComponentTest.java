/**
 * Elliott Model 803B Simulator
 * 
 * (C) Copyright Tim Baldwin 2009
 */
package elliott803.view.component;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;

import javax.swing.JFrame;

import junit.framework.TestCase;

/**
 * General Swing test frame
 * 
 * @author Baldwin
 */
public abstract class BaseComponentTest extends TestCase {
    
    protected abstract void setupTest();
    
    protected void setUp() {
        // Create a simple frame for base component tests
        testFrame = new JFrame("Component View Tester");    
        testFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        testFrame.setSize(new Dimension(640, 480));
        testFrame.setLocation(new Point(100, 100));
        
        testView = testFrame.getContentPane();
        setupTest();
        
        testFrame.pack();
        testFrame.setVisible(true);
    }
    
    protected JFrame testFrame;
    protected Container testView;
}
