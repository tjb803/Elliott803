/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2009
 */
package elliott803.view;

import java.awt.Dimension;

import javax.swing.JDesktopPane;

import elliott803.machine.Computer;


/**
 * A visual representation of the full computer.  This is a frame that contains the
 * visuals for the various computer hardware elements.
 *
 * @author Baldwin
 */
public class ComputerView extends JDesktopPane {
    private static final long serialVersionUID = 1L;

    public ConsoleView console;
    public CpuView cpu;
    public StoreView store;
    public PtsView pts;

    public ComputerView(Computer computer) {
        setLayout(null);

        // Create views for the various devices
        console = new ConsoleView(computer.console);
        cpu = new CpuView(computer.cpu);
        store = new StoreView(computer.core);
        pts = new PtsView(computer.pts);

        add(console);
        add(cpu);
        add(store);
        add(pts);
        add(pts.reader[0]);
        add(pts.reader[1]);
        add(pts.punch[0]);
        add(pts.punch[1]);
        add(pts.teletype);
    }

    public void layout(ViewDefinition def) {
        if (def.empty) {
            // Empty layout, so arrange everything in default positions
            int max1X = console.getWidth() + cpu.getWidth() + store.getWidth() + 20;
            int max2X = pts.teletype.getWidth() + pts.punch[0].getWidth() + pts.reader[0].getWidth() + 20;
            int maxX = Math.max(max1X, max2X);

            int max1Y = console.getHeight() + pts.teletype.getHeight() + 15;
            int max2Y = cpu.getHeight() + 2*pts.punch[0].getHeight() + pts.getHeight() + 15;
            int max3Y = store.getHeight() + 2*pts.reader[0].getHeight() + pts.getHeight() + 15;
            int maxY = Math.max(Math.max(max1Y, max2Y), max3Y);

            int consoleX = 0, consoleY = 0;
            int storeX = maxX - store.getWidth(), storeY = 0;
            int cpuX = storeX - cpu.getWidth() - 10, cpuY = 0;
            
            pts.setSize(pts.reader[0].getWidth() + pts.punch[0].getWidth() + 5, pts.getHeight());
            
            int teletypeX = 0, teletypeY = maxY - pts.teletype.getHeight();
            int ptsX = maxX - pts.getWidth(), ptsY = maxY - pts.getHeight();
            int punch2X = maxX - pts.punch[1].getWidth(), punch2Y = ptsY - pts.punch[1].getHeight();
            int reader2X = punch2X - pts.reader[1].getWidth() - 5, reader2Y = punch2Y;
            int punch1X = punch2X,  punch1Y = punch2Y - pts.punch[0].getHeight();
            int reader1X = reader2X, reader1Y = reader2Y - pts.reader[0].getHeight();

            console.setLocation(consoleX, consoleY);
            cpu.setLocation(cpuX, cpuY);
            store.setLocation(storeX, storeY);

            pts.teletype.setLocation(teletypeX, teletypeY);
            pts.setLocation(ptsX, ptsY);
            pts.punch[0].setLocation(punch1X, punch1Y);
            pts.punch[1].setLocation(punch2X, punch2Y);
            pts.reader[0].setLocation(reader1X, reader1Y);
            pts.reader[1].setLocation(reader2X, reader2Y);

            // Update the size of the desktop
            def.viewWidth = maxX;  def.viewHeight = maxY;
            Dimension size = new Dimension(maxX, maxY);
            setSize(size);
            setPreferredSize(size);
        }
    }
}
