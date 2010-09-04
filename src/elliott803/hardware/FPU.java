/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2009
 */
package elliott803.hardware;

import elliott803.machine.Computer;
import elliott803.machine.Word;

/**
 * This class is the Floating Point Unit.  This handles all floating point arithmetic
 * functions.
 *
 * This doesn't simulate the exact floating point operation of the original machine, it
 * simply converts the Elliott FP bit pattern to a Java doubles, performs the operation
 * in Java and converts back to the Elliott bit pattern.
 *
 * @author Baldwin
 */
public class FPU  {

    public Computer computer;               // The owning computer

    boolean overflow = false;               // Overflow indicators
    boolean fpOverflow = false;           

    public FPU(Computer computer) {
        this.computer = computer;
    }

    /*
     * Floating point operations
     */

    public long add(long n1, long n2) {
        double result = Word.getDouble(n1) + Word.getDouble(n2);
        return makeFloat(result);
    }

    public long sub(long n1, long n2) {
        double result = Word.getDouble(n1) - Word.getDouble(n2);
        return makeFloat(result);
    }

    public long mul(long n1, long n2) {
        double result = Word.getDouble(n1) * Word.getDouble(n2);
        return makeFloat(result);
    }

    public long div(long n1, long n2) {
        double result = 0;
        if (n2 != 0) {
            result = Word.getDouble(n1) / Word.getDouble(n2);
        } else {    // Divide by zero
            overflow = fpOverflow = true;
        }
        return makeFloat(result);
    }

    public long convert(long n) {
        return makeFloat((double)Word.getLong(n));
    }

    // End-around left shift.
    public long shl(long n1, int n) {
        return Word.asInteger((n1<<n) | (n1>>(39-n)));
    }

    // Short (integer) divide
    public long sdiv(long n1, long n2) {
        long result = 0;
        if (n2 != 0)
            result = Word.getLong(n1) / Word.getLong(n2);
        return Word.asInteger(result);
    }

    // Short (integer) square root
    public long sqrt(long n) {
        long result = (long)Math.sqrt(Word.getLong(n));
        return Word.asInteger(result);
    }

    /*
     * Return overflow states
     */
    public boolean isOverflow() {
        return overflow;
    }
    
    public boolean isFpOverflow() {
        return fpOverflow;
    }

    // Make a floating point result value, checking for overflow
    long makeFloat(double value) {
        long result = Word.asFloat(value);
        overflow = fpOverflow = false;
        if (result == Word.NOTHING) {
            fpOverflow = true;
            result = 0;
        }
        return result;
    }
}
