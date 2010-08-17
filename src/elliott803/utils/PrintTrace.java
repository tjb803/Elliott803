/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2009
 */
package elliott803.utils;

import java.io.File;
import java.io.PrintStream;
import java.text.DateFormat;

import elliott803.machine.Instruction;
import elliott803.machine.Trace;
import elliott803.machine.Word;

/**
 * This class will print a formatted instruction trace
 *
 * Usage:
 *    PrintTrace tracefile [outputfile]
 *
 * where:
 *    tracefile: is the name of an instruction trace file
 *    outputfile: is the name of the formatted output file, default is standard out.
 *
 * Current trace output format is
 *
 *    AAAA.N:  OP1 ADDR1 b OP2 ADDR2   O   nnnnnnnnnn (d)
 *
 * AAAA.N: is the current address (.0 means first instruction, .1 means second instruction)
 * OP1 etc: is the current instruction pair
 * O: is the overflow indicator, either 'O' or '-'
 * nnnnn: is the accumulator in octal
 * (d): is the accumulator in decimal
 *
 * @author Baldwin
 */
public class PrintTrace {

    public static void main(String[] args) throws Exception {
        // Handle parameters
        Args parms = new Args("PrintTrace", "tracefile [outputfile]", args, null);
        File inputFile = parms.getInputFile(1);
        File outputFile = parms.getOutputFile(2);

        // Check parameters
        if (inputFile == null) {
            parms.usage();
        }

        // Create input and output streams
        PrintStream output = System.out;
        if (outputFile != null) {
            output = new PrintStream(outputFile);
        }

        // Read the trace file
        Trace trace = Trace.readTrace(inputFile);
        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG);

        output.println("Elliott 803B Instruction Trace");
        output.println("created: " + df.format(trace.timestamp));
        output.println();

        // Format the output
        for (Trace.Entry entry = trace.nextEntry(); entry != null; entry = trace.nextEntry()) {
            output.print(Instruction.toAddrString(entry.scr) + "." + entry.scr2 + ":  ");
            output.print(Word.toInstrString(entry.ir));
            output.print("   " + (entry.oflow ? "O" : "-"));
            output.print("   " + Word.toOctalString(entry.acc) + " (" + Word.toIntegerString(entry.acc) + ")");
            output.println();
        }
    }
}
