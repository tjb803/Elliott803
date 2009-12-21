/**
 * Elliott Model 803B Simulator
 * 
 * (C) Copyright Tim Baldwin 2009
 */
package elliott803.view.component;

import java.awt.Container;
import java.util.Random;

import javax.swing.BoxLayout;

import elliott803.machine.Word;
import elliott803.view.component.DisplayWord;

public class TestDisplayWord extends BaseComponentTest {
    protected void setUp() {
        super.setUp();
        
        // Create the various storage display options
        displayBinary = new DisplayWord(DisplayWord.Type.BIN39);
        displayBin38 = new DisplayWord(DisplayWord.Type.BIN38);
        displayOctal = new DisplayWord(DisplayWord.Type.OCTAL);
        displayInt = new DisplayWord(DisplayWord.Type.INTEGER);
        displayFloat = new DisplayWord(DisplayWord.Type.FLOAT);
        displayInstruction = new DisplayWord(DisplayWord.Type.INSTRUCTION);
        
        // Add to the test frame
        Container content = testFrame.getContentPane();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.add(displayBinary);
        content.add(displayBin38);
        content.add(displayOctal);
        content.add(displayInt);
        content.add(displayFloat);
        content.add(displayInstruction);
        testFrame.pack();
        testFrame.setVisible(true);
    }
    
    private DisplayWord displayBinary;
    private DisplayWord displayBin38;
    private DisplayWord displayOctal;
    private DisplayWord displayInt;
    private DisplayWord displayFloat;
    private DisplayWord displayInstruction;
    
    public void testRandom() throws Exception {
        Random rand = new Random();
        while (true) {
            long word = Word.asWord(rand.nextLong());
            displayBinary.setValue(word);
            displayBin38.setValue(word);
            displayOctal.setValue(word);
            displayInt.setValue(word);
            displayFloat.setValue(word);
            displayInstruction.setValue(word);
            Thread.sleep(100);
        }
    }
}
