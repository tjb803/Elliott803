/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2009
 */
package elliott803.telecode;

/**
 * Utility class to convert Java chars to Elliott telecode characters.
 *
 * We allow a certain amount of tolerance when converting for ease of use:
 * - accept both upper- and lowercase letters
 * - accept $ or & for figure-shift 5
 * - accept ' or ; for figure-shift 9
 * - accept ( or [ for figure-shift 17
 * - accept ) or ] for figure-shift 18
 * - accept ? or ! for figure-shift 20
 * - accept / or \ for figure-shift 23
 * - accept # or GB-pound sign for figure-shift 26
 * - accept _ for blank characters (ie character 0).
 * Any other characters are ignored.
 *
 * @author Baldwin
 */
public class CharToTelecode extends Telecode {

    static final String figureShift = "12*4$=78',+:-.%0()3?56/@9" + Character.toString(GBP);
    static final String figureAlt   = "    &   ;       [] !  \\  #";
    static final String letterUpper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    static final String letterLower = "abcdefghijklmnopqrstuvwxyz";

    boolean isFigure = false;
    boolean isFirst = true;

    /*
     * Reset shift status to unknown.  Will force a shift character to be written
     * before the next converted character.
     */
    public void resetShift() {
        isFirst = true;
    }

    /*
     * Conversion routine.  Can be called to convert a sequence of Java characters in the
     * input array to telecode characters in the output array.  This can be called multiple times
     * for a single conversion and will remember the last known shift state.
     *
     * Output array is assumed to be be big enough - may need to be at least twice the length
     * of the input array.  Returns the number of characters output.
     *
     * Any Java characters that cannot be converted are ignored.
     */
    public int convert(char[] input, int inputSize, byte[] output) {
        int outputSize = 0;
        for (int i = 0; i < inputSize; i++) {
            char ch = input[i];

            int li = isLetter(ch);
            int fi = isFigure(ch);
            int si = isSpecial(ch);

            if (li > 0) {
                if (isFirst || isFigure) {
                    output[outputSize++] = TELE_LS;
                    isFirst = false;  isFigure = false;
                }
                output[outputSize++] = (byte)li;
            } else if (fi > 0) {
                if (isFirst || !isFigure) {
                    output[outputSize++] = TELE_FS;
                    isFirst = false;  isFigure = true;
                }
                output[outputSize++] = (byte)fi;
            } else if (si >= 0) {
                output[outputSize++] = (byte)si;
            }
        }
        return outputSize;
    }

    private int isLetter(char ch) {
        int i = letterUpper.indexOf(ch);
        if (i == -1) {
            i = letterLower.indexOf(ch);
        }
        return i + 1;
    }

    private int isFigure(char ch) {
        int i = figureShift.indexOf(ch);
        if (i == -1 && ch != ' ') {
            i = figureAlt.indexOf(ch);
        }
        return i + 1;
    }

    private int isSpecial(char ch) {
        int i = -1;
        if (ch == 0) i = 0;
        else if (ch == '_') i = 0;
        else if (ch == ' ' || ch == '\t') i = TELE_SP;
        else if (ch == '\r') i = TELE_CR;
        else if (ch == '\n') i = TELE_LF;
        return i;
    }
}
