/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2009
 */
package elliott803.telecode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

/**
 * Utility class to provide an input stream of telecode characters read by
 * converting Java characters from an underlying character Reader.  Java
 * characters are converted according to the rules in the CharToTelecode
 * converter class.
 *
 * All line ends in the source data are returned as the [CR] [LF] pair of
 * telecode characters.
 *
 * @author Baldwin
 */
public class TelecodeInputStream extends InputStream {

    BufferedReader inputReader;
    CharToTelecode converter;
    byte[] bb = new byte[2];
    char[] cc = new char[1];
    String line;
    int buffLen;
    int lineLen, linePos;

    // The reader needs to be wrapped in a BufferedReader (if it is not already
    // buffered) so complete lines can be read and the BufferedReader code can
    // handle correctly detecting line ends.

    public TelecodeInputStream(Reader in) {
        this(new BufferedReader(in));
    }

    public TelecodeInputStream(BufferedReader in) {
        inputReader = in;
        converter = new CharToTelecode();
    }

    /*
     * Override the required InputStream methods
     */

    public int read() throws IOException {
        int tc = -1;
        if (buffLen > 0) {
            tc = bb[1];
            buffLen -= 1;
        } else {
            if (line == null) {
                line = inputReader.readLine();
                if (line != null) {
                    lineLen = line.length();
                    linePos = 0;
                }
            }
            if (line != null) {
                while (buffLen == 0) {
                    if (linePos == lineLen) {
                        bb[0] = Telecode.TELE_CR;
                        bb[1] = Telecode.TELE_LF;
                        buffLen = 2;
                        line = null;
                    } else {
                        cc[0] = line.charAt(linePos++);
                        buffLen = converter.convert(cc, 1, bb);
                    }
                }
                tc = bb[0];
                buffLen -= 1;
            }
        }
        return tc;
    }

    public void close() throws IOException {
        inputReader.close();
    }
}
