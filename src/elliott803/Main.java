/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2009
 */
package elliott803;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import elliott803.machine.Computer;
import elliott803.utils.Args;
import elliott803.view.ComputerView;
import elliott803.view.ViewDefinition;

/**
 * This is the main entry to the Elliott 803 simulator, it will start the GUI
 * view of computer and allow it to be operated.
 *
 * Usage:
 *   View [options] [machine]
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

        // TODO: Load any saved view definition
        ViewDefinition definition = new ViewDefinition();

        // Create a new 803 simulation and view and start the simulator thread
        Computer computer = new Computer();
        ComputerView view = new ComputerView(computer);
        computer.start();

        // Fire up the GUI
        Main gui = new Main(computer, view, definition);
        SwingUtilities.invokeLater(gui);
    }

    // Try to set the Swing look and feel
    private static void setLookAndFeel(String look) throws Exception {
        String lafClass = null;

        // Look for an exact match first, then a likely match
        if (look != null) {
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

        frame = new JFrame(computer.name + " simulation (v" + computer.version + ")");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(view);

        // Layout the contents of the frame according to the view definition and
        // set the frame size and position
        view.layout(def);
        if (def.empty) {
            Toolkit tk = Toolkit.getDefaultToolkit();
            Dimension screen = tk.getScreenSize();
            frame.setLocation((screen.width-def.viewWidth)/2, (screen.height-def.viewHeight)/2);
        }
    }

    public void run() {
        frame.pack();
        frame.setVisible(true);
    }
}
