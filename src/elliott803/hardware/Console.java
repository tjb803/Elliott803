/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2009
 */
package elliott803.hardware;

import elliott803.hardware.device.Device;
import elliott803.machine.Computer;
import elliott803.machine.Word;
import elliott803.view.ConsoleView;

/**
 * The operator console, including the word generator and various operating
 * functions.
 *
 * @author Baldwin
 */

public class Console extends Device {

    public static final int CONSOLE_READ = 1;
    public static final int CONSOLE_OBEY = 2;
    public static final int CONSOLE_NORMAL = 3;

    long wordGen;               // Word generator
    int action;                 // Next action (Read/Obey/Normal)
    boolean manualData;         // In manual data mode
    boolean manualDataDelay;    // Delay setting manual data

    boolean busy;               // Status lights
    boolean step;
    boolean overflow, fpOverflow;

    public Console(Computer computer) {
        this.computer = computer;
    }

    // Read the word generator as as device.  This will cause a wait if the 
    // manual data mode is set.  
    public long read() {
        if (manualData) {
            suspend();
        } else if (manualDataDelay) {
            manualData = true;
            manualDataDelay = false;
        }
        return wordGen;
    }

    // Read the current value of the word generator
    public long readWordGen() {
        return wordGen;
    }

    // Set the word generator
    public void setWordGen(long value) {
        wordGen = value;
        viewWordGen();
    }

    // Set a bit in the word generator, bit = 1 to 39
    public void setWordGenBit(int bit) {
        if (bit > 0 && bit < 40) {
            wordGen |= 1L<<(bit-1);
            viewWordGen();
        }    
    }

    // Clear a bit in the word generator, bit = 1 to 39
    public void clearWordGenBit(int bit) {
        if (bit > 0 && bit < 40) {
            wordGen &= ~(1L<<(bit-1));
            viewWordGen();
        }    
    }
    
    // Toggle a bit in the word generator, bit = 1 to 39
    public void toggleWordGenBit(int bit) {
        if (bit > 0 && bit < 40) {
            wordGen ^= 1L<<(bit-1);
            viewWordGen();
        }
    }
    
    // Set manual data status
    public void setManualData(boolean isManualData) {
        manualData = isManualData;
    }
    
    // Set manual data on, but only after the next read 
    public void setManualDataDelay() {
        manualData = false;
        manualDataDelay = true;
    }

    // Set status lights
    public void setBusy(boolean isBusy) {
        busy = isBusy;
        viewLights();
    }

    public void setOverflow(boolean isOverflow, boolean isFpOver) {
        overflow = isOverflow;
        fpOverflow = isFpOver;
        viewLights();
    }

    public void setStep(boolean isStep) {
        step = isStep;
        viewLights();
    }
    
    // Enter a wait state until the Operate bar is pressed
    public void suspend() {
        deviceWait();
    }

    // Perform a reset
    public void reset() {
        computer.cpu.reset();
    }
    
    // Perform a clear store
    public void clear() {
        computer.run(Computer.ACT_CLEAR);
    }

    // Set the next action to be performed
    public void setAction(int value) {
        computer.cpu.stop();
        action = value;
    }

    // Operate - perform the last selected step-by-step action
    public void operate() {
        if (deviceBusy()) {
            deviceReady();
        } else {
            switch (action) {
                case CONSOLE_READ:   computer.cpu.setInstruction(Word.getInstr1(wordGen));  break;
                case CONSOLE_OBEY:   computer.run(Computer.ACT_STEP);  break;
                case CONSOLE_NORMAL: computer.run(Computer.ACT_RUN);   break;
            }
        }    
    }

    // Mainly for debugging
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("CONSOLE:");
        sb.append(" wordgen=").append(Word.toOctalString(wordGen));
        sb.append(", \"").append(Word.toInstrString(wordGen)).append("\"");
        return sb.toString();
    }

    /*
     * GUI Visualisation
     */

    ConsoleView view;

    public void setView(ConsoleView view) {
        this.view = view;
        viewLights();           // Set initial console lights
    }

    void viewWordGen() {
        if (view != null)
            view.updateWordGen(wordGen);
    }

    void viewLights() {
        if (view != null)
            view.updateLights(step, busy, overflow, fpOverflow);
    }
}
