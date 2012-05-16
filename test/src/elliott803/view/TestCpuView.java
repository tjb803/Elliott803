/**
 * Elliott Model 803B Simulator
 * 
 * (C) Copyright Tim Baldwin 2009
 */
package elliott803.view;

import java.util.Random;

import elliott803.hardware.CPU;
import elliott803.machine.Instruction;
import elliott803.machine.Word;

/**
 * Unit test for the CpuView component
 * 
 * @author Baldwin
 */
public class TestCpuView extends BaseViewTest {
    
    protected void setupTest() {
        cpuView = new CpuView(new CPU(testComputer));
        testView.add(cpuView);
    }
    
    private CpuView cpuView;

    public void testRandom() throws Exception {
        Random rand = new Random();
        while (true) {
            long a = Word.asWord(rand.nextLong());
            long ar = Word.asWord(rand.nextLong());
            int ir = Instruction.asInstr(rand.nextInt());
            int pc = rand.nextInt(8192);
            long instr = Word.asWord(rand.nextLong());
            cpuView.updateRegisters(a, ar, ir, pc, instr);
            cpuView.updateFlags(rand.nextBoolean(), rand.nextBoolean());
            Thread.sleep(500);
        }
    }    
}
