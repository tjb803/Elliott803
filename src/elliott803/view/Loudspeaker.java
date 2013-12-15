/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2013
 */
package elliott803.view;

import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;

/**
 * This class provides the loudspeaker sound for the console.
 * 
 * @author Baldwin
 */
public class Loudspeaker extends Thread {
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
    public static int sampleRate = 44100;
   
    int frame, cycle;
    byte[] pulse, quiet;
    BlockingQueue<byte[]> samples;
    
    AtomicBoolean on;
    SourceDataLine line;

    public Loudspeaker()  {
        // Calculate frame size closest to 288us for the sample rate and then 
        // the actual cycle time that results.  This allows 'sampleRate' to be
        // changed for experimentation.
        frame = (sampleRate*288)/1000000;
        cycle = (frame*1000000)/sampleRate;

        pulse = new byte[frame]; 
        quiet = new byte[frame];

        on = new AtomicBoolean(false);
        samples = new ArrayBlockingQueue<byte[]>(1000);

        try {
            // Create the line with a buffer for about 1/5s of sound so it starts 
            // and stops near enough when requested, but not so small it runs out
            // of buffered samples too often.
            int bufSize = ((sampleRate/5)/frame)*frame;
            AudioFormat af = new AudioFormat(sampleRate, 8, 1, false, false);
            line = AudioSystem.getSourceDataLine(af);
            line.open(af, bufSize); 
            start();                // Start the audio thread
        } catch (Throwable e) {
            System.err.println(e);  // Ignore all errors trying to get sound
            line = null;            // No sound available;
        }
    }

    // Return cycle time required by the speaker
    public int getCycleTime() {
        return cycle;
    }
    
    // Send pulse/quiet samples to the speaker
    public void sound(boolean click, int count) {
        if (on.get()) {
            byte[] sample = click ? pulse : quiet;
            while (count-- > 0)
                samples.offer(sample);
        }
    }
 
    // Is queue full - mostly used by tests
    public boolean isFull() {
        return (samples.remainingCapacity() == 0);
    }
 
    // Set the volume from 0 to 100.  Volume 0 means switch off the speaker.
    public void setVolume(int volume) {
        if (volume == 0) {
            on.set(false);
        } else if (line != null) {
            volume = (255*volume*volume)/(100*100); // Scale in a non-linear curve
            Arrays.fill(pulse, 0, frame/2, (byte)volume);
            synchronized (this) {
                if (!on.get()) {
                    on.set(true);
                    notify();
                }
            }
        }    
    }

    /*
     * The sound thread just pulls samples from the queue and writes them
     * to the output line. It seems best to continue to write silence if the
     * queue becomes empty, otherwise you get strange clicks, pops and bits 
     * of sound - I don't really know why, I'm sure it shouldn't do that!
     * 
     * If the speaker is switched off the thread goes idle and waits for
     * it to be switched back on again.
     */
    public void run() {
        setName("Loudspeaker");
        setPriority(Thread.MAX_PRIORITY);
        while (true) {
            synchronized (this) {
                while (!on.get()) {
                    try {
                        wait();
                    } catch (InterruptedException e) { }    
                }
            }
            line.start();
            while (on.get()) {
                byte[] b = samples.poll();
                if (b != null)
                    line.write(b, 0, frame);
                else 
                    line.write(quiet, 0, frame/2);
            }
            line.flush();
            line.stop();
        }
    }
}
