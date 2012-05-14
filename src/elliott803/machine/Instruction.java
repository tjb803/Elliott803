/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2009
 */
package elliott803.machine;

import java.util.StringTokenizer;

/**
 * Constants and utility functions to manipulate a single instruction
 *
 * @author Baldwin
 */
public abstract class Instruction {

    public static final int OP_BITS    = 0x3F;
    public static final int ADDR_BITS  = 0x1FFF;

    public static final int INSTR_MASK = 0x7FFFF;           // 19 bits
    public static final int OP_MASK = asOp(OP_BITS);
    public static final int ADDR_MASK = asAddr(ADDR_BITS);

    /*
     * Set/extract the op code and addr parts of the instruction
     */
    public static final int asOp(int op) {
        return ((op & OP_BITS) << 13);
    }

    public static final int asAddr(int addr) {
        return (addr & ADDR_BITS);
    }

    public static final int asInstr(int op, int addr) {
        return asOp(op) | asAddr(addr);
    }

    public static final int getOp(int instr) {
        return ((instr >> 13) & OP_BITS);
    }

    public static final int getAddr(int instr) {
        return (instr & ADDR_BITS);
    }

    /*
     * Format an instruction as a String in the for "oo nnnn"
     * where "oo" is the opcode as 2 octal digits
     *       "nnnn" is the address in decimal left-space padded to 4 characters
     */
    public static final String toOpString(int op) {
        StringBuilder result = new StringBuilder();
        String text = Integer.toOctalString(op);
        result.append("00", 0, 2-text.length()).append(text);
        return result.toString();
    }

    public static final String toAddrString(int addr) {
        StringBuilder result = new StringBuilder();
        String text = Integer.toString(addr);
        result.append("    ", 0, 4-text.length()).append(text);
        return result.toString();
    }

    public static final String toInstrString(int instr) {
        StringBuilder result = new StringBuilder();
        String text1 = Integer.toOctalString(getOp(instr));
        String text2 = Integer.toString(getAddr(instr));
        result.append("00", 0, 2-text1.length()).append(text1);
        result.append("     ", 0, 5-text2.length()).append(text2);
        return result.toString();
    }
    
    /*
     * Parse the "oo nnnn" String form to an integer.
     */
    public static final int parseInstr(String s) {
        int op = 0, addr = 0;
        StringTokenizer t = new StringTokenizer(s, " ");
        if (t.hasMoreTokens())
            op = Integer.parseInt(t.nextToken(), 8);
        if (t.hasMoreTokens())
            addr = Integer.parseInt(t.nextToken());
        return asInstr(op, addr);
    }
}
