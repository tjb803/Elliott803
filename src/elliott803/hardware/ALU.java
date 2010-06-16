/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2009
 */
package elliott803.hardware;

import java.math.BigInteger;

import elliott803.machine.Computer;
import elliott803.machine.Word;

/**
 * This class is the Arithmetic and Logic unit.  It handles all the basic integer
 * arithmetic and logical operations.
 *
 * @author Baldwin
 */
public class ALU {

    public Computer computer;                 // The owning computer

    boolean overflow = false;                 // Overflow indicator
    long extension = 0;                       // Double length result extension

    public ALU(Computer computer) {
        this.computer = computer;
    }

    /*
     * Basic functions.  Sign extend the 39-bit value(s), perform the operation
     * and reduce back to 39 bits, checking for overflow.
     */

    public long add(long n1, long n2) {
        long result = Word.getLong(n1) + Word.getLong(n2);
        checkOverflow(result);
        return Word.asInteger(result);
    }

    public long sub(long n1, long n2) {
        long result = Word.getLong(n1) - Word.getLong(n2);
        checkOverflow(result);
        return Word.asInteger(result);
    }

    public long and(long n1, long n2) {
        long result = Word.getLong(n1) & Word.getLong(n2);
        checkOverflow(result);
        return Word.asInteger(result);
    }

    public long shr(long n1, int n) {
        // Right shift needs an unsigned value to work on as it needs to add zeros
        // at the most significant end.  Right shift cannot overflow.
        long result = Word.getBits(n1) >> n;
        return Word.asInteger(result);
    }

    public long shl(long n1, int n) {
        // Left shift needs a signed value to work on, so the overflow detection
        // will work correctly.
        long result = Word.getLong(n1);
        if (n < 26) {
            result <<= n;                  // Can do a fast shift if the result will
            checkOverflow(result);         // not exceed 64 bits.
        } else {
            while (n-- > 0) {              // Otherwise it is best to do the shift
                result <<= 1;              // in a loop to ensure overflow is
                checkOverflow(result);     // detected correctly.
            }
        }
        return Word.asInteger(result);
    }

    public long mul(long n1, long n2) {
        // Can do a fast multiply for small numbers otherwise, if the result could
        // overflow a 64 bit long, we have to use BigInteger arithmetic and extract
        // a single length result.
        long result;
        if (n1 == (int)n1 && n2 == (int)n2) {
            result = Word.getLong(n1) * Word.getLong(n2);
            checkOverflow(result);
        } else {
            BigInteger bigResult = makeBig(n1).multiply(makeBig(n2));
            overflow = bigResult.bitLength() > 38;
            result = bigResult.longValue();
        }
        return Word.asInteger(result);
    }

    public boolean isZero(long n) {
        return (Word.getLong(n) == 0);
    }

    public boolean isNeg(long n) {
        return (Word.getLong(n) < 0);
    }

    /*
     * Double length functions.  Operate on the 77 bit value formed by n1 and nx (nx is a 38 bit
     * value), with n1 forming the most significant bits.
     */

    public long longMul(long n1, long n2) {
        BigInteger result = makeBig(n1).multiply(makeBig(n2));
        overflow = (result.bitLength() > 76);
        return makeLong2(result);
    }

    public long longDiv(long n1, long nx, long n2) {
        long n = 0;
        if (n2 != 0) {
            BigInteger result = makeBig(n1, nx).divide(makeBig(n2));
            overflow = (result.bitLength() > 76);
            n = makeLong1(result);
        } else {    // Divide by zero
            overflow = true;
        }
        return n;
    }

    public long longShr(long n1, long nx, int n) {
        BigInteger result = makeBig(n1, nx).shiftRight(n);
        overflow = (result.bitLength() > 76);
        return makeLong2(result);
    }

    public long longShl(long n1, long nx, int n) {
        BigInteger result = makeBig(n1, nx).shiftLeft(n);
        overflow = (result.bitLength() > 76);
        return makeLong2(result);
    }

    /*
     * Return last overflow condition and double length extension value
     */

    public boolean isOverflow() {
        return overflow;
    }

    public long getExtension() {
        return extension;
    }

    // Convert a 39 bit or 77 bit value to a BigInteger
    BigInteger makeBig(long n) {
        return BigInteger.valueOf(Word.getLong(n));
    }

    BigInteger makeBig(long n, long nx) {
        return BigInteger.valueOf(Word.getLong(n)).shiftLeft(38).or(BigInteger.valueOf(nx));
    }

    // Convert a BigInteger to a 39 or 77 bit value
    long makeLong1(BigInteger n) {
        extension = 0;
        return (Word.asInteger(n.longValue()));
    }

    long makeLong2(BigInteger n) {
        extension = (Word.asExtension(n.longValue()));
        return (Word.asInteger(n.shiftRight(38).longValue()));
    }

    // Check for overflow.  The upper 25 bits of the Java long should be identical to bit
    // 39 (the sign bit).  In other words the top 26 bits must be all zeros or all ones.
    void checkOverflow(long n) {
        n = n & ~Word.INT_MASK;
        overflow = (n != 0 && n != ~Word.INT_MASK);
    }
}
