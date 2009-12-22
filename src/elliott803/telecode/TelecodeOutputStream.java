/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2009
 */
package elliott803.telecode;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Writer;

/**
 * Utility class to take a stream of telecode characters and write them as
 * Java characters using a Writer.  The telecode characters are converted
 * to Java characters according to the rules in the TelecodeToChar class.
 *
 * To handle line ends the telecode [CR] character is ignored and the telecode
 * [LF] is written as a line end.  This is not quite perfect but works well for
 * standard text data.
 *
 * @author Baldwin
 */
public class TelecodeOutputStream extends OutputStream {

    Writer outputWriter;
    TelecodeToChar converter;
    byte[] bb = new byte[1];
    char[] cc = new char[1];

    // The writer needs to be wrapped in a BufferedWriter (if it is not already
    // buffered) to allow the BufferedWriter code to correctly handle line ends.

    public TelecodeOutputStream(Writer out, boolean useASCII) {
        this(new BufferedWriter(out), useASCII);
    }

    public TelecodeOutputStream(BufferedWriter out, boolean useASCII) {
        outputWriter = out;
        converter = new TelecodeToChar(useASCII);
    }

    // Allow for a PrintWriter too, which unfortunately is not a subclass
    // of BufferedWriter but has similar line end handling.

    public TelecodeOutputStream(PrintWriter out, boolean useASCII) {
        outputWriter = out;
        converter = new TelecodeToChar(useASCII);
    }

    // Also allow for a PrintStream (typically System.out).

    public TelecodeOutputStream(PrintStream out, boolean useASCII) {
        this(new PrintWriter(new OutputStreamWriter(out), true), useASCII);
    }

    /*
     * Override the required OutputStream methods
     */

    public void write(int tc) throws IOException {
        if (tc == Telecode.TELE_LF) {
            if (outputWriter instanceof BufferedWriter)
                ((BufferedWriter)outputWriter).newLine();
            else
                ((PrintWriter)outputWriter).println();
        } else if (tc != Telecode.TELE_CR) {
            bb[0] = (byte)tc;
            if (converter.convert(bb, 1, cc) > 0)
                outputWriter.write(cc[0]);
        }
    }

    public void flush() throws IOException {
        outputWriter.flush();
    }

    public void close() throws IOException {
        outputWriter.close();
    }
}
