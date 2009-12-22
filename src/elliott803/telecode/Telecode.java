/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2009
 */
package elliott803.telecode;

/**
 * Various Elliott telecode constants
 *
 * @author Baldwin
 */
public abstract class Telecode {

    /*
     * 5 bit Elliott telecode consists of the following characters
     *
     *          LS      FS
     *  -----   -----   -----
     *  0       blank   blank       (blank tape)
     *  1       A       1
     *  2       B       2
     *  3       C       *           (asterisk)
     *  4       D       4
     *  5       E       $ (or &)    (dollar or ampersand)
     *  6       F       =           (equals)
     *  7       G       7
     *  8       H       8
     *  9       I       '           (apostrophe)
     * 10       J       ,           (comma)
     * 11       K       +           (plus)
     * 12       L       :           (colon)
     * 13       M       -           (minus)
     * 14       N       .           (period)
     * 15       O       %           (percent)
     * 16       P       0
     * 17       Q       (           (open bracket)
     * 18       R       )           (close bracket)
     * 19       S       3
     * 20       T       ?           (question mark)
     * 21       U       5
     * 22       V       6
     * 23       W       /           (slash)
     * 24       X       @           (at)
     * 25       Y       9
     * 26       Z       £           (GB-pound)
     * 27       FS      FS          (figure shift)
     * 28       Space   Space
     * 29       CR      CR          (carriage return)
     * 30       LF      LF          (line feed)
     * 31       LS      LS          (letter shift)
     */

    public static final byte TELE_FS = 27;
    public static final byte TELE_SP = 28;
    public static final byte TELE_CR = 29;
    public static final byte TELE_LF = 30;
    public static final byte TELE_LS = 31;

    public static final char GBP = '\u00a3';       // British pound sign

    public static final int CHAR_MASK = 0x1F;      // 5 bits

    /*
     * Simple translation from telecode to Java characters.  For more sophisticated
     * translation use the TelecodeToChar/CharToTelecode classes, or one of the Telecode
     * input/output streams.
     *
     * Note: this simple translation always uses '#' for GB-pound sign
     */

    static final String letterShift = "\u0000ABCDEFGHIJKLMNOPQRSTUVWXYZ\u0000 \r\n\u0000";
    static final String figureShift = "\u000012*4$=78',+:-.%0()3?56/@9#\u0000 \r\n\u0000";

    public static char asLetter(int tc) {
        return letterShift.charAt(tc & CHAR_MASK);
    }

    public static char asFigure(int tc) {
        return figureShift.charAt(tc & CHAR_MASK);
    }
}
