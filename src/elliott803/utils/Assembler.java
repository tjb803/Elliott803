/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2009,2013
 */
package elliott803.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import elliott803.machine.Instruction;
import elliott803.machine.Word;
import elliott803.telecode.CharToTelecode;
import elliott803.telecode.Telecode;

/**
 * This class provides a simple assembler that can take a basic 803 machine code language
 * input file and create a binary tape suitable for loading by the initial instructions.
 *
 * Usage:
 *    Assemble inputfile outputtape
 *
 * where:
 *    inputfile: is the assembler source input file name
 *    outputtape: is the binary tape output file name
 *
 * Assembler syntax is a sequence of lines.  Each line can have a one or more labels which 
 * correspond to the address of that line and then one of:
 *  - a directive
 *  - a constant or sequence of constants
 *  - a string
 *  - an instruction or instruction pair
 *  Anything after a * is a comment and is ignored.  Whitespace is also largely ignored.
 *
 * Label:  axxxx:  (ie some text starting with a letter followed by a colon)
 *
 * Directives: 
 *     =addr   means load at address addr (loads at top of store if omitted)
 *     @addr   means add trigger to entry point addr (can be a label name)
 *
 * ConstantSequence:  Constant [,ConstantSequence]
 *
 * Constant:  Integer or Float or Label name
 *
 * Integer:  [+|-]nnnn
 * 
 * Float:  [+|-]nnn.nnn[@[+|-]nnn]
 *
 * String:  'xxxxxxx'
 *     becomes a sequence of characters (including any necessary shifts)
 *
 * Instruction:  op1 addr1 [b op2 addr2]
 *     op is a octal opcode, addr is a decimal address or label name, b is : or /
 *
 * e.g.
 *    * 
 *    * Simple Hello World program
 *    *
 *             =8160                * Load program starting at address 8160 
 *             @entry               * Define the entry address
 *      
 *    begin:                                                 
 *    loop:   22 index / 30 hello   * Get next character 
 *            42 end   : 40 write   * Check for zero at end of string or write char 
 *    write:  20 char  / 74 4096    * Write character to teletype
 *            40 loop               * Loop back to next character  
 *    end:    74 4125  : 74 4126    * Write CR and LF to finish line   
 *    done:   72 8191               * Exit when finished     
 *    
 *    entry:  30 m1    : 20 index   * Program entry point
 *            40 begin              * Initialize 'index' and begin output     
 *             
 *    char:   0                     * Character workspace
 *    index:  0                     * Index into string 
 *    hello: 'Hello World?'         * Text - will be {LS}HELLO WORLD{FS}? in telecode
 *            0                     * Zero marks end of string 
 *    m1:    -1, +4, 1.5, 0.1@-3    * A few constant values (not all used!) 
 *
 * TODO:
 *   Add constant symbols and simple arithmetic expressions to support e.g.
 *        CR = 29         * Define symbol
 *        74 4096+CR      * Print CR
 *
 * Note: the syntax of this assembler is loosely based on my faded memory of a real 803
 *        assembler, but has many changes and additions to make it easier to use.  Its
 *        main purpose is to allow simple binary tapes of test programs to be produced.
 *
 * @author Baldwin
 */
public class Assembler {

    public static void main(String[] args) throws Exception {
        // Handle parameters
        Args parms = new Args("Assemble", "inputfile outputtape", args, null);
        File inputFile = parms.getInputFile(1);
        File outputFile = parms.getOutputFile(2);

        // Check parameters
        if (inputFile == null || outputFile == null) {
            parms.usage();
        }

        // Open the input and output streams
        LineNumberReader input = new LineNumberReader(new FileReader(inputFile));
        FileOutputStream output = new FileOutputStream(outputFile);

        // Run the assembler
        Assembler assembler = new Assembler(input, output);
        assembler.run();
        input.close();
        output.close();
    }

    /*
     * Create an assembler
     */
    Assembler(LineNumberReader in, OutputStream out) {
        input = in;
        output = out;
    }

    private static final String COMMENT_CHAR = "*";
    private static final String LABEL_CHAR = ":";
    private static final String ADDRESS_CHAR = "=";
    private static final String TRIGGER_CHAR = "@";
    private static final String STRING_CHAR = "'";

    private static final String OP_PATTERN = "([0-7]{2})";
    private static final String ADDRESS_PATTERN = "([0-9]{1,4})";
    private static final String B_PATTERN = "([:/])";
    private static final String LABEL_PATTERN = "([A-Za-z]\\w*)";
    private static final String INTEGER_PATTERN = "([+-]?\\d+)";
    private static final String FLOAT_PATTERN = "([+-]?((\\d+\\.\\d*)|(\\d*\\.\\d+))(@[+-]?\\d+)?)";
    private static final String CONSTANT_PATTERN = "(" + INTEGER_PATTERN + "|" + FLOAT_PATTERN + "|" + LABEL_PATTERN + ")";
    private static final String VALUE_PATTERN = "(" + LABEL_PATTERN + "|" + ADDRESS_PATTERN + ")";
    private static final String LOAD_PATTERN = "(" + ADDRESS_CHAR + ADDRESS_PATTERN + ")";
    private static final String TRIGGER_PATTERN = "(" + TRIGGER_CHAR + VALUE_PATTERN + ")";
    private static final String INSTR_PATTERN = "(" + OP_PATTERN + "\\s+" + VALUE_PATTERN + ")";

    private static final String LABEL_LINE = LABEL_PATTERN + "\\s*" + LABEL_CHAR + ".*";
    private static final String DIRECTIVE_LINE = LOAD_PATTERN + "|" + TRIGGER_PATTERN;
    private static final String CONSTANT_LINE = CONSTANT_PATTERN + "(\\s*,\\s*" + CONSTANT_PATTERN + ")*";
    private static final String STRING_LINE = STRING_CHAR + ".*" + STRING_CHAR;
    private static final String CODE_LINE = INSTR_PATTERN + "(\\s*" + B_PATTERN + "\\s*" + INSTR_PATTERN + ")?";

    private LineNumberReader input;
    private OutputStream output;

    private Map<String,Integer> symbols;
    private List<SourceLine> sourceCode;
    private List<Long> objectCode;
    private SourceLine loadDirective, triggerDirective;
    private int loadAddress, triggerAddress;

    void run() throws IOException {
        // Pass 1: Read the input file and remove comments, blank lines etc.
        // Extract any labels found to build a symbol table and a set of source code
        // lines for the code to be generated.
        symbols = new HashMap<String,Integer>();
        sourceCode = new ArrayList<SourceLine>();
        buildSymbolsAndSource();

        // Process any directives found to determine the load and entry point addresses
        // for the program and update the symbol table to absolute addresses.
        loadAddress = -1;
        triggerAddress = -1;
        setLoadAndEntryAddress();

        // Pass 2: Generate code
        objectCode = new ArrayList<Long>();
        generateObjectCode();

        // Write the object code to the output tape and add a trigger if needed
        writeOutputTape();
        if (triggerAddress != -1) {
            writeTrigger();
        }
    }

    // Build symbol table and source code to translate
    private void buildSymbolsAndSource() throws IOException {
        String line = input.readLine();
        while (line != null) {
            // Strip comments, unwanted space and blank lines.
            int i = line.indexOf(COMMENT_CHAR);
            if (i != -1)
                line = line.substring(0, i);
            line = line.trim();
            if (line.length() > 0) {
                if (line.matches(LABEL_LINE)) {
                    // Have a label, add the current address offset to the symbol table
                    i = line.indexOf(LABEL_CHAR);
                    String label = line.substring(0, i).trim();
                    addSymbol(label, sourceCode.size());

                    // Re-process the line, without the label
                    line = line.substring(i+1);
                    continue;
                }

                if (line.matches(DIRECTIVE_LINE)) {
                    // Have a directive
                    if (line.matches(LOAD_PATTERN)) {
                        if (loadDirective == null) {
                            loadDirective = new SourceLine(line);
                        } else {
                            error(0, "Load directive already set to " + loadDirective.source);
                        }
                    } else if (line.matches(TRIGGER_PATTERN)) {
                        if (triggerDirective == null) {
                            triggerDirective = new SourceLine(line);
                        } else {
                            error(0, "Entry directive already set to " + triggerDirective.source);
                        }
                    } else {
                        error(0, "Incorrect directive: " + line);
                    }
                } else if (line.matches(CONSTANT_LINE)) {
                    // Have a sequence of one or more constants
                    StringTokenizer t = new StringTokenizer(line, ",");
                    while (t.hasMoreTokens()) {
                        sourceCode.add(new SourceLine(t.nextToken().trim()));
                    }
                } else if (line.matches(CODE_LINE)) {
                    // Have code
                    sourceCode.add(new SourceLine(line));
                } else if (line.matches(STRING_LINE)) {
                    // Have a string.  Need to turn the characters into telecode and write a
                    // a series of integer constants.
                    String text = line.substring(1);
                    if (text.endsWith(STRING_CHAR))
                        text = text.substring(0, text.length()-1);
                    CharToTelecode converter = new CharToTelecode();
                    byte[] tc = new byte[text.length()*2];
                    int len = converter.convert(text.toCharArray(), text.length(), tc);
                    for (i = 0; i < len ; i++)
                        sourceCode.add(new SourceLine(Byte.toString(tc[i])));
                } else {
                    error(0, "Syntax error: " + line);
                }
            }
            line = input.readLine();
        }
    }
    
    // Add a symbol to the symbol table
    private void addSymbol(String label, int value) {
        if (!symbols.containsKey(label)) {
            symbols.put(label, value);
        } else {    
            error(0, "Symbol defined more than once: " + label);
        }
    }    

    // Generate load address and entry address
    private void setLoadAndEntryAddress() {
        // Check for load address.  If not present we load into the top of store.
        if (loadDirective != null) {
            loadAddress = Instruction.parseAddr(loadDirective.source.substring(1));
        } else {
            loadAddress = 8192 - sourceCode.size();
        }

        // Update the symbol table to absolute addresses
        for (Map.Entry<String,Integer> entry : symbols.entrySet()) {
            entry.setValue(entry.getValue() + loadAddress);
        }

        // Set the trigger entry point address, if present
        if (triggerDirective != null) {
            String addr = triggerDirective.source.substring(1);
            if (addr.matches(ADDRESS_PATTERN)) {
                triggerAddress = Instruction.parseAddr(addr);
            } else {
                if (symbols.containsKey(addr)) {
                    triggerAddress = symbols.get(addr);
                } else {
                    error(triggerDirective.lineNo, "Incorrect entry point: " + triggerDirective.source);
                }
            }
        }
    }

    // Generate the object code
    private void generateObjectCode() {
        for (SourceLine sourceLine : sourceCode) {
            String source = sourceLine.source;
            long value = 0;
            if (source.matches(CONSTANT_LINE)) {
                if (source.matches(INTEGER_PATTERN)) {
                    value = Word.parseInteger(source);
                } else if (source.matches(FLOAT_PATTERN)) {
                    value = Word.parseFloat(source);
                    if (value == Word.NOTHING) { 
                        error(sourceLine.lineNo, "Float value out of range: " + source);
                    }    
                } else {
                    if (symbols.containsKey(source)) {
                        value = symbols.get(source);
                    } else {
                        error(sourceLine.lineNo, "Unresolved symbol: " + source);
                    }
                }
            } else if (source.matches(CODE_LINE)) {
                // Code to be generated
                int instr1 = 0, b = 0, instr2 = 0;
                StringTokenizer t1 = new StringTokenizer(source, ":/", true);
                instr1 = parseInstruction(sourceLine.lineNo, t1.nextToken().trim());
                if (t1.hasMoreTokens()) {
                   b = t1.nextToken().equals(":") ? 0 : 1;
                   if (t1.hasMoreTokens()) {
                       instr2 = parseInstruction(sourceLine.lineNo, t1.nextToken().trim());
                   }
                }
                value = Word.asInstr(instr1, b, instr2);
            } else {
                // Should not happen!
                error(sourceLine.lineNo, "Incorrect source code: " + source);
            }
            objectCode.add(value);
        }
    }

    private int parseInstruction(int lineNo, String instruction) {
        int op = 0, addr = 0;
        StringTokenizer t = new StringTokenizer(instruction, " \t");
        op = Instruction.parseOp(t.nextToken());
        String target = t.nextToken();
        if (target.matches(ADDRESS_PATTERN)) {
            addr = Instruction.parseAddr(target);
        } else {
            if (symbols.containsKey(target)) {
                addr = symbols.get(target);
            } else {
                error(lineNo, "Unresolved symbol: " + target);
            }
        }
        if (addr < 0 || addr > 8191) {
            error(lineNo, "Address out of range: " + addr);
        }
        return Instruction.asInstr(op, addr);
    }

    // Write the object code to the output tape
    private void writeOutputTape() throws IOException {
        // Write the load address first
        writeWord(loadAddress-4);

        // Followed by the object code
        for (long value : objectCode) {
           writeWord(value);
        }
    }

    // Write trigger code
    private void writeTrigger() throws IOException {
        // We need to write enough zero-words to take us from the last loaded word
        // to the top of store (8192) and four more words to wrap around the initial
        // instructions.
        int pad = 8192 - (loadAddress + objectCode.size());

        // Write a warning if there is too much padding
        if (pad > 12) {
            System.out.println("WARNING: will need " + pad + " blank words for trigger.");
            System.out.println("Suggest loading code at address " + (8192-objectCode.size()));
        }

        // Write padding plus 4 extra zeros
        for (int i = 0; i < pad + 4; i++)
            writeWord(0);

        // Write the trigger instruction and one extra character.  The trigger needs to
        // read a few more characters to operate - the exact number is a little variable
        // but it is at least one character and no more than needed to cause an overflow.
        writeWord(Word.asInstr(0, 0, Instruction.asInstr(022, triggerAddress-4)));
        writeWord(0);
    }

    private void writeWord(long word) throws IOException {
        byte[] bb = new byte[8];
        for (int i = bb.length; i > 0; i--) {
            bb[i-1] = (byte)(word & Telecode.CHAR_MASK);
            word = word >> 5;
        }
        bb[0] |= 0x10;      // Ensure top bit is always set
        output.write(bb);
    }

    // Report an error and exit
    private void error(int lineNo, String msg) {
        System.err.println("Line " + ((lineNo == 0) ? input.getLineNumber() : lineNo) + ": " + msg);
        System.exit(1);
    }

    private class SourceLine {
        int lineNo;
        String source;
        SourceLine(String s) {
            lineNo = input.getLineNumber();
            source = s;
        }
    }
}
