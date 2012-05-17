/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2009,2010
 */
package elliott803.view;

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
        console = new ConsoleView(computer.console, this);
        cpu = new CpuView(computer.cpu);
        store = new StoreView(computer.core);
        pts = new PtsView(computer.pts);
        plotter = new PlotterView(computer.plotter);

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
        int plotterX = pts.teletype.getWidth() - pts.teletype.getRootPane().getPreferredSize().width;
        int plotterY = pts.teletype.getHeight() - pts.teletype.getRootPane().getPreferredSize().height;
        
        int max1X = console.getWidth() + cpu.getWidth() + store.getWidth() + 20;
        int max2X = pts.teletype.getWidth() + plotterX + pts.punch[0].getWidth() + pts.reader[0].getWidth() + 20;
        int maxX = Math.max(max1X, max2X);

        int max1Y = console.getHeight() + pts.teletype.getHeight() + plotterY + 10;
        int max2Y = cpu.getHeight() + 2*pts.punch[0].getHeight() + 15;
        int max3Y = store.getHeight() + 2*pts.reader[0].getHeight() + 15;
        int maxY = Math.max(Math.max(max1Y, max2Y), max3Y);

        int consoleX = 0, consoleY = 0;
        int storeX = maxX - store.getWidth(), storeY = 0;
        int cpuX = storeX - cpu.getWidth() - 10, cpuY = 0;

        int teletypeX = 0, teletypeY = maxY - pts.teletype.getHeight();
        int punch2X = maxX - pts.punch[1].getWidth(), punch2Y = maxY - pts.punch[1].getHeight();
        int reader2X = punch2X - pts.reader[1].getWidth() - 5, reader2Y = punch2Y;
        int punch1X = punch2X,  punch1Y = punch2Y - pts.punch[0].getHeight();
        int reader1X = reader2X, reader1Y = reader2Y - pts.reader[0].getHeight();

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

        Dimension size = new Dimension(maxX, maxY);
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
