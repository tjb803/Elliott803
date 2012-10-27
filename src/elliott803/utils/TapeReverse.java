/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2012
 */
package elliott803.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Utility program to copy and reverse a telecode tape.  This is mainly
 * used for Algol "owncode" tapes which are printed in reverse and needed
 * to be wound up backwards. 
 *
 * Usage:
 *    TapeReverse inputtape outputtape
 *
 * where:
 *    inputtape: the name of a telecode tape to be reversed
 *    outputtape: the name for the output tape file
 *
 * @author Baldwin
 */
public class TapeReverse {
    
    public static void main(String[] args) throws Exception {
        // Handle parameters
        Args.Map options = Args.optionMap();
        Args parms = new Args("TapeReverse", "inputtape outputtape", args, options);

        File inputFile = parms.getInputFile(1);
        File outputFile = parms.getOutputFile(2);

        // Check parameters
        if (inputFile == null || outputFile == null) {
            parms.usage();
        }

        // Read the input tape to a memory buffer
        InputStream input = new BufferedInputStream(new FileInputStream(inputFile));
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int ch = input.read();
        while (ch != -1) {
            buffer.write(ch);
            ch = input.read();
        }
        input.close();
        
        // And write it out backwards
        OutputStream output = new BufferedOutputStream(new FileOutputStream(outputFile));
        byte[] tape = buffer.toByteArray();
        for (int i = tape.length-1; i >= 0; i--) {
            output.write(tape[i]);
        }
        output.close();
    }
}
