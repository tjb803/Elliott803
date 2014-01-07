/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2009
 */
package elliott803.machine;

import junit.framework.TestCase;

/**
 * JUnit tests for the Instruction class
 * 
 * @author Baldwin
 */
public class TestInstruction extends TestCase {
    
    static String I77_8191 = "111111-1111111111111";
    static String I40_128  = "100000-0000010000000";
    static String I65_7    = "110101-0000000000111";
    
    /*
     * Check basic splitting of instruction pieces
     */
    public void testGetOp() throws Exception {
        int i1 = makeInstruction(I77_8191);
        int i2 = makeInstruction(I40_128);
        int i3 = makeInstruction(I65_7);
        assertEquals(077, Instruction.getOp(i1));
        assertEquals(040, Instruction.getOp(i2));
        assertEquals(065, Instruction.getOp(i3));
    }
    
    public void testGetAddr() throws Exception {
        int i1 = makeInstruction(I77_8191);
        int i2 = makeInstruction(I40_128);
        int i3 = makeInstruction(I65_7);
        assertEquals(8191, Instruction.getAddr(i1));
        assertEquals(128, Instruction.getAddr(i2));
        assertEquals(7, Instruction.getAddr(i3));
    }
    
    /*
     * Check building up instructions from parts
     */
    public void testAsOp() throws Exception {
        int i1 = makeOpcode(I77_8191);
        int i2 = makeOpcode(I40_128);
        int i3 = makeOpcode(I65_7);
        assertEquals(i1, Instruction.asOp(077));
        assertEquals(i2, Instruction.asOp(040));
        assertEquals(i3, Instruction.asOp(065));
    }
    
    public void testAsAddr() throws Exception {
        int i1 = makeAddress(I77_8191);
        int i2 = makeAddress(I40_128);
        int i3 = makeAddress(I65_7);
        assertEquals(i1, Instruction.asAddr(8191));
        assertEquals(i2, Instruction.asAddr(128));
        assertEquals(i3, Instruction.asAddr(7));
    }
    
    public void testAsInstr() throws Exception {
        int i1 = makeInstruction(I77_8191);
        int i2 = makeInstruction(I40_128);
        int i3 = makeInstruction(I65_7);
        assertEquals(i1, Instruction.asInstr(077, 8191));
        assertEquals(i2, Instruction.asInstr(040, 128));
        assertEquals(i3, Instruction.asInstr(065, 7));
    }

    /*
     * Check toString formats
     */
    public void testToOpString() throws Exception {
        assertEquals("77", Instruction.toOpString(077));
        assertEquals("00", Instruction.toOpString(000));
        assertEquals("65", Instruction.toOpString(065));
    }
    
    public void testToAddrString() throws Exception {
        assertEquals("8191", Instruction.toAddrString(8191));
        assertEquals(" 128", Instruction.toAddrString(128));
        assertEquals("   7", Instruction.toAddrString(7));
    }
    
    public void testToInstrString() throws Exception {
        int i1 = makeInstruction(I77_8191);
        int i2 = makeInstruction(I40_128);
        int i3 = makeInstruction(I65_7);
        assertEquals("77 8191", Instruction.toInstrString(i1));
        assertEquals("40  128", Instruction.toInstrString(i2));
        assertEquals("65    7", Instruction.toInstrString(i3));
    }

    
    // Build a 19 bit instruction from the test format
    int makeInstruction(String ibits) {
        return (makeOpcode(ibits)<<13) | makeAddress(ibits);
    }
    
    int makeOpcode(String ibits) {
        String[] ss = ibits.split("-");
        int op = packBits(ss[0]);
        return op;
    }
    
    int makeAddress(String ibits) {
        String[] ss = ibits.split("-");
        int ad = packBits(ss[1]);
        return ad;
    }

    int packBits(String s) {
        int result = 0;
        for (int i = 0; i < s.length(); i++) {
            result <<= 1;
            if (s.charAt(i) == '1') result |= 1;
        }
        return result;
    }
}
