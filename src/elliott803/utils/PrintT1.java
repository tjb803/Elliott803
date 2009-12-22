/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2009
 */
package elliott803.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import elliott803.machine.Instruction;
import elliott803.machine.Word;

/**
 * This class will print the contents of a binary tape - this is a tape that can
 * be loaded using the initial instructions.
 *
 * Usage:
 *    PrintT1 tapefile [outputfile]
 *
 * where:
 *    tapefile: is the name of a T1 format tape
 *    outputfile: is the name of the formatted output file, default is standard out.
 *
 * @author Baldwin
 */
public class PrintT1 {

    public static void main(String[] args) throws Exception {
        // Handle parameters
        Args parms = new Args("PrintT1", "tapefile [outputfile]", args, null);
        File inputFile = parms.getInputFile(1);
        File outputFile = parms.getOutputFile(2);

        // Check parameters
        if (inputFile == null) {
            parms.usage();
        }

        // Create input and output streams
        FileInputStream input = new FileInputStream(inputFile);
        PrintStream output = System.out;
        if (outputFile != null) {
            output = new PrintStream(outputFile);
        }

        // Read the load address
        long word = readWord(input);
        if (word == -1) {
            error("Unable to read program load address");
        }
        int loadAddress = (int)word + 4;

        // Load the tape until either we reach the end or we reach the top of store
        long[] store = new long[8192];
        int addr = 0;
        for (addr = loadAddress; addr < store.length; addr++) {
            word = readWord(input);
            if (word == -1) {
                break;
            }
            store[addr] = word;
        }
        int size = addr - loadAddress;

        // If we reach the top of store, we need to read the entry address trigger
        int triggerInstruction = -1;
        if (word != -1) {
            // Read the four words that wrap over the initial instructions and then
            // read the trigger instruction itself
            for (int i = 0; i < 5; i++) {
                word = readWord(input);
                if (word == -1) {
                    break;
                }
            }
            if (word == -1) {
                error("Unable to read program trigger address");
            }

            // Tape must have at least one more character present to trigger
            if (input.read() == -1) {
                error("Tape is missing final character to activate trigger");
            }

            // Final trigger will be "16 3" + second instruction from trigger word + 1
            triggerInstruction = Instruction.asInstr(016, 4) + Word.getInstr2(word);
        }

        // Format the output (uses a method from the dump formatter to format words).
        output.println("Elliott 803B Binary Tape\n");
        output.println("Load address: " + Instruction.toAddrString(loadAddress));
        if (triggerInstruction != -1) {
            output.println("Trigger:   " + Instruction.toInstrString(triggerInstruction));
        }
        output.println();

        PrintCore formatter = new PrintCore(output);
        formatter.printStore(store, loadAddress, size);
    }

    // Read next word from the input tape
    private static long readWord(InputStream in) throws IOException {
        long word = 0;
        while (Long.numberOfLeadingZeros(word) > (64-39)) {
            int ch = in.read();
            if (ch != -1)
                word = (word<<5) + (ch & Word.CHAR_MASK);
            else
                word = -1;
        }
        return (word == -1) ? word : Word.asWord(word);
    }

    // Error report
    private static void error(String msg) {
        System.err.println("ERROR: " + msg);
        System.exit(1);
    }
}
