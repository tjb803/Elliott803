/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2009, 2012
 */
package elliott803.view;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import elliott803.machine.Computer;

/**
 * A visual representation of the full computer.  This is a frame that contains the
 * visuals for the various computer hardware elements.
 *
 * @author Baldwin
 */
public class ComputerView extends JDesktopPane implements ActionListener {
    private static final long serialVersionUID = 1L;
    
    static final String IMAGE_LOAD = "Load...";
    static final String IMAGE_SAVE = "Save...";

    public ControlView control;
    public ConsoleView console;
    public CpuView cpu;
    public StoreView store;
    public PtsView pts;
    public PlotterView plotter;
    
    Computer computer;
    JFileChooser imageSelect;

    public ComputerView(Computer computer) {
        setLayout(null);
        
        this.computer = computer;

        // Create views for the various devices
        // Note: PtsView not currently displayed as it does not seem very useful!
        control = new ControlView(computer, this);
        console = new ConsoleView(computer.console);
        cpu = new CpuView(computer.cpu);
        store = new StoreView(computer.core);
        pts = new PtsView(computer.pts);
        plotter = new PlotterView(computer.plotter);

        add(control);
        add(console);
        add(cpu);
        add(store);
        add(pts.reader[0]);
        add(pts.reader[1]);
        add(pts.punch[0]);
        add(pts.punch[1]);
        add(pts.teletype);      // Make sure the plotter is added after the teletype
        add(plotter);           // as it want it to appear behind.

        // Create a file chooser dialog for the load/save image function
        imageSelect = new JFileChooser(new File("."));
        imageSelect.setDialogTitle("Elliott 803 Machine Image");
        imageSelect.setFileFilter(new FileFilter() {
            public boolean accept(File f) {
                return (f.isDirectory() || f.getName().endsWith(".803"));
            }
            public String getDescription() {
                return "Elliott 803 Machine Images";
            }
        });    
    }
   
    // Layout all the windows in their default positions
    public boolean defaultLayout() {   
        // First some messiness related to the GTK+ look and feel on Linux.  This seems
        // to want to add some sort of TaskBar at the bottom in a layer that overlays
        // our windows (in the default layer).  If we can find something resembling this
        // bar (in a non-default layer) we need to adjust our size to allow for it.
        int extraY = 0, extraHeight = 0;
        for (Component c: getComponents()) {
            if (getLayer(c) != DEFAULT_LAYER) {
                if (c.getY() == 0)      // TaskBar is at the top
                    extraY = Math.max(extraY, c.getHeight());
                else if (c.getY() < 0)  // TaskBar is at the bottom
                    extraHeight = Math.max(extraHeight, c.getHeight()-1);
            }
        }

        int plotterX = pts.teletype.getWidth() - pts.teletype.getRootPane().getPreferredSize().width;
        int plotterY = pts.teletype.getHeight() - pts.teletype.getRootPane().getPreferredSize().height;

        int max1X = control.getWidth();
        int max2X = cpu.getWidth() + store.getWidth();
        int maxCX = Math.max(max1X, max2X);

        int max3X = console.getWidth() + maxCX + 10;;
        int max4X = pts.teletype.getWidth() + plotterX + pts.punch[0].getWidth() + pts.reader[0].getWidth() + 10;
        int maxX = Math.max(max3X, max4X);

        int max1Y = console.getHeight() + pts.teletype.getHeight() + plotterY + 10;
        int max2Y = control.getHeight() + cpu.getHeight() + 2*pts.punch[0].getHeight() + 10;
        int max3Y = control.getHeight() + store.getHeight() + 2*pts.reader[0].getHeight() + 10;
        int maxY = Math.max(Math.max(max1Y, max2Y), max3Y);

        int consoleX = 0, consoleY = extraY;
        int controlX = maxX - maxCX, controlY = extraY;
        int storeX = maxX - store.getWidth(), storeY = controlY + control.getHeight();
        int cpuX = storeX - cpu.getWidth(), cpuY = controlY + control.getHeight();

        int teletypeX = 0, teletypeY = maxY - pts.teletype.getHeight();
        int punch2X = maxX - pts.punch[1].getWidth(), punch2Y = maxY - pts.punch[1].getHeight();
        int reader2X = punch2X - pts.reader[1].getWidth(), reader2Y = punch2Y;
        int punch1X = punch2X,  punch1Y = punch2Y - pts.punch[0].getHeight();
        int reader1X = reader2X, reader1Y = reader2Y - pts.reader[0].getHeight();

        control.setSize(maxCX, control.getHeight());
        control.setLocation(controlX, controlY);
        console.setLocation(consoleX, consoleY);
        cpu.setLocation(cpuX, cpuY);
        store.setLocation(storeX, storeY);

        plotter.setSize(pts.teletype.getWidth(), pts.teletype.getHeight());
        plotter.setLocation(teletypeX + plotterX, teletypeY - plotterY);

        pts.teletype.setLocation(teletypeX, teletypeY);
        pts.punch[0].setLocation(punch1X, punch1Y);
        pts.punch[1].setLocation(punch2X, punch2Y);
        pts.reader[0].setLocation(reader1X, reader1Y);
        pts.reader[1].setLocation(reader2X, reader2Y);

        Dimension size = new Dimension(maxX, maxY+extraY+extraHeight);
        setPreferredSize(size);
        
        return false;       // Layout is incomplete (frame is unsized)
    } 
    
    /*
     * Prompt to load and save machine images
     */

    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals(IMAGE_LOAD)) {
            if (imageSelect.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = imageSelect.getSelectedFile();
                MachineImage image = MachineImage.readImage(file);
                image.apply(computer, this);
            }
        } else if (e.getActionCommand().equals(IMAGE_SAVE)) {
            if (imageSelect.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = imageSelect.getSelectedFile();
                if (!file.getName().contains("."))
                    file = new File(file.getPath() + ".803");
                MachineImage image = new MachineImage(computer, this);
                image.write(file);
            }
        }
    }
}
