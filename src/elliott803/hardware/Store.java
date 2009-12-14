/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2009
 */
package elliott803.hardware;

import elliott803.machine.Computer;
import elliott803.machine.Dump;
import elliott803.machine.Instruction;
import elliott803.machine.Word;
import elliott803.view.StoreView;

/**
 * This class simulates the core store.  The machine had 8K (8192) words of storage, each
 * word being 39-bits long.  The first 4 storage locations contained some hard-wired
 * instructions called the 'initial instructions' that were used to load programs from
 * paper tape.
 *
 * @author Baldwin
 */
public class Store {

    static final int STORE_SIZE = 8*1024;          // 8K
    static final int STORE_START = 4;              // First writable location

    public Computer computer;                      // The owning computer

    // Words are 39-bits - so we'll have to use Java longs to hold them
    long[] store = null;

    int lastAddr;
    long lastValue;

    public Store(Computer computer) {
        this.computer = computer;

        // Default store size is 8K (4K versions were also available but it doesn't
        // really seem worth simulating that!).
        store = new long[STORE_SIZE];

        // Set the initial instructions.  The first four word of store contain some
        // hard-wired instructions.
        store[0] = Word.asInstr(Instruction.asInstr(026, 4), 0, Instruction.asInstr(006, 0));
        store[1] = Word.asInstr(Instruction.asInstr(022, 4), 1, Instruction.asInstr(016, 3));
        store[2] = Word.asInstr(Instruction.asInstr(055, 5), 0, Instruction.asInstr(071, 0));
        store[3] = Word.asInstr(Instruction.asInstr(043, 1), 0, Instruction.asInstr(040, 2));
    }

    // Clear store
    public void clear() {
        for (int i = STORE_START; i < STORE_SIZE; i++) {
            store[i] = 0;
            viewWord(i, 0);
        }
    }

    // Read store
    // Used for reading data, returns 0 if used to read initial instructions
    public long read(int addr) {
        addr &= Instruction.ADDR_MASK;
        if (addr >= STORE_START) {
            return store[addr];
        } else {
            return 0;
        }
    }

    // Fetch store
    // Used for reading instructions, returns initial instructions
    public long fetch(int addr) {
        addr &= Instruction.ADDR_MASK;
        return store[addr];
    }

    // Write store, cannot overwrite initial instructions
    public void write(int addr, long value) {
        addr &= Instruction.ADDR_MASK;
        if (addr >= STORE_START) {
            store[addr] = value;
            lastAddr = addr;
            lastValue = value;
            viewWord(addr, value);
        }
    }

    // Dump
    public void dump(Dump dump) {
        dump.core = store;
    }

    // Mainly for debugging
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("STORE: last write ").append(lastAddr).append("=").append(Word.toOctalString(lastValue));
        return sb.toString();
    }

    /*
     * GUI visualisation
     */

    StoreView view;

    public void setView(StoreView view) {
        this.view = view;
    }

    void viewWord(int addr, long value) {
        if (view != null)
            view.updateCore(addr, value);
    }
}
