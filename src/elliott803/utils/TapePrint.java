/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2009
 */
package elliott803.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import elliott803.telecode.TelecodeOutputStream;

/**
 * Utility program to take a telecode tape and print or write a text file representation
 *
 * Usage:
 *    TapePrint [options] inputtape [outputfile]
 *
 * where:
 *    inputtape: the name of the telecode tape to be printed
 *    outputfile: the name for the output file, defaults to standard output
 *
 * options:
 *    -ascii: print using only US-ASCII characters
 *    -encoding encoding_name: the encoding of the output file, defaults to the
 *                             standard platform encoding
 *
 * When printing with the '-ascii' option the only character affected is the telecode figure-shift
 * character 26 which is supposed to be the GB-pound sign; this will be printed as a '#'.
 *
 * @author Baldwin
 */
public class TapePrint {

    public static void main(String[] args) throws Exception {
        // Handle parameters
        Args.Map options = Args.optionMap();
        options.put("ascii", null);
        options.put("encoding", "outputencoding");
        Args parms = new Args("TapePrint", "inputtape [outputfile]", args, options);

        File inputFile = parms.getInputFile(1);
        File outputFile = parms.getOutputFile(2);
        String encoding = parms.getOption("encoding");
        boolean useASCII = parms.getFlag("ascii");

        // Check parameters
        if (inputFile == null) {
            parms.usage();
        }

        // Create input/output streams/readers
        FileInputStream input = new FileInputStream(inputFile);
        TelecodeOutputStream output = null;
        if (outputFile != null) {
            if (encoding == null)
                output = new TelecodeOutputStream(new PrintWriter(outputFile), useASCII);
            else
                output = new TelecodeOutputStream(new PrintWriter(outputFile, encoding), useASCII);
        } else {
            if (encoding == null)
                output = new TelecodeOutputStream(System.out, useASCII);
            else 
                output = new TelecodeOutputStream(new OutputStreamWriter(System.out, encoding), useASCII);
        }

        // Print the input to the output
        output.write(input);
        output.close();
    }
}
