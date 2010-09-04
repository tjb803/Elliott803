/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2009
 */
package elliott803.hardware;

import junit.framework.TestCase;
import elliott803.machine.Computer;
import elliott803.machine.Instruction;
import elliott803.machine.Word;

/**
 * JUnit tests for the Store class.
 * 
 * @author Baldwin
 */
public class TestStore extends TestCase {
    
    static long II0 = Word.asInstr(Instruction.asInstr(026, 4), 0, Instruction.asInstr(006, 0));
    static long II1 = Word.asInstr(Instruction.asInstr(022, 4), 1, Instruction.asInstr(016, 3));
    static long II2 = Word.asInstr(Instruction.asInstr(055, 5), 0, Instruction.asInstr(071, 0));
    static long II3 = Word.asInstr(Instruction.asInstr(043, 1), 0, Instruction.asInstr(040, 2));
     
    protected void setUp() throws Exception {
        // Create store and fill with data (except for locations 0 to 3)
        store = new Store(new Computer(true));
        for (int i = 4; i < store.store.length; i++)
            store.store[i] = i;
    }

    Store store;
    
    public void testClear() throws Exception {
        store.clear();
        assertTrue(store.store[0] != 0);
        assertTrue(store.store[1] != 0);
        assertTrue(store.store[2] != 0);
        assertTrue(store.store[3] != 0);
        for (int i = 4; i < store.store.length; i++)
            assertEquals(0, store.store[i]);
    }
    
    public void testRead() throws Exception {
        assertEquals(0, store.read(0));
        assertEquals(0, store.read(1));
        assertEquals(0, store.read(2));
        assertEquals(0, store.read(3));
        for (int i = 4; i < store.store.length; i++)
            assertEquals(i, store.read(i));
        
        // Check address wrap around
        assertEquals(10, store.read(8192 + 10));
        assertEquals(20, store.read(8192*2 + 20));
    }
    
    public void testFetch() throws Exception {
        assertEquals(II0, store.fetch(0));
        assertEquals(II1, store.fetch(1));
        assertEquals(II2, store.fetch(2));
        assertEquals(II3, store.fetch(3));
        for (int i = 4; i < store.store.length; i++)
            assertEquals(i, store.fetch(i));
        
        // Check address wrap around
        assertEquals(10, store.fetch(8192 + 10));
        assertEquals(20, store.fetch(8192*2 + 20));
    }
    
    public void testWrite() throws Exception {
        for (int i = 0; i < store.store.length; i++) 
            store.write(i, i+100);
        
        assertEquals(II0, store.store[0]);
        assertEquals(II1, store.store[1]);
        assertEquals(II2, store.store[2]);
        assertEquals(II3, store.store[3]);
        for (int i = 4; i < store.store.length; i++)
            assertEquals(i+100, store.store[i]);
        
        // Check address wrap around
        store.write(8192 + 10, 1001);
        store.write(8192*2 + 20, 1002);
        assertEquals(1001, store.store[10]);
        assertEquals(1002, store.store[20]);
    }
}
