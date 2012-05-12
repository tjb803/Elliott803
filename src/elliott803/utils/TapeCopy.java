/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2009
 */
package elliott803.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Utility program to copy one or more telecode tapes to a single new tape.
 *
 * Usage:
 *    TapeCopy inputtape1 [inputtape2 ...] -output outputtape
 *
 * where:
 *    inputtapeN: the name of a telecode tape to be copied
 *    outputtape: the name for the output tape file
 *
 * @author Baldwin
 */
public class TapeCopy {

    private static final byte[] runout = new byte[20];

    public static void main(String[] args) throws Exception {
        // Handle parameters
        Args.Map options = Args.optionMap();
        options.put("output", "outputtape");
        Args parms = new Args("TapeCopy", "inputtape1 [inputtape2 ...]", args, options);

        List<File> inputFiles = parms.getInputFiles(1);
        File outputFile = parms.getOutputFile("output");

        // Check parameters
        if (inputFiles.isEmpty() || outputFile == null) {
            parms.usage();
        }

        // Copy input tapes to the output tape, adding some runout between each tape
        OutputStream output = new BufferedOutputStream(new FileOutputStream(outputFile));
        for (File inputFile : inputFiles) {
            InputStream input = new BufferedInputStream(new FileInputStream(inputFile));

            // Add some runout at the start of the copy, then copy the input tape to the
            // output, ignoring any runout at the start and end of the input.
            output.write(runout);

            int ch = input.read();
            while (ch == 0) {
                ch = input.read();
            }

            int blanks = 0;
            while (ch != -1) {
                if (ch == 0) {
                    blanks += 1;
                } else {
                    while (blanks > 0) {
                        output.write(0);
                        blanks -= 1;
                    }
                    output.write(ch);
                }
                ch = input.read();
            }
            input.close();
        }

        output.write(runout);
        output.close();
    }
}
