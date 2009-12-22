/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2009
 */
package elliott803.view;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import elliott803.view.component.DeviceLight;
import elliott803.view.component.DeviceMode;
import elliott803.view.component.DeviceModeSelect;
import elliott803.view.component.DisplayLight;

/**
 * A visual representation of a tape device.  This is the base class for the
 * reader and punch views.
 *
 * @author Baldwin
 */
public abstract class TapeDeviceView extends JInternalFrame implements ActionListener {
    private static final long serialVersionUID = 1L;

    static final String DEV_EJECT = "Eject";
    static final String DEV_LOAD = "Load";
    static final String DEV_SAVE = "Save";

    JLabel file;
    DeviceMode mode;
    DeviceLight wait, busy;
    JFileChooser select;
    DeviceModeSelect modeSelect;
    Timer busyTimer;

    // Simple view used by Teletype
    public TapeDeviceView(String name) {
        super(name, true, false, false, true);

        file = new JLabel();
        mode = new DeviceMode(DeviceMode.MODE_SYSTEM);

        // Teletype file save dialog
        modeSelect = new DeviceModeSelect(false, false);
        select = new JFileChooser(new File("."));
        select.setDialogTitle(getTitle() + " - Output Log");
        select.setAccessory(modeSelect);
    }

    // Default view used for readers and punches
    public TapeDeviceView(String name, String type, String operation, int id) {
        super(name + " " + id, false, false, false, true);

        file = new JLabel(" ");
        mode = new DeviceMode(DeviceMode.MODE_ELLIOTT);
        wait = new DeviceLight("Waiting", DisplayLight.RED);
        busy = new DeviceLight("Busy", DisplayLight.GREEN);

        // Create the file selection dialog
        modeSelect = new DeviceModeSelect(operation.equals(DEV_LOAD), true);
        select = new JFileChooser(new File("."));
        select.setDialogTitle(getTitle() + " - " + operation + " Tape");
        select.setAccessory(modeSelect);

        // Create the busy light timer - device shows as busy when a character is
        // read/written and busy light goes out if there is no activity for 1 second
        busyTimer = new Timer(1000, this);
        busyTimer.setRepeats(false);
        busyTimer.start();

        JPanel tape = new JPanel();
        tape.setLayout(new BoxLayout(tape, BoxLayout.Y_AXIS));
        tape.setBorder(BorderFactory.createTitledBorder(type + " Tape"));
        tape.setAlignmentX(CENTER_ALIGNMENT);
        tape.add(file);

        JPanel action = new JPanel();
        action.setLayout(new BoxLayout(action, BoxLayout.X_AXIS));
        action.setAlignmentX(CENTER_ALIGNMENT);

        JPanel bp = new JPanel();
        bp.setLayout(new BoxLayout(bp, BoxLayout.Y_AXIS));
        bp.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        JButton eb = new JButton(DEV_EJECT);
        eb.setAlignmentX(LEFT_ALIGNMENT);
        eb.addActionListener(this);
        JButton ob = new JButton(operation + "...");
        ob.setAlignmentX(LEFT_ALIGNMENT);
        ob.setActionCommand(operation);
        ob.addActionListener(this);
        bp.add(eb);
        bp.add(Box.createVerticalStrut(5));
        bp.add(ob);

        JPanel sp = new JPanel();
        sp.setLayout(new BoxLayout(sp, BoxLayout.Y_AXIS));
        sp.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        busy.setAlignmentX(RIGHT_ALIGNMENT);
        wait.setAlignmentX(RIGHT_ALIGNMENT);
        mode.setAlignmentX(RIGHT_ALIGNMENT);
        sp.add(busy);
        sp.add(Box.createVerticalGlue());
        sp.add(wait);
        sp.add(Box.createVerticalGlue());
        sp.add(mode);

        action.add(bp);
        action.add(Box.createHorizontalGlue());
        action.add(sp);

        Container content = getContentPane();
        content.setLayout(new BorderLayout());
        content.add(tape, BorderLayout.NORTH);
        content.add(action, BorderLayout.SOUTH);
        pack();
        setVisible(true);
    }

    /*
     * Common file handling dialog
     */

    void setTape(File file, String mode, boolean ascii) {
        // Subclasses will override this to handle file I/O
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == busyTimer) {
            busy.setValue(false);
        } else  if (e.getActionCommand().equals(DEV_EJECT)) {
            file.setText(" ");
            setTape(null, null, false);
        } else {
            if (select.showDialog(this, e.getActionCommand()) == JFileChooser.APPROVE_OPTION) {
                File f = select.getSelectedFile();
                file.setText(f.getName());
                mode.setMode(modeSelect.getMode());
                setTape(f, modeSelect.getMode(), modeSelect.getAscii());
            }
        }
    }

    /*
     * GUI Visualisation
     */

    public void updateCh(int ch) {
        busy.setValue(true);
        busyTimer.restart();
    }

    public void updateTape(Object tape) {
        if (tape == null)
            file.setText(" ");
    }

    public void updateWait(boolean state) {
        wait.setValue(state);
    }
}
