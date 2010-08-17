/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2009,2010
 */
package elliott803;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.filechooser.FileFilter;

import elliott803.machine.Computer;
import elliott803.utils.Args;
import elliott803.view.ComputerView;
import elliott803.view.MachineImage;

/**
 * This is the main entry to the Elliott 803 simulator, it will start the GUI
 * view of computer and allow it to be operated.
 *
 * Usage:
 *   Main [options] [machine]
 *
 * where:
 *   machine: a previously saved machine image
 *
 * options:
 *   -look lookAndFeel: the Java UI look-and-feel (defaults to system look and feel)
 *   -load: prompt to load machine image on startup
 *   -save: prompt to save machine image on exit
 *
 * @author Baldwin
 */
public class Main implements Runnable, WindowListener {

    public static void main(String[] args) throws Exception {
        // Handle parameters
        Map<String,String> options = Args.optionMap();
        options.put("look", "lookAndFeel");
        options.put("save", null);
        options.put("load", null);
        Args parms = new Args("View", "[machine]", args, options);

        // Set the Swing look and feel
        setLookAndFeel(parms.getOption("look"));

        // Get machine image to restore and save/load prompt flags
        boolean saveImage = parms.getFlag("save");
        boolean loadImage = parms.getFlag("load");
        File imageFile = parms.getInputFile(1);
        MachineImage image = null; 
        if (imageFile != null)
            image = MachineImage.readImage(imageFile);

        // Create a new 803 simulation and view and start the simulator thread
        Computer computer = new Computer();
        ComputerView view = new ComputerView(computer);
        computer.start();
        
        // Fire up the GUI
        Main gui = new Main(computer, view, image, loadImage, saveImage);
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
    JFileChooser selectImage;
    Computer computer;
    ComputerView computerView;
    boolean saveImage, loadImage;
    
    public Main(Computer computer, ComputerView view, MachineImage image, boolean load, boolean save) {
        this.computer = computer;
        this.computerView = view;
        
        Toolkit tk = Toolkit.getDefaultToolkit();
        Image icon = tk.createImage(getClass().getResource("icon/803-32.png"));

        frame = new JFrame(computer.name + " Simulation (v" + computer.version + ")");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setIconImage(icon);
        frame.setContentPane(view);
        frame.addWindowListener(this);
        
        // Restore previous machine image or create a default layout
        saveImage = save;
        loadImage = load;
        if (image != null) {
            image.apply(computer, view);
        } else {    
            view.defaultLayout();
            Dimension screen = tk.getScreenSize();
            Dimension window = view.getPreferredSize();
            Insets insets = frame.getInsets();
            int fw = window.width + insets.left + insets.right;
            int fh = window.height + insets.top + insets.bottom;
            frame.setLocation((screen.width - fw)/2, (screen.height - fh)/2);
        } 
        
        // Create image selection dialog 
        selectImage = new JFileChooser(new File("."));
        selectImage.setDialogTitle("Elliott 803 Machine Image");
        selectImage.setFileFilter(new FileFilter() {
            public boolean accept(File f) {
                return (f.isDirectory() || f.getName().endsWith(".803"));
            }
            public String getDescription() {
                return "Elliott 803 Machine Images";
            }
        });
    }
 
    public void run() {
        frame.pack();
        frame.setVisible(true);
    }
    
    /*
     * Implement WindowListener to detect window closing and offer save option
     */

    public void windowOpened(WindowEvent e) {
        if (loadImage) {
            if (selectImage.showOpenDialog(computerView) == JFileChooser.APPROVE_OPTION) {
                File file = selectImage.getSelectedFile();
                MachineImage image = MachineImage.readImage(file);
                image.apply(computer, computerView);
            }
        }
    }
    
    public void windowClosing(WindowEvent e) {
        if (saveImage) {
            if (selectImage.showSaveDialog(computerView) == JFileChooser.APPROVE_OPTION) {
                File file = selectImage.getSelectedFile();
                if (!file.getName().contains("."))
                    file = new File(file.getPath() + ".803");
                MachineImage image = new MachineImage(computer, computerView);
                image.write(file);
            }
        }
    }
    
    public void windowClosed(WindowEvent e) {
    }

    public void windowIconified(WindowEvent e) {
    }

    public void windowDeiconified(WindowEvent e) {
    }

    public void windowActivated(WindowEvent e) {
    }

    public void windowDeactivated(WindowEvent e) {
    }
}
