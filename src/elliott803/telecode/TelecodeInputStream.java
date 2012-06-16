/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2009, 2012
 */
package elliott803.telecode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;

/**
 * Utility class to provide an input stream of telecode characters read by
 * converting Java characters from an underlying character Reader.  Java
 * characters are converted according to the rules in the CharToTelecode
 * converter class.
 *
 * All line ends in the source data are returned as the [CR] [LF] pair of
 * telecode characters.  A Java line end is defined as either a single '\r', 
 * a single '\n' or the pair '\r\n' (which are the same rules used by the 
 * Java BufferedReader class).
 *
 * @author Baldwin
 */
public class TelecodeInputStream extends InputStream {

    BufferedReader inputReader;
    CharToTelecode converter;
    byte[] bb = new byte[2];
    char[] cc = new char[1];
    int bbLen, bbPos;
    boolean skipLF;

    // The reader is wrapped in a BufferedReader (if it is not already
    // buffered) to improve performance.

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
        while (bbLen == 0) {
            if (inputReader.read(cc) > 0) {
                if (cc[0] == '\r' || cc[0] == '\n') {
                    if (cc[0] == '\r' || !skipLF) {
                        bb[0] = Telecode.TELE_CR;
                        bb[1] = Telecode.TELE_LF;
                        bbLen = 2;
                        bbPos = 0;
                    }    
                    skipLF = (cc[0] == '\r');
                } else {
                    bbLen = converter.convert(cc, 1, bb);
                    bbPos = 0;
                    skipLF = false;
                }
            } else {
                break;
            }
        } 
        if (bbLen > 0) {
            tc = bb[bbPos++];
            bbLen -= 1;
        } 
        return tc;
    }

    public void close() throws IOException {
        inputReader.close();
    }
    
    /*
     * Extra useful methods
     */
    
    public void write(OutputStream output) throws IOException {
        for (int ch = read(); ch != -1; ch = read())
            output.write(ch);
        close();
    }
}
