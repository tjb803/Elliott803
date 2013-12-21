/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2013
 */
package elliott803.view;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;

import elliott803.machine.Computer;

/**
 * This class provides the loudspeaker sound for the console.
 *
 * @author Baldwin
 */
public class Loudspeaker {
    /*
     * Use an audio SourceDataLine with a sample frequency of 44.1kHz as this is a
     * standard that should be supported directly by just about all sound hardware.
     * Sound is written in 'frames' of 12 samples, so at 44.1kHz this represents a
     * time of 272.1us.  For the best sound emulation we really need this frame time
     * to match the CPU cycle time of 288us - it is close but not quite right, so I
     * cheat a bit and set the CPU cycle time down to 272us.
     *
     * If we really want the CPU to run at 288us and we want the best possible sound
     * there are a couple of other options:
     *  - pick a different sample frequency - 48kHz is probably standard on all
     *    hardware and would get closer (14 samples at 48kHz = 291.7us).
     *  - or vary the frame size - at 44.1kHz a sequence of frames sized 12,13,13,13
     *    would average 289.1us.
     *
     * Frames either contain a 'pulse' (first half of the frame is non-zero)
     * or 'quiet' (frame is all zeros).  Therefore a constant stream of 'pulses'
     * should make a tone of about 3.5kHz.
     */
    public static int sampleRate = 44100;   // Sample frequency
    public static int bufferSize = 0;       // Buffer size (0 = use default)

    int frame, cycle;
    byte[] pulse, quiet;

    AtomicBoolean on;
    SourceDataLine line;

    public Loudspeaker()  {
        // Calculate frame size closest to 288us for the sample rate and then
        // the actual cycle time that results.  This allows 'sampleRate' to be
        // changed for experimentation.
        frame = (sampleRate*(288)+250000)/1000000;
        cycle = (frame*1000000)/sampleRate;

        on = new AtomicBoolean(false);

        pulse = new byte[frame];
        quiet = new byte[frame];

        try {
            AudioFormat af = new AudioFormat(sampleRate, 8, 1, false, false);
            line = AudioSystem.getSourceDataLine(af);
            if (bufferSize > 0) {     
                line.open(af, (bufferSize/frame)*frame);
            } else {
                line.open();
            }
        } catch (Throwable e) {
            System.err.println(e);  // Ignore all errors trying to get sound
            line = null;            // No sound available;
        }

        if (Computer.debug) {
            System.out.println("Speaker:");
            if (line != null) {
                System.out.println("  sample rate: " + line.getFormat().getFrameRate()/1000 + "kHz");
                System.out.println("  frame length: " + frame);
                System.out.println("  buffer size: " + line.getBufferSize());
                System.out.println("  cycle time: " + cycle + "us");
            } else {
                System.out.println("  not available");
            }
        }
    }

    // Return cycle time needed to match the speaker data rate
    public int getCycleTime() {
        return cycle;
    }

    // Send pulse/quiet frames to the speaker
    public void sound(boolean click, int count) {
        if (on.get()) {
            byte[] b = click ? pulse : quiet;
            while (count-- > 0) {
                if (line.available() >= frame)
                    line.write(b, 0, frame);
            }
            line.start();
        }
    }

    // Silence the speaker
    public void silence() {
        line.stop();
        line.flush();
    }

    // Set the volume from 0 to 100.  Volume 0 means switch off the speaker.
    public void setVolume(int volume) {
        if (volume == 0) {
            on.set(false);
            silence();
        } else if (line != null) {
            volume = (255*volume*volume)/(100*100); // Scale in a non-linear curve
            Arrays.fill(pulse, 0, frame/2, (byte)volume);
            on.set(true);
        }
    }

    // Is queue full - mostly used by tests
    public boolean isFull() {
        return (line.available() < frame);
    }
}
