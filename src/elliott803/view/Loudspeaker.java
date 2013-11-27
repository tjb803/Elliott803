/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2013
 */
package elliott803.view;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
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
     * We create a SouceDataLine that runs at 44.1kHz as this should be supported
     * by pretty well all sound cards.  The fastest frequency pulse should be 288uS 
     * which corresponds to 12.7 samples at 44.1kHz, so we'll use sample lengths of
     * 12 bytes to try to ensure we don't overrun.
     * 
     * We write samples that either contain a 'pulse' (first half of the sample 
     * is non-zero) or 'quiet' (sample is all zeros).  Therefore a constant stream
     * of 'pulses' should make a tone of about 3.5kHz.
     */
    static final int SAMPLE_SIZE = 12;
    
    byte[] pulse, quiet;
    Queue<byte[]> samples;
    
    AtomicBoolean on;
    SourceDataLine line;
    
    public Loudspeaker()  {
        pulse = new byte[SAMPLE_SIZE];
        quiet = new byte[SAMPLE_SIZE];  
        
        samples = new ArrayBlockingQueue<byte[]>(1470); // (=8820/12 - see below)
        on = new AtomicBoolean(false);
        
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
        }
    }

    // Send pulse/quiet samples to the spekaer
    public void sound(boolean click, int count) {
        if (on.get()) {
            byte[] sample = click ? pulse : quiet;
            while (count-- > 0)
                samples.offer(sample);
        }
    }
    
    // Return true if sound queued
    
    
    // Set the volume from 0 to 255.  Volume of 0 means switch off the speaker.
    public void setVolume(int volume) {
        if (volume == 0) {
            on.set(false);
        } else if (line != null) {
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
     * to the output line.  If no samples are available it writes a short
     * piece of silence while waiting for the next sample, just to ensure
     * the output does not go idle.
     * 
     * If the speaker is switched off the thread goes idle and waits for
     * it to be switched back on again.
     */
    public void run() {
        setName("ConsoleSpeaker");
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
                byte[] s = samples.poll();
                if (s != null)
                    line.write(s, 0, SAMPLE_SIZE);
                else        // TODO: is this really necessary??
                    line.write(quiet, 0, SAMPLE_SIZE/2);
            }
            line.flush();
            line.stop();
        }
    }
}
