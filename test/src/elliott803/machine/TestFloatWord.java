/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2009
 */
package elliott803.machine;

import junit.framework.TestCase;

/**
 * JUnit tests for the Word class floating point conversions
 *
 * @author Baldwin
 */
public class TestFloatWord extends TestCase {

    // Some standard normalised floating point values that should convert both ways
    static String[][] normalfp = {
            // Elliott 39 bit form                       // IEEE 64 bit form
         { "010000000000000000000000000000-100000001", "0-01111111111-0000000000000000000000000000000000000000000000000000" }, // 1
         { "010000000000000000000000000000-100000010", "0-10000000000-0000000000000000000000000000000000000000000000000000" }, // 2
         { "011000000000000000000000000000-100000010", "0-10000000000-1000000000000000000000000000000000000000000000000000" }, // 3
         { "010000000000000000000000000000-100000011", "0-10000000001-0000000000000000000000000000000000000000000000000000" }, // 4
         { "010100000000000000000000000000-100000011", "0-10000000001-0100000000000000000000000000000000000000000000000000" }, // 5
         { "011100000000000000000000000000-100000011", "0-10000000001-1100000000000000000000000000000000000000000000000000" }, // 7
         { "010000000000000000000000000000-100000000", "0-01111111110-0000000000000000000000000000000000000000000000000000" }, // 1/2
         { "010101010101010101010101010101-011111111", "0-01111111101-0101010101010101010101010101000000000000000000000000" }, // 1/3
         { "010000000000000000000000000000-011111111", "0-01111111101-0000000000000000000000000000000000000000000000000000" }, // 1/4
         { "011001100110011001100110011001-011111110", "0-01111111100-1001100110011001100110011001000000000000000000000000" }, // 1/5
         { "011111111111111111111111111111-011111111", "0-01111111101-1111111111111111111111111111000000000000000000000000" }, // just <1/2
         { "100000000000000000000000000000-100000000", "1-01111111111-0000000000000000000000000000000000000000000000000000" }, // -1
         { "100000000000000000000000000000-100000001", "1-10000000000-0000000000000000000000000000000000000000000000000000" }, // -2
         { "101000000000000000000000000000-100000010", "1-10000000000-1000000000000000000000000000000000000000000000000000" }, // -3
         { "100000000000000000000000000000-100000010", "1-10000000001-0000000000000000000000000000000000000000000000000000" }, // -4
         { "101100000000000000000000000000-100000011", "1-10000000001-0100000000000000000000000000000000000000000000000000" }, // -5
         { "100100000000000000000000000000-100000011", "1-10000000001-1100000000000000000000000000000000000000000000000000" }, // -7
         { "100000000000000000000000000000-011111111", "1-01111111110-0000000000000000000000000000000000000000000000000000" }, // -1/2
         { "101010101010101010101010101010-011111111", "1-01111111101-0101010101010101010101010110000000000000000000000000" }, // -1/3
         { "100000000000000000000000000000-011111110", "1-01111111101-0000000000000000000000000000000000000000000000000000" }, // -1/4
         { "100110011001100110011001100110-011111110", "1-01111111100-1001100110011001100110011010000000000000000000000000" }, // -1/5
         { "101111111111111111111111111111-100000000", "1-01111111110-0000000000000000000000000001000000000000000000000000" }, // just <-1/2
     };

    // These are non-normalised Elliott values that should still convert to IEEE form
    static String[][] nonnormalfp = {
        { "000000000000000000000000000000-000000000", "0-00000000000-0000000000000000000000000000000000000000000000000000" }, // 0
        { "000000000000000000000000000000-100000000", "0-00000000000-0000000000000000000000000000000000000000000000000000" }, // Also 0
        { "001010101010101010101010101010-100000000", "0-01111111101-0101010101010101010101010100000000000000000000000000" }, // 1/3 (not normalized)
        { "110101010101010101010101010101-100000000", "1-01111111101-0101010101010101010101010110000000000000000000000000" }, // -1/3 (not normalized)
    };

    // These are valid additional Java doubles that should convert correctly
    static String[][] doublefp = {
        { "000000000000000000000000000000-000000000", "0-00000000000-0000000000000000000000000000000000000000000000000000" }, // +0
        { "000000000000000000000000000000-000000000", "1-00000000000-0000000000000000000000000000000000000000000000000000" }, // -0
    };

    // Java doubles that are too small to represent in Elliott form
    static String[] underflowfp = {
        "0-00000000000-0011110000000000000000000000000000000000000000000000", // Non-normal (+ve)
        "1-00000000000-0010000011111000000000000000000000000000000000000000", // Non-normal (-ve)
        "0-01011111101-1000000000000000000000000000000000000000000000000000", // Too small (+ve)
        "1-01011111101-1000000000000000000000000000000000000000000000000000", // Too small (+ve)
    };

    // Java double that are invalid or too large to represent in Elliott form
    static String[] overflowfp = {
        "0-11111111111-0000000000000000000000000000000000000000000000000000", // +Infinity
        "1-11111111111-0000000000000000000000000000000000000000000000000000", // +Infinity
        "0-11111111111-1000000000000000000000000000000000000000000000000000", // NaN
        "0-11111111111-1100000000000000000000000000000000000000000000000000", // NaN
        "1-11111111111-1000000000000000000000000000000000000000000000000000", // -NaN
        "1-11111111111-1100000000000000000000000000000000000000000000000000", // -NaN
        "0-10100000000-1000000000000000000000000000000000000000000000000000", // Too big (+ve)
        "1-10100000000-1000000000000000000000000000000000000000000000000000", // Too big (-ve)
    };

    /*
     * Test conversion from Elliott to Java doubles
     */
    public void testGetDouble() throws Exception {
        // Check some standard normalised values first
        System.out.println("getDouble(normalized)");
        for (int i = 0; i < normalfp.length; i++) {
            long e = makeElliott(normalfp[i][0]);
            double d = Word.getDouble(e);
            System.out.println(printElliott(e) + "   "  + printDouble(d) + "   " + d);
            assertEquals(normalfp[i][1], printDouble(d));
        }

        // Also check non-normalised Elliott values convert (not sure if these should
        // ever actually occur, but just in case check they convert correctly!).
        // Check some standard normalised values first
        System.out.println("getDouble(non-normalized)");
        for (int i = 0; i < nonnormalfp.length; i++) {
            long e = makeElliott(nonnormalfp[i][0]);
            double d = Word.getDouble(e);
            System.out.println(printElliott(e) + "   "  + printDouble(d) + "   " + d);
            assertEquals(nonnormalfp[i][1], printDouble(d));
        }
    }

    /*
     * Test conversion from Java doubles to Elliott
     */
    public void testAsFloat() throws Exception {
        // Check the standard normalised values first
        System.out.println("asFloat(normalized)");
        for (int i = 0; i < normalfp.length; i++) {
            double d = makeDouble(normalfp[i][1]);
            long e = Word.asFloat(d);
            System.out.println(printElliott(e) + "   " + printDouble(d) + "   " + d);
            assertEquals(normalfp[i][0], printElliott(e));
        }

        // Check additional values that convert correctly
        System.out.println("asFloat(additional)");
        for (int i = 0; i < doublefp.length; i++) {
            double d = makeDouble(doublefp[i][1]);
            long e = Word.asFloat(d);
            System.out.println(printElliott(e) + "   " + printDouble(d) + "   " + d);
            assertEquals(doublefp[i][0], printElliott(e));
        }
    }

    // Underflow should convert to 0
    public void testAsFloatUnderflow() throws Exception {
        System.out.println("asFloat(underflow)");
        for (int i = 0; i < underflowfp.length; i++) {
            double d = makeDouble(underflowfp[i]);
            long e = Word.asFloat(d);
            System.out.println(e + "   " + printDouble(d) + "   " + d);
            assertEquals(0, e);
        }
    }

    // Overflow should produce an error
    public void testAsFloatOverflow() throws Exception {
        System.out.println("asFloat(overflow)");
        for (int i = 0; i < overflowfp.length; i++) {
            double d = makeDouble(overflowfp[i]);
            long e = Word.asFloat(d);
            System.out.println(e + "   " + printDouble(d) + "   " + d);
            assertEquals(Word.NOTHING, e);
        }
    }
    
    // Build floating point values from string representation
    long makeElliott(String sbits) {
        String[] ss = sbits.split("-");
        long m = packBits(ss[0]);
        long e = packBits(ss[1]);
        return (m<<9) | e;
    }

    double makeDouble(String sbits) {
        String[] ss = sbits.split("-");
        long s = packBits(ss[0]);
        long e = packBits(ss[1]);
        long f = packBits(ss[2]);
        return Double.longBitsToDouble((s<<63) | (e<<52) | f);
    }

    long packBits(String s) {
        long result = 0;
        for (int i = 0; i < s.length(); i++) {
            result <<= 1;
            if (s.charAt(i) == '1') result |= 1;
        }
        return result;
    }

    // Build string representation from floating point values
    String printElliott(long e) {
        String em = unpackbits(e >> 9, 30);
        String ee = unpackbits(e & 0x1FF, 9);
        return em + "-" + ee;
    }

    String printDouble(double d) {
        long bits = Double.doubleToLongBits(d);
        String s = unpackbits(bits >> 63, 1);
        String e = unpackbits((bits >> 52) & 0x7FF, 11);
        String f = unpackbits(bits & 0xFFFFFFFFFFFFFL, 52);
        return s + "-" + e + "-" + f;
    }

    String unpackbits(long bits, int size) {
        String result = "";
        for (int i = 0; i < size; i++) {
            result = (((bits & 1) != 0) ? "1" : "0") + result;
            bits >>= 1;
        }
        return result;
    }
}
