/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2009, 2012
 */
package elliott803;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;

import elliott803.hardware.PaperTapeStation;
import elliott803.hardware.TapeDevice;
import elliott803.machine.Computer;
import elliott803.machine.Word;
import elliott803.telecode.TelecodeInputStream;
import elliott803.telecode.TelecodeOutputStream;
import elliott803.utils.Args;

/**
 * This is a command line interface to the Elliott 803 simulator.
 *
 * Usage:
 *   Run [options] programtape [entrypoint]
 *
 * where:
 *   programtape: a binary prorgram tape to be loaded by the initial instructions
 *   entrypoint: address to enter after tape is loaded (ignored for a self-triggering tape)
 *
 * options:
 *   -reader1 or -sysreader1 inputtape: tape to load in reader 1 (after program tape is read)
 *   -reader2 or -sysreader2 inputtape: tape to load in reader 2
 *   -punch1 outputtape: output tape file for punch 1
 *   -punch2 outputtape: output tape file for punch 2
 *   -teletype outputfile: output file for teletype (defaults to System.out)
 *   -wordgen instruction: instruction pair to set on the word generator
 *   -press button: word generator button to press on a console wait
 *   -ascii: use only US-ASCII character set
 *   -dump: produce a system dump on exit
 *   -trace: produce a full instruction trace
 *
 * @author Baldwin
 */
public class Run {

    public static void main(String[] args) throws Exception {
        // Handle parameters
        Args.Map options = Args.optionMap();
        options.put("reader1", "+or");
        options.put("sysreader1", "inputtape");
        options.put("reader2", "+or");
        options.put("sysreader2", "inputtape");
        options.put("punch1", "outputtape");
        options.put("punch2", "outputtape");
        options.put("teletype", "outputfile");
        options.put("wordgen", "+\"instruction\"");
        options.put("press", "button");
        options.put("ascii", null);
        options.put("dump", null);
        options.put("trace", null);
        Args parms = new Args("elliott803.Run", "programtape [entryaddress]", args, options);

        File inputFile1 = parms.getInputFile("reader1");
        File inputFile2 = parms.getInputFile("reader2");
        File sysInputFile1 = parms.getInputFile("sysreader1");
        File sysInputFile2 = parms.getInputFile("sysreader2");
        File outputFile1 = parms.getOutputFile("punch1");
        File outputFile2 = parms.getOutputFile("punch2");
        File outputFile3 = parms.getOutputFile("teletype");
        String wgInstruction = parms.getOption("wordgen");
        int button = parms.getInteger("press");
        boolean instrTrace = parms.getFlag("trace");
        boolean dumpOnExit = parms.getFlag("dump");
        boolean useASCII = parms.getFlag("ascii");

        File programFile = parms.getInputFile(1);
        int entryAddr = parms.getInteger(2);

        // Check at least a program tape has been supplied
        if (programFile == null)
            parms.usage();

        // Open any input/output streams
        InputStream programTape = new FileInputStream(programFile);

        InputStream inputTape1 = null, inputTape2 = null;
        if (inputFile1 != null)
            inputTape1 = new FileInputStream(inputFile1);
        else if (sysInputFile1 != null)
            inputTape1 = new TelecodeInputStream(new FileReader(sysInputFile1));
        
        if (inputFile2 != null)
            inputTape2 = new FileInputStream(inputFile2);
        else if (sysInputFile2 != null)
            inputTape2 = new TelecodeInputStream(new FileReader(sysInputFile2));

        OutputStream outputTape1 = null, outputTape2 = null, outputTeletype = null;
        if (outputFile1 != null)
            outputTape1 = new FileOutputStream(outputFile1);
        if (outputFile2 != null)
            outputTape2 = new FileOutputStream(outputFile2);
        
        if (outputFile3 != null)
            outputTeletype = new TelecodeOutputStream(new FileWriter(outputFile3), useASCII);
        else
            outputTeletype = new TelecodeOutputStream(System.out, useASCII);
        
        // Anything to set on the word generator?
        long wordgen = 0;
        if (wgInstruction != null)
            wordgen = Word.parseInstr(wgInstruction);

        // Create computer and set initial program tape and output tapes
        Computer computer = new Computer();
        computer.setRealTime(false);
        computer.pts.setReaderTape(PaperTapeStation.READER1, programTape);
        computer.pts.setPunchTape(PaperTapeStation.PUNCH1, outputTape1);
        computer.pts.setPunchTape(PaperTapeStation.PUNCH2, outputTape2);
        computer.pts.setPunchTape(PaperTapeStation.TELETYPE, outputTeletype);
        
        // Set console options
        computer.console.setWordGen(wordgen);
        computer.console.setManualData(button > 0);

        // Jump to the initial instructions to load the program
        computer.runInstructions(0);

        // If system enters a busy wait, load the data tapes into the readers
        boolean rdr1Wait = computer.pts.readers[PaperTapeStation.READER1].deviceBusy();
        boolean rdr2Wait = computer.pts.readers[PaperTapeStation.READER2].deviceBusy();                
        computer.pts.setReaderTape(PaperTapeStation.READER1, inputTape1);
        computer.pts.setReaderTape(PaperTapeStation.READER2, inputTape2);

        if (instrTrace)
            computer.traceStart();

        // If we have an entry point jump to it, otherwise restart with data tapes loaded
        if (entryAddr != -1) 
            computer.runInstructions(entryAddr);
        else if (rdr1Wait || rdr2Wait)
            computer.runInstructions();
        
        // If we enter a wait on the console, we need to simulate a button press to try
        // to continue.  To do this we have to allow one "70 0" instruction to read the 
        // current keyboard state state before we change the state of the requested button 
        // and allow a second "70 0". 
        if (computer.console.deviceBusy()) {
            computer.console.setManualDataDelay();
            computer.runInstructions();
            computer.console.toggleWordGenBit(40 - button);
            computer.console.setManualData(false);
            computer.runInstructions();
        }    

        if (instrTrace)
            computer.traceStop();

        for (TapeDevice device : computer.pts.readers) {
            if (device.deviceBusy())
                System.out.println("*** Waiting for input: READER " + device.id);
        }
        for (TapeDevice device : computer.pts.punches) {
            if (device.deviceBusy())
                System.out.println("*** Waiting for output: PUNCH " + device.id);
        }

        // Generate dump on exit if required
        if (dumpOnExit) {
            computer.dump();
        }
    }
}
