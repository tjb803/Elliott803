/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2009, 2013
 */
package elliott803.hardware;

import elliott803.hardware.device.Device;
import elliott803.machine.Computer;
import elliott803.view.TapeDeviceView;

/**
 * This is the base class for tape readers, punches and teletypes.
 *
 * @author Baldwin
 */
public abstract class TapeDevice extends Device {

    public int id;              // Device id

    public TapeDevice(Computer computer, int id) {
        this.computer = computer;
        this.id = id;
    }

    // Transfer a character to or from the device
    protected void transfer(int ch) {
        computer.console.setBlockTr(true);
        devicePause();
        viewChar(ch);
    }
    
    // Need to override the deviceWait to enable GUI updates
    protected void deviceWait() {
        viewBusy(true);
        super.deviceWait();
        viewBusy(false);
    }

    /*
     * GUI Visualisation
     */

    TapeDeviceView view;

    public void setView(TapeDeviceView view) {
        this.view = view;
    }

    void viewChar(int ch) {
        if (view != null)
            view.updateCh(ch);
    }

    void viewTape(Object tape) {
        if (view != null)
            view.updateTape(tape);
    }

   void viewBusy(boolean busy) {
        if (view != null)
            view.updateWait(busy);
    }
}
