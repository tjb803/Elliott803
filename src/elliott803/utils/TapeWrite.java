/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2009
 */
package elliott803.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.Map;

import elliott803.telecode.TelecodeInputStream;

/**
 * Utility program to take an input text file and write an equivalent telecode tape.
 *
 * Usage:
 *    TapeWrite [options] inputfile outputtape
 *
 * where:
 *    inputfile: the name of the text input file
 *    outputtape: the name for the output tape
 *
 * options:
 *    -encoding encoding_name: the encoding of the input file, defaults to the
 *                             standard platform encoding
 *
 * @author Baldwin
 */
public class TapeWrite {

    public static void main(String[] args) throws Exception {
        // Handle parameters
        Map<String,String> options = Args.optionMap();
        options.put("encoding", "inputencoding");
        Args parms = new Args("TapeWrite", "inputfile outputtape", args, options);

        File inputFile = parms.getInputFile(1);
        File outputFile = parms.getOutputFile(2);
        String encoding = parms.getOption("encoding");

        // Check parameters
        if (inputFile == null || outputFile== null) {
            parms.usage();
        }

        // Create input/output streams
        FileOutputStream output = new FileOutputStream(outputFile);
        TelecodeInputStream input = null;
        if (encoding == null) {
            input = new TelecodeInputStream(new FileReader(inputFile));
        } else {
            input = new TelecodeInputStream(new InputStreamReader(new FileInputStream(inputFile), encoding));
        }

        // Write the input to the output
        input.write(output);
        output.close();
    }
}
