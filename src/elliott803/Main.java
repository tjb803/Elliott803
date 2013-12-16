/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2009,2013
 */
package elliott803;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.OceanTheme;

import elliott803.machine.Computer;
import elliott803.utils.Args;
import elliott803.view.ComputerView;
import elliott803.view.Loudspeaker;
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
 *   -volume volume: the initial volume (0 to 100, 0 means no sound)
 *   -debug: print diagnostic information
 *
 * @author Baldwin
 */
public class Main implements Runnable {

    public static void main(String[] args) throws Exception {
        // Handle parameters
        Args.Map options = Args.optionMap();
        options.put("look", "lookAndFeel");
        options.put("volume", "volume");
        options.put("sound", "sampleRate:bufferSize");
        options.put("debug");
        Args parms = new Args("elliott803.Main", "[machine]", args, options);

        // Global debug flag
        Computer.debug = parms.getFlag("debug");

        // Sound sample rate and buffer size, for experimentation
        String sound = parms.getOption("sound");
        if (sound != null) {
            String[] sp = sound.split(":");
            if (sp.length > 0)
                Loudspeaker.sampleRate = Math.max(8000, Integer.parseInt(sp[0]));
            if (sp.length > 1)
                Loudspeaker.bufferSize = Math.max(100, Integer.parseInt(sp[1]));
        }

        // Set the Swing look and feel
        setLookAndFeel(parms.getOption("look"));

        // Get machine image to restore
        File imageFile = parms.getInputFile(1);
        MachineImage image = null;
        if (imageFile != null)
            image = MachineImage.readImage(imageFile);

        // Get initial volume
        int volume = parms.getInteger("volume");
        volume = Math.max(-1, Math.min(10, volume));
        volume = (volume < 0) ? 50 : volume*10;

        // Create a new 803 simulation and view and start the simulator thread
        Computer computer = new Computer(volume);
        ComputerView view = new ComputerView(computer);
        computer.start();

        // Fire up the GUI
        Main gui = new Main(computer, view, image);
        SwingUtilities.invokeLater(gui);
    }

    // Try to set the Swing look and feel
    private static void setLookAndFeel(String look) throws Exception {
        String lafClass = null, lafTheme = null;

        if (Computer.debug) {
            System.out.println("Look:");
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                String name = info.getName();
                if (name.equals("Metal"))
                    name += "/Steel/Ocean";
                System.out.println("  " + name);
            }
        }

        if (look != null) {
            // "Steel" and "Ocean" are themes for "Metal"
            if (look.equalsIgnoreCase("Steel") || look.equalsIgnoreCase("Ocean")) {
                lafTheme = look;
                look= "Metal";
            }

            // Look for an exact match first, then a likely match
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if (info.getName().equalsIgnoreCase(look)) {
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
        } else {
            // No look parameter, so use the system default
            LookAndFeel laf = UIManager.getLookAndFeel();
            if (laf == null || laf.getID().equals("Metal"))
                lafClass = UIManager.getSystemLookAndFeelClassName();
        }

        // Set look and feel if found, otherwise leave as default
        if (lafClass != null) {
            // Set Metal theme if needed
            if (lafTheme != null) {
                if (lafTheme.equalsIgnoreCase("Steel"))
                    MetalLookAndFeel.setCurrentTheme(new DefaultMetalTheme());
                else if (lafTheme.equalsIgnoreCase("Ocean"))
                    MetalLookAndFeel.setCurrentTheme(new OceanTheme());
            }

            UIManager.setLookAndFeel(lafClass);
        }

        JFrame.setDefaultLookAndFeelDecorated(true);
    }


    /*
     * Main code needs to run on the Swing dispatch thread
     */
    JFrame frame;
    Computer computer;
    ComputerView computerView;
    boolean layout;

    public Main(Computer computer, ComputerView view, MachineImage image) {
        this.computer = computer;
        this.computerView = view;

        Image icon = Toolkit.getDefaultToolkit().createImage(getClass().getResource("icon/803-32.png"));

        frame = new JFrame(computer.name + " Simulation (v" + computer.version + ")");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setIconImage(icon);
        frame.setContentPane(view);

        // Set the default window layout and then apply any saved image
        layout = view.defaultLayout();
        if (image != null) {
            layout = image.apply(computer, view);
        }
    }

    public void run() {
        // If layout is not complete (ie from a restored machine image) we
        // need to pack the frame and position it in the centre of the screen.
        if (!layout) {
            Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
            frame.pack();
            frame.setLocation((screen.width - frame.getWidth())/2, (screen.height - frame.getHeight())/2);
        }
        frame.setVisible(true);
    }
}
