/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2009
 */
package elliott803.view;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;

import elliott803.hardware.Punch;
import elliott803.telecode.TelecodeOutputStream;
import elliott803.view.component.DeviceMode;


/**
 * A visual representation of a tape punch
 *
 * @author Baldwin
 */
public class PunchView extends TapeDeviceView {
    private static final long serialVersionUID = 1L;

    Punch punch;

    public PunchView(Punch punch, int id) {
        super("Punch ", "Output", TapeDeviceView.DEV_SAVE, id);
        this.punch = punch;
        punch.setView(this);
    }

    // Need to implement setTape to handle new tape loaded
    void setTape(File file, String mode, boolean ascii) {
        if (file == null) {
            punch.setTape(null);
        } else {
            try {
                OutputStream output = null;
                if (mode.equals(DeviceMode.MODE_ELLIOTT)) {
                    output = new FileOutputStream(file);
                } else {
                    output = new TelecodeOutputStream(new FileWriter(file), ascii);
                }
                punch.setTape(output);
            } catch (IOException e) {
                System.err.println(e);
            }
        }
    }
}
