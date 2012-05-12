/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2010
 */
package elliott803.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import elliott803.telecode.TelecodeInputStream;
import elliott803.telecode.TelecodeOutputStream;

/**
 * Utility program to take an input text file and write an equivalent output 
 * file using only telecode characters.
 *
 * Usage:
 *    PrintTelecode [options] inputfile [outputfile]
 *
 * where:
 *    inputfile: the name of the text input file
 *    outputfile: the name for the output file, defaults to standard output
 *
 * options:
 *    -ascii: print using only US-ASCII characters
 *    -inputenc encoding_name: the encoding of the input file, defaults to the
 *                             standard platform encoding 
 *    -outputenc encoding_name: the encoding of the output file, defaults to the
 *                              standard platform encoding     
 *                                                    
 * When printing with the '-ascii' option the only character affected is the telecode figure-shift
 * character 26 which is supposed to be the GB-pound sign; this will be printed as a '#'.
 *
 * @author Baldwin
 */
public class PrintTelecode {

    public static void main(String[] args) throws Exception {
        // Handle parameters
        Args.Map options = Args.optionMap();
        options.put("ascii", null);
        options.put("inputenc", "inputencoding");
        options.put("outputenc", "outputencoding");
        Args parms = new Args("PrintTelecode", "inputfile [outputfile]", args, options);

        File inputFile = parms.getInputFile(1);
        File outputFile = parms.getOutputFile(2);
        String inputenc = parms.getOption("inputenc");
        String outputenc = parms.getOption("outputenc");
        boolean useASCII = parms.getFlag("ascii");

        // Check parameters
        if (inputFile == null) {
            parms.usage();
        }

        // Create input/output streams/readers
        TelecodeInputStream input = null;
        if (inputenc == null) {
            input = new TelecodeInputStream(new FileReader(inputFile));
        } else {
            input = new TelecodeInputStream(new InputStreamReader(new FileInputStream(inputFile), inputenc));
        }

        TelecodeOutputStream output = null;
        if (outputFile != null) {
            if (outputenc == null)
                output = new TelecodeOutputStream(new PrintWriter(outputFile), useASCII);
            else
                output = new TelecodeOutputStream(new PrintWriter(outputFile, outputenc), useASCII);
        } else {
            if (outputenc == null)
                output = new TelecodeOutputStream(System.out, useASCII);
            else 
                output = new TelecodeOutputStream(new OutputStreamWriter(System.out, outputenc), useASCII);
        }
        
        // Print the input to the output
        output.write(input);
        output.close();
    }    
}
