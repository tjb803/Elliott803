/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2012
 */
package elliott803.telecode;

import java.util.StringTokenizer;

import junit.framework.TestCase;

/**
 * Base class for telecode tests
 * 
 * @author Baldwin
 */
public abstract class BaseTelecodeTest extends TestCase {

    // Convert a string of telecode values to a byte[] 
    protected byte[] parseTelecode(String input) {
        StringTokenizer t = new StringTokenizer(input, " ,");
        byte[] output = new byte[t.countTokens()];
        for (int i = 0; i < output.length; i++) {
            String ch = t.nextToken();
            if (ch.equalsIgnoreCase("LS"))
                output[i] = Telecode.TELE_LS;
            else if (ch.equalsIgnoreCase("FS"))
                output[i] = Telecode.TELE_FS;
            else if (ch.equalsIgnoreCase("CR"))
                output[i] = Telecode.TELE_CR;
            else if (ch.equalsIgnoreCase("LF"))
                output[i] = Telecode.TELE_LF;
            else 
                output[i] = Byte.parseByte(ch);
        }
        return output;
    }
}
