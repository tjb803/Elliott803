/**
 * Elliott Model 803B Simulator
 * 
 * (C) Copyright Tim Baldwin 2009,2012
 */
package elliott803.view.component;

import java.util.Random;

import javax.swing.Box;
import javax.swing.BoxLayout;

import elliott803.machine.Instruction;
import elliott803.machine.Word;

public class TestDisplayWord extends BaseComponentTest {
    
    protected void setupTest() {
        // Create the various storage display options
        displayBinary = new DisplayWord(DisplayWord.Type.BIN39);
        displayBin38 = new DisplayWord(DisplayWord.Type.BIN38);
        displayOctal = new DisplayWord(DisplayWord.Type.OCTAL);
        displayInt = new DisplayWord(DisplayWord.Type.INTEGER);
        displayFloat = new DisplayWord(DisplayWord.Type.FLOAT);
        displayInstruction = new DisplayWord(DisplayWord.Type.INSTRUCTION);
        displayInstr = new DisplayInstruction();
        displayAddr = new DisplayAddress();
        
        // Add to the test frame
        testView.setLayout(new BoxLayout(testView, BoxLayout.Y_AXIS));
        testView.add(displayBinary);
        testView.add(displayBin38);
        testView.add(displayOctal);
        testView.add(displayInt);
        testView.add(displayFloat);
        testView.add(displayInstruction);
        testView.add(Box.createVerticalStrut(10));
        testView.add(displayInstr);
        testView.add(displayAddr);
    }
    
    private DisplayWord displayBinary;
    private DisplayWord displayBin38;
    private DisplayWord displayOctal;
    private DisplayWord displayInt;
    private DisplayWord displayFloat;
    private DisplayWord displayInstruction;

    private DisplayInstruction displayInstr;
    private DisplayAddress displayAddr;
    
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
            int instr = Word.getInstr2(word);
            displayInstr.setValue(instr);
            displayAddr.setValue(Instruction.getAddr(instr));
            Thread.sleep(500);
        }
    }
}
