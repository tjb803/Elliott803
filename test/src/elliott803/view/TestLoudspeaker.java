/**
 * Elliott Model 803B Simulator
 * 
 * (C) Copyright Tim Baldwin 2013
 */
package elliott803.view;

import elliott803.view.Loudspeaker;
import junit.framework.TestCase;

/**
 * Unit tests for the console speaker
 * 
 * @author Baldwin
 */
public class TestLoudspeaker extends TestCase {
    
    protected void setUp() {
        speaker = new Loudspeaker();
    }

    Loudspeaker speaker;
    
    public void testMaxTone() throws InterruptedException {
        speaker.setVolume(50);
        long end = System.currentTimeMillis() + 5000;
        while (System.currentTimeMillis() < end) {
            speaker.sound(true, 1);
            if (speaker.isFull())
                Thread.sleep(100);
        }
        speaker.setVolume(0);
    }
    
    public void testHalfTone() throws InterruptedException {
        speaker.setVolume(50);
        long end = System.currentTimeMillis() + 5000;
        while (System.currentTimeMillis() < end) {
            speaker.sound(true, 1);
            speaker.sound(false, 1);
            if (speaker.isFull())
                Thread.sleep(100);
        }
        speaker.setVolume(0);
    }
    
    public void testVolume() throws InterruptedException {
        for (int vol = 100; vol > 0; vol--) {
            speaker.setVolume(vol);
            long end = System.currentTimeMillis() + 20;
            while (System.currentTimeMillis() < end) {
                speaker.sound(true, 1);  
                speaker.sound(false, 2);
                if (speaker.isFull())
                    Thread.sleep(100);
            }    
        }
    }
}
