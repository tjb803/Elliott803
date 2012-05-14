/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2009
 */
package elliott803.machine;

import java.util.StringTokenizer;

/**
 * Constants and utility functions to manipulate the 39-bit words.
 *
 * @author Baldwin
 */
public abstract class Word {

    public static final long NOTHING = -1;                  // Special non-Elliott marker value

    public static final long CARRY_BIT = 0x8000000000L;     // Arithmetic carry bit
    public static final long SIGN_BIT  = 0x4000000000L;     // Sign bit

    public static final long WORD_MASK = 0x7FFFFFFFFFL;     // 39 bits (full word)
    public static final long INT_MASK  = 0x3FFFFFFFFFL;     // 38 bits (signed integer)
    public static final long CHAR_MASK = 0x1F;              // 5 bits (character)

    /*
     * Handle word as unsigned 39 bits
     */
    public static final long getBits(long word) {
        return (word & WORD_MASK);
    }

    public static final long asWord(long word) {
        return (word & WORD_MASK);
    }

    public static final String toOctalString(long word) {
        StringBuilder result = new StringBuilder();
        String text = Long.toOctalString(getBits(word));
        result.append("0000000000000", 0, 13-text.length()).append(text);
        return result.toString();
    }

    public static final String toBinaryString(long word) {
        StringBuilder result = new StringBuilder();
        String text = Long.toBinaryString(getBits(word));
        result.append("000000000000000000000000000000000000000", 0, 39-text.length()).append(text);
        return result.toString();
    }

    public static final String toBin38String(long word) {
        StringBuilder result = new StringBuilder(toBinaryString(word));
        result.setCharAt(0, ' ');
        return result.toString();
    }

    /*
     * Handle word as a pair of instructions
     */
    public static final int getInstr1(long word) {
        return ((int)(word >> 20) & Instruction.INSTR_MASK);
    }

    public static final int getInstr2(long word) {
        return ((int)(word) & Instruction.INSTR_MASK);
    }

    public static final int getB(long word) {
        return ((int)(word >> 19) & 0x1);
    }

    public static final long asInstr1(int instr) {
        return ((long)(instr & Instruction.INSTR_MASK) << 20);
    }

    public static final long asInstr2(int instr) {
        return ((long)(instr & Instruction.INSTR_MASK));
    }

    public static final long asB(int b) {
        return ((long)(b & 0x1) << 19);
    }

    public static final long asInstr(int instr1, int b, int instr2) {
        return asInstr1(instr1) | asB(b) | asInstr2(instr2);
    }

    public static final String toInstrString(long word) {
        StringBuilder result = new StringBuilder();
        result.append(Instruction.toInstrString(getInstr1(word)));
        result.append((getB(word) == 0) ? " : " : " / ");
        result.append(Instruction.toInstrString(getInstr2(word)));
        return result.toString();
    }
    
    public static final long parseInstr(String s) {
        int instr1 = 0, instr2 = 0, b = 0;
        StringTokenizer t = new StringTokenizer(s, ":/", true);
        if (t.hasMoreTokens())
            instr1 = Instruction.parseInstr(t.nextToken());
        if (t.hasMoreTokens())
            b = t.nextToken().equals("/") ? 1 : 0;
        if (t.hasMoreTokens())
            instr2 = Instruction.parseInstr(t.nextToken());
        return asInstr(instr1, b, instr2);
    }

    /*
     * Handle word as a signed integer
     */
    public static final long getLong(long word) {
        return ((word & SIGN_BIT) == 0) ? (word & INT_MASK) : (word | ~INT_MASK);
    }

    public static final long asInteger(long value) {
        return (value & WORD_MASK);
    }

    public static final long asExtension(long value) {
        return (value & INT_MASK);      // 38 bits (used for auxiliary register extension)
    }

    public static final String toIntegerString(long word) {
        StringBuilder result = new StringBuilder();
        long value = getLong(word);
        String text = Long.toString(getLong(word));
        result.append((value > 0) ? "+" : "").append(text);
        return result.toString();
    }

    /*
     * Handle word as floating point
     */

    /*
     * Elliott 39-bit floating point format:
     *   value is a*2^b
     *   mantissa a is upper 30-bit 2's complement value representing 0.5<=a<1, -1<=a<-0.5
     *   exponent b is lower 9-bit positive integer 0 to 511 representing b+256
     *   zero is represented as all 0 bits.
     *
     * Java doubles are IEEE 64-bit format
     *   value is  s*a*2^b
     *   sign s is top bit, 0 = positive, 1 = negative
     *   exponent b is next 11-bit positive integer 0 to 2048 representing b+1023
     *   mantissa a is the bottom 52-bits representing 1<=a<2
     *     it is actually a 53-bit value with a hidden first bit that is always 1 unless the
     *     exponent is zero, in which case the hidden bit is 0.
     */

    // Note: Conversion from Elliott form to Java double must always succeed, but conversion
    // the other way can fail, since the range of Java doubles is larger than the 39 bit
    // Elliott values.  If there is a failure the special but pattern -1 (all ones) will be
    // used and the FPU will detect this and flag a floating point overflow.

    static final int FP_FRAC_MASK = 0x3FFFFFFF;
    static final int FP_EXP_MASK = 0x1FF;
    static final int FP_FRAC_SIGN = 0x20000000;
    static final int FP_FRAC_BITS = 0x1FFFFFFF;
    static final int IEEE_EXP_MASK = 0x7FF;
    static final long IEEE_FRAC_MASK = 0xFFFFFFFFFFFFFL;
    static final long IEEE_HIDDEN_BIT = 0x10000000000000L;
    static final long IEEE_TOP_BITS = 0x30000000000000L;

    public static final double getDouble(long value) {
        double result= 0;
        if (value != 0) {
            // Sign extend the 39 bits and extract the Elliott 30-bit mantissa and 9-bit exponent
            // as signed ints.
            value = Word.getLong(value);
            int em = (int)(value >> 9);
            int ee = (int)(value & FP_EXP_MASK) - 256;

            // Build the s, a and b values for the IEEE double.  Start with the sign and
            // if it is negative take the two's complement of the mantissa.
            long s = (em > 0) ? 0 : 1;
            if (em < 0)
                em = -em;

            // Now shift the mantissa left until there is a one in the sign bit location.
            // This will become the hidden 1 bit in the IEEE representation, so we take the
            // remaining 29 bits and put them in the top 52 bits of the IEEE fraction.  Each
            // shift left is a double so we also need to decrease the exponent on each shift.
            while ((em & FP_FRAC_SIGN) == 0) {
                em <<= 1;
                ee -= 1;
            }
            long a = (long)(em & FP_FRAC_BITS) << 23;
            long b = (long)((ee + 1023) & IEEE_EXP_MASK);

            result = Double.longBitsToDouble((s<<63) | (b<<52) | (a));
        }
        return result;
    }

    public static final long asFloat(double value) {
        long result = 0;
        if (value != 0) {
            // Extract the sign, exponent and fraction values, adding in the extra hidden
            // bit at bit 53 of the fraction.  Don't need to worry about non-normal form
            // (where the hidden bit is 0) as these are too small and will underflow anyway.
            long bits = Double.doubleToLongBits(value);
            int ds = (int)(bits >>> 63);
            int de = ((int)(bits >>> 52) & IEEE_EXP_MASK) - 1023;
            long df = (bits & IEEE_FRAC_MASK) | IEEE_HIDDEN_BIT;

            // Build the a and b values for the Elliott float.  For negative values we need a two's
            // complement mantissa.
            if (ds != 0)
                df = -df;

            // df now contains a 54 bit sign-extended fraction.  This needs to be normalized
            // by shifting left so that the sign bit and the first fraction bit are different.
            // Each left shift multiplies the fraction by two, so the exponent is decreased.
            while ((df & IEEE_TOP_BITS) == 0 || (df & IEEE_TOP_BITS) == IEEE_TOP_BITS) {
                df <<= 1;
                de -= 1;
            }
            long a = (df >> 24) & FP_FRAC_MASK;
            int b = de + 257;       // Don't mask this as we need to check for over- and underflow

            // Set the result, checking for overflow if the final exponent needs more
            // than 9 bits or underflow if the value is too small to represent.
            if (b > 511) {
                result = Word.NOTHING;          // Overflow
            } else if (b < 0) {
                result = 0;                     // Underflow
            } else {
                result = ((a<<9) | (b));
            }
        }
        return result;
    }

    public static final String toFloatString(long value) {
        return Double.toString(getDouble(value));
    }
}
