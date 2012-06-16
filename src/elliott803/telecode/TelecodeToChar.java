/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2009
 */
package elliott803.telecode;

/**
 * Utility class to convert Elliott telecode characters to Java chars.
 *
 * All the telecode characters are present in standard US-ASCII except for the
 * figure-shift 26 (the GB-pound sign).  To simplify use outside the UK the converter
 * can be set to translate this character to '#'.
 *
 * @author Baldwin
 */
public class TelecodeToChar extends Telecode {

    boolean isFigure = false;
    boolean useASCII = false;


    public TelecodeToChar() {
        // Default converter uses the correct characters for output
    }

    public TelecodeToChar(boolean useASCII) {
        // Flag allows the use of only US-ASCII characters for output
        this.useASCII = useASCII;
    }

    /*
     * Force converter state to letter or figure shift
     */
    public void setLetterShift() {
        isFigure = false;
    }

    public void setFigureShift() {
        isFigure = true;
    }

    /*
     * Conversion routine.  Can be called to convert a sequence of telecode characters in the
     * input array to Java characters in the output array.  This can be called multiple times
     * for a single conversion and will remember the last known shift state.
     *
     * Output array is assumed to be be big enough - it will normally be smaller than the input
     * as shifts are removed.  Returns the number of characters output.
     */
    public int convert(byte[] input, int inputSize, char[] output) {
        int outputSize = 0;
        for (int i = 0; i < inputSize; i++) {
            int tc = input[i] & CHAR_MASK;
            if (tc == TELE_LS) {
                isFigure = false;
            } else if (tc == TELE_FS) {
                isFigure = true;
            } else {
                char ch = (isFigure) ? figureShift.charAt(tc) : letterShift.charAt(tc);
                if (ch == NUM && !useASCII) {
                    ch = GBP;
                }
                output[outputSize++] = ch;
            }
        }
        return outputSize;
    }
}
