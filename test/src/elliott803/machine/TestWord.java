/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2009
 */
package elliott803.machine;

import junit.framework.TestCase;

/**
 * JUnit tests for the Word class.
 * 
 * Note: doesn't test floating point conversions - see TestFloatWord class.
 * 
 * @author Baldwin
 */
public class TestWord extends TestCase {

    static String BITS_0  = "000000000000000000000000000000000000000";
    static String BITS_1  = "111111111111111111111111111111111111111";
    static String BITS_16 = "000000000000000000000001111111111111111";
    static String BITS_38 = "011111111111111111111111111111111111111";
    
    static long W0s = 0;
    static long W1s = -1;
    static long W65535 = 65535;
    
    static String I1 = "111111-1111111111111-1-111111-1111111111111";
    static String I2 = "110101-0000000000111-0-100000-0000010000000";
    
    /*
     * Tests for unsigned 39-bit word operations
     */
    public void testGetBits() throws Exception {
        long w1 = makeWord(BITS_0);
        long w2 = makeWord(BITS_1);
        long w3 = makeWord(BITS_16);
        assertEquals(w1, Word.getBits(W0s));
        assertEquals(w2, Word.getBits(W1s));
        assertEquals(w3, Word.getBits(W65535));
    }
    
    public void testAsWord() throws Exception {
        long w1 = makeWord(BITS_0);
        long w2 = makeWord(BITS_1);
        long w3 = makeWord(BITS_16);
        assertEquals(w1, Word.asWord(W0s));
        assertEquals(w2, Word.asWord(W1s));
        assertEquals(w3, Word.asWord(W65535));
    }
    
    public void testToOctalString() throws Exception {
        long w1 = makeWord(BITS_0);
        long w2 = makeWord(BITS_1);
        long w3 = makeWord(BITS_16);
        assertEquals("0000000000000", Word.toOctalString(w1));
        assertEquals("7777777777777", Word.toOctalString(w2));
        assertEquals("0000000177777", Word.toOctalString(w3));
    }
    
    public void testToBinaryString() throws Exception {
        long w1 = makeWord(BITS_0);
        long w2 = makeWord(BITS_1);
        long w3 = makeWord(BITS_16);
        assertEquals("000000000000000000000000000000000000000", Word.toBinaryString(w1));
        assertEquals("111111111111111111111111111111111111111", Word.toBinaryString(w2));
        assertEquals("000000000000000000000001111111111111111", Word.toBinaryString(w3));
    }
    
    public void testToBin38tring() throws Exception {
        long w1 = makeWord(BITS_0);
        long w2 = makeWord(BITS_1);
        long w3 = makeWord(BITS_16);
        assertEquals(" 00000000000000000000000000000000000000", Word.toBin38String(w1));
        assertEquals(" 11111111111111111111111111111111111111", Word.toBin38String(w2));
        assertEquals(" 00000000000000000000001111111111111111", Word.toBin38String(w3));
    }
    
    /*
     * Tests for signed integer operations 
     */
    public void testGetLong() throws Exception {
        long w1 = makeWord(BITS_0);
        long w2 = makeWord(BITS_1);
        long w3 = makeWord(BITS_16);
        assertEquals(0, Word.getLong(w1));
        assertEquals(-1, Word.getLong(w2));
        assertEquals(65535, Word.getLong(w3));
    }
    
    public void testAsInteger() throws Exception {
        long w1 = makeWord(BITS_0);
        long w2 = makeWord(BITS_1);
        long w3 = makeWord(BITS_16);
        assertEquals(w1, Word.asWord(W0s));
        assertEquals(w2, Word.asWord(W1s));
        assertEquals(w3, Word.asWord(W65535));
    }
    
    public void testAsExtension() throws Exception {
        long w1 = makeWord(BITS_38);
        assertEquals(w1, Word.asExtension(W1s));
    }
    
    public void testToIntegerString() throws Exception {
        long w1 = makeWord(BITS_0);
        long w2 = makeWord(BITS_1);
        long w3 = makeWord(BITS_16);
        assertEquals("0", Word.toIntegerString(w1));
        assertEquals("-1", Word.toIntegerString(w2));
        assertEquals("+65535", Word.toIntegerString(w3));
    }
    
    /*
     * Tests for instruction pairs
     */
    public void testGetInstr() throws Exception {
        long w1 = makePair(I1);
        long w2 = makePair(I2);
        int i1 = (077<<13) + 8191;
        int i2 = (065<<13) + 7;
        int i3 = (040<<13) + 128;
        assertEquals(i1, Word.getInstr1(w1));
        assertEquals(1, Word.getB(w1));
        assertEquals(i1, Word.getInstr2(w1));
        assertEquals(i2, Word.getInstr1(w2));
        assertEquals(0, Word.getB(w2));
        assertEquals(i3, Word.getInstr2(w2));
    }
    
    public void testAsInstr() throws Exception {
        long w1 = makePair(I1);
        long w2 = makePair(I2);
        int i1 = (077<<13) + 8191;
        int i2 = (065<<13) + 7;
        int i3 = (040<<13) + 128;
        assertEquals(w1, Word.asInstr(i1, 1, i1));
        assertEquals(w2, Word.asInstr(i2, 0, i3));
    }
    
    public void testToInstrString() throws Exception {
        long w1 = makePair(I1);
        long w2 = makePair(I2);
        assertEquals("77 8191 / 77 8191", Word.toInstrString(w1));
        assertEquals("65    7 : 40  128", Word.toInstrString(w2));
    }
    
    
    // Build a 39 bit word from the test format
    long makeWord(String wbits) {
        return packBits(wbits);
    }
    
    long makePair(String ibits) {
        String[] ss = ibits.split("-");
        long op1 = packBits(ss[0]);
        long ad1 = packBits(ss[1]);
        long b = packBits(ss[2]);
        long op2 = packBits(ss[3]);
        long ad2 = packBits(ss[4]);
        return (op1<<33) | (ad1<<20) | (b<<19) | (op2<<13) | ad2;
    }
    
    long packBits(String s) {
        long result = 0;
        for (int i = 0; i < s.length(); i++) {
            result <<= 1;
            if (s.charAt(i) == '1') result |= 1;
        }
        return result;
    }
}
