/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2010
 */
package elliott803.utils;

import java.awt.Rectangle;
import java.io.File;
import java.io.PrintStream;
import java.text.DateFormat;
import java.util.Map;

import elliott803.machine.Dump;
import elliott803.view.MachineImage;
import elliott803.view.ViewImage;

/**
 * Print the contents of a saved machine image.  Mainly for debugging.
 * 
 * Usage:
 *    PrintMachine [options] machine [outputfile]
 *
 * where:
 *    machine: is the name of a saved machine image
 *    outputfile: is the name of the formatted output file, default is standard out.
 *
 * options:
 *    -nodump: Do not print core dump section
 *    
 * @author Baldwin
 */
public class PrintMachine {

    public static void main(String[] args) throws Exception {
        // Handle parameters
        Map<String,String> options = Args.optionMap();
        options.put("nodump", null);
        Args parms = new Args("PrintMachine", "machine [outputfile]", args, options);
        
        File inputFile = parms.getInputFile(1);
        File outputFile = parms.getOutputFile(2);
        boolean nodump = parms.getFlag("nodump");

        // Check parameters
        if (inputFile == null) {
            parms.usage();
        }

        // Create input and output streams
        PrintStream output = System.out;
        if (outputFile != null) {
            output = new PrintStream(outputFile);
        }

        // Machine image consists of a core dump followed by an 
        // optional view image with all window positions.
        MachineImage image = MachineImage.readImage(inputFile);
        Dump dump = image.imageDump;
        ViewImage view = image.imageView;
        
        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG);
        output.println("Elliott 803B Machine Image");
        output.println("created: " + df.format(dump.timestamp));
        output.println();
        
        // Start with the core dump
        if (nodump) {
            output.println("Core store omitted.");     
            output.println();
        } else {    
            output.println("Core store:");
            PrintCore formatter = new PrintCore(output);
            formatter.printStore(dump.core, 0, 8192);
        }    
        
        // Then the saved window positions
        if (view == null) {
            output.println("No view details.");
        } else {
            output.println("View details:");
            output.println("  Application: " + view.title); 
            output.println("    position = " + printRect(view.position) + ", " + printRect(view.position2));
            output.println();
            output.println("  Windows:");
            for (ViewImage v : view.windows) {
                System.out.println("    " + v.title);
                System.out.println("      position = " + printRect(v.position));
                System.out.println("      isMin = " + v.isMin + ", isMax = " + v.isMax);
            }
            output.println();
        }    
    } 
    
    private static String printRect(Rectangle rect) {
        return "[x=" + rect.x + " y=" + rect.y + " w=" + rect.width + " h=" + rect.height + "]";
    }
}
