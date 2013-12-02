/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2013
 */
package elliott803.view;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

/**
 * This class provides the loudspeaker sound for the console.
 * 
 * @author Baldwin
 */
public class Loudspeaker extends Thread {
    /*
     * We use a SouceDataLine with a sample frequency of 44.1kHz as this is very 
     * likely to be a supported rate for all soundcards and we use a sample size 
     * of 12 bytes.  This gives a 'cycle time' of 272.1us which is close enough (at 
     * 44.1kHz) to the required 288us.  Note the the CPU cycle speed is also set 
     * to 272us to match as this makes the sound work better!  (I could get a 
     * closer match at a 48kHz sample rate, but maybe some systems don't support
     * that rate?)
     * 
     * We write samples that either contain a 'pulse' (first half of the sample 
     * is non-zero) or 'quiet' (sample is all zeros).  Therefore a constant stream
     * of 'pulses' should make a tone of about 3.5kHz.
     */
    static final int SAMPLE_SIZE = 12;
    
    byte[] pulse, quiet;
    BlockingQueue<byte[]> samples;
    
    AtomicBoolean on;
    SourceDataLine line;
    
    public Loudspeaker()  {
        pulse = new byte[SAMPLE_SIZE]; 
        quiet = new byte[SAMPLE_SIZE];

        on = new AtomicBoolean(false);
        samples = new ArrayBlockingQueue<byte[]>(1000);

        try {
            // Create the line with a buffer for about 1/5s of sound so it starts 
            // and stops near enough when requested, but not so small it runs out
            // of buffered samples too often.
            AudioFormat af = new AudioFormat(44100, 8, 1, false, false);
            line = AudioSystem.getSourceDataLine(af);
            line.open(af, 8820);    // Multiple of 12 that is about 1/5s at 44.1kHz
            start();                // Start the audio thread
        } catch (LineUnavailableException e) {
            System.err.println(e);
            line = null;            // No sound available;
        } catch (IllegalArgumentException e) {
            System.err.println(e);
            line = null;            // No sound available;
        }
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
            pulse[0] = pulse[5] = (byte)(volume/4);
            pulse[1] = pulse[4] = (byte)(volume/2);
            pulse[2] = pulse[3] = (byte)(volume);
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
                if (!on.get()) {
                    try {
                        wait();
                    } catch (InterruptedException e) { }    
                }
                line.start();
            }
            while (on.get()) {
                byte[] b = samples.poll();
                if (b != null)
                    line.write(b, 0, SAMPLE_SIZE);
                else 
                    line.write(quiet, 0, SAMPLE_SIZE/2);
            }
            line.flush();
            line.stop();
        }
    }
}
