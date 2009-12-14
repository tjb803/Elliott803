/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2009
 */
package elliott803.hardware;

import elliott803.machine.Computer;
import elliott803.machine.Word;
import elliott803.view.ConsoleView;

/**
 * The operator console, including the word generator and various operating
 * functions.
 *
 * @author Baldwin
 */

public class Console {

    public static final int CONSOLE_READ = 1;
    public static final int CONSOLE_OBEY = 2;
    public static final int CONSOLE_NORMAL = 3;

    public  Computer computer;  // The owning computer

    long wordGen;           // Word generator
    int action;             // Next action (Read/Obey/Normal)

    boolean busy;           // Status lights
    boolean step;
    boolean overflow, fpOverflow;

    public Console(Computer computer) {
        this.computer = computer;
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
        wordGen |= 1L<<(bit-1);
        viewWordGen();
    }

    // Clear a bit in the word generator, bit = 1 to 39
    public void clearWordGenBit(int bit) {
        wordGen &= ~(1L<<(bit-1));
        viewWordGen();
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

    // Perform a reset
    public void reset() {
        computer.cpu.reset();
    }

    // Set the next action to be performed S
    public void setAction(int value) {
        action = value;
    }

    // Operate - perform the last selected action
    public void operate() {
        switch (action) {
            case CONSOLE_READ:   computer.cpu.setInstruction(wordGen);  break;
            case CONSOLE_OBEY:   computer.cpu.obey();  break;
            case CONSOLE_NORMAL: computer.cpu.run();   break;
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
