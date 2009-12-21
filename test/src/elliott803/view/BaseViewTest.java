/**
 * Elliott Model 803B Simulator
 * 
 * (C) Copyright Tim Baldwin 2009
 */
package elliott803.view;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;

import junit.framework.TestCase;
import elliott803.machine.Computer;

/**
 * General Swing test frame
 * 
 * @author Baldwin
 */
public abstract class BaseViewTest extends TestCase {
    
    protected void setUp() {
        // Create a multi-document frame for machine view tests
        testFrame = new JFrame("Machine View Tester");    
        testFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        testFrame.setSize(new Dimension(800, 600));
        testFrame.setLocation(new Point(100, 100));
        testFrame.setContentPane(new JDesktopPane());
        testFrame.getContentPane().setLayout(new FlowLayout());

        // Test Computer instance
        testComputer = new Computer(true);      
    }
    
    protected JFrame testFrame;
    protected Computer testComputer;
}
