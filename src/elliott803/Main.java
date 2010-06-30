/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2009
 */
package elliott803;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import elliott803.machine.Computer;
import elliott803.machine.Dump;
import elliott803.utils.Args;
import elliott803.view.ComputerView;
import elliott803.view.ViewDefinition;

/**
 * This is the main entry to the Elliott 803 simulator, it will start the GUI
 * view of computer and allow it to be operated.
 *
 * Usage:
 *   Main [options] [machine]
 *
 * where:
 *   machine: a previously saved machine definition
 *
 * options:
 *   -look lookAndFeel: the Java UI look-and-feel (defaults to system look and feel)
 *
 * @author Baldwin
 */
public class Main implements Runnable {

    public static void main(String[] args) throws Exception {
        // Handle parameters
        Map<String,String> options = Args.optionMap();
        options.put("look", "lookAndFeel");
        Args parms = new Args("View", "[machine]", args, options);

        // Set the Swing look and feel
        setLookAndFeel(parms.getOption("look"));

        // Get machine image to restore
        File imageFile = parms.getInputFile(1);

        // Create a new 803 simulation and view and start the simulator thread
        Computer computer = new Computer();
        ComputerView view = new ComputerView(computer);
        computer.start();
        
        // Restore saved state
        ViewDefinition viewdef = new ViewDefinition();
        if (imageFile != null) {
            FileInputStream image = new FileInputStream(imageFile);
            
            // First object in the saved image is a full system dump.  For the 
            // time being we just restore the store contents from the dump.
            Dump dump = Dump.readDump(image);
            computer.core.restore(dump);
            
            // Second object is an optional ViewDefinition containing saved window
            // sizes and positions.
            viewdef = ViewDefinition.readViewDef(image);
 
            image.close();
        }

        // Fire up the GUI
        Main gui = new Main(computer, view, viewdef);
        SwingUtilities.invokeLater(gui);
    }

    // Try to set the Swing look and feel
    private static void setLookAndFeel(String look) throws Exception {
        String lafClass = null;

        // Look for an exact match first, then a likely match
        if (look != null) {
            if (look.equals("LIST")) {
                for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels())
                    System.err.println("look: " + info.getName());
            }
            
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if (info.getName().equals(look)) {
                    lafClass = info.getClassName();
                    break;
                }
            }
            if (lafClass == null) {
                look = look.toUpperCase();
                for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if (info.getName().toUpperCase().contains(look)) {
                        lafClass = info.getClassName();
                        break;
                    }
                }
            }
        }

        // No match, so use the system default
        if (lafClass == null) {
            lafClass = UIManager.getSystemLookAndFeelClassName();
        }

        UIManager.setLookAndFeel(lafClass);
        JFrame.setDefaultLookAndFeelDecorated(true);
    }


    /*
     * Main code needs to run on the Swing dispatch thread
     */
    JFrame frame;
    ComputerView computerView;

    public Main(Computer computer, ComputerView view, ViewDefinition def) {
        computerView = view;
        
        Toolkit tk = Toolkit.getDefaultToolkit();
        Image icon = tk.createImage(getClass().getResource("icon/803-32.png"));

        frame = new JFrame(computer.name + " Simulation (v" + computer.version + ")");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setIconImage(icon);
        frame.setContentPane(view);

        // Layout the contents of the frame according to the view definition and
        // set the frame size and position
        view.layout(def);
        if (def.empty) {
            Dimension screen = tk.getScreenSize();
            frame.setLocation((screen.width-def.viewWidth)/2, (screen.height-def.viewHeight)/2);
        }
    }

    public void run() {
        frame.pack();
        frame.setVisible(true);
    }
}
