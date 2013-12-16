/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2009, 2013
 */
package elliott803.hardware;

import elliott803.hardware.device.Device;
import elliott803.machine.Computer;
import elliott803.machine.Word;
import elliott803.view.ConsoleView;

/**
 * The operator console, including the word generator, various operating
 * functions and the loudspeaker.
 *
 * @author Baldwin
 */

public class Console extends Device {

    public static final int CONSOLE_READ = 1;
    public static final int CONSOLE_OBEY = 2;
    public static final int CONSOLE_NORMAL = 3;

    long wordGen;               // Word generator
    int action;                 // Next action (Read/Obey/Normal)
    boolean clearStore;         // In clear store mode
    boolean manualData;         // In manual data mode
    boolean manualDataDelay;    // Delay setting manual data

    boolean blockTr;            // Status lights
    boolean busy;
    boolean step;
    boolean overflow, fpOverflow;

    boolean speakerOn;          // Speaker on/off
    int speakerVol;             // Speaker volume (0 to 100)

    public Console(Computer computer) {
        this.computer = computer;
        action = CONSOLE_READ;
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

    // Set clear store status
    public void setClearStore(boolean isClearStore) {
        clearStore = isClearStore;
    }

    // Set status lights
    public void setBlockTr(boolean isBlockTr) {
        if (blockTr != isBlockTr) {
            blockTr = isBlockTr;
            viewLights();
        }
    }

    public void setBusy(boolean isBusy) {
        if (busy != isBusy) {
            busy = isBusy;
            viewLights();
            if (busy) {
                soundSilence();
            }    
        }
    }

    public void setOverflow(boolean isOverflow, boolean isFpOver) {
        if (overflow != isOverflow || fpOverflow != isFpOver) {
            overflow = isOverflow;
            fpOverflow = isFpOver;
            viewLights();
        }
    }

    public void setStep(boolean isStep) {
        if (step != isStep) {
            step = isStep;
            viewLights();
            if (step) {
                soundSilence();
            }    
        }
    }

    // Enter a wait state until the Operate bar is pressed
    public void suspend() {
        deviceWait();
    }

    // Perform a reset
    public void reset() {
        computer.cpu.reset();
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
        } else if (clearStore) {
            switch (action) {
                case CONSOLE_NORMAL: computer.run(Computer.ACT_CLEAR); break;
            }
        } else {
            switch (action) {
                case CONSOLE_READ:   computer.cpu.setInstruction(Word.getInstr1(wordGen));  break;
                case CONSOLE_OBEY:   computer.run(Computer.ACT_STEP);  break;
                case CONSOLE_NORMAL: computer.run(Computer.ACT_RUN);   break;
            }
        }
    }

    // Set speaker on/off
    public void setSpeaker(boolean on) {
        speakerOn = on;
        viewVolume();
    }

    // Set the speaker volume (from 0 to 100)
    public void setVolume(int volume) {
        speakerVol = Math.max(0, Math.min(100, volume));
        viewVolume();
    }

    public int getVolume() {
        return speakerVol;
    }
    
    // Make a sound on the speaker
    public void speakerSound(boolean click, int cycles) {
        soundClicks(click, cycles);
    }

    // Mainly for debugging
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("CONSOLE:");
        sb.append(" wordgen=").append(Word.toOctalString(wordGen));
        sb.append(", \"").append(Word.toInstrString(wordGen)).append("\"");
        return sb.toString();
    }

    /*
     * GUI visualisation and sound
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
            view.updateLights(step, blockTr, busy, overflow, fpOverflow);
    }

    void viewVolume() {
        if (view != null)
            view.updateVolume(speakerOn, speakerVol);
    }

    void soundClicks(boolean click, int cycles) {
        if (view != null)
            view.soundSpeaker(click, cycles);
    }

    void soundSilence() {
        if (view != null)
            view.silenceSpeaker();
    }
}
