/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2009, 2013
 */
package elliott803.hardware;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import elliott803.machine.Computer;
import elliott803.machine.Dump;
import elliott803.machine.Instruction;
import elliott803.machine.Trace;
import elliott803.machine.Word;
import elliott803.view.CpuView;

/**
 * This is the Central Processor.  This fetches and executes instructions.
 * 
 * This class needs to be careful with multi-threaded access as the main 'execute'
 * methods will be run on one thread, whereas methods such as 'reset' and 'stop'
 * can be invoked asynchronously via a different GUI thread.  It is important that
 * common variables are either Atomic or read and/or updated under a common monitor
 * lock.
 *
 * @author Baldwin
 */
public class CPU {

    public Computer computer;   // The owning computer

    // CPU registers and flags
    long acc;               // Accumulator register
    long ar;                // Auxiliary register
    boolean overflow;       // Overflow flag
    boolean fpOverflow;     // Floating point overflow

    long ir;                // Last fetched instruction pair
    int irx;                // Instruction to execute
    int scr;                // Sequence control register
    int scr2;               // 0 = first instruction, 1 = second instruction

    // Variables used during execution
    AtomicBoolean running;
    boolean jump;
    Trace trace;
    
    // Variables used to control instruction timing
    boolean useSpin;
    int cycleNano, cycles;
    long spinPause, sleepPause;
    long busyStart; 
    
    // Variables used to calculate relative speed
    AtomicBoolean realTime;
    AtomicLong cpuStart, cpuBusy, cpuCycles; 

    public CPU(Computer computer) {
        this.computer = computer;
        running = new AtomicBoolean();
        realTime = new AtomicBoolean();
        cpuStart = new AtomicLong();
        cpuBusy = new AtomicLong();
        cpuCycles = new AtomicLong();
        setCycleTime(288);              // Default cycle time is 288us
        calibrate();
        
        if (Computer.debug) {
            System.out.println("CPU:");
            System.out.println("  sleep time:   " + sleepPause);
            System.out.println("  spin time:    " + spinPause);
            System.out.println("  pause method: " + (useSpin ? "spin" : "sleep"));
        }
    }
    
    // Set the basic cycle time in micro-seconds
    public void setCycleTime(int us) {
        cycleNano = us*1000;
    }
    
    // Set the next instruction to be executed
    public void setInstruction(int instruction) {
        synchronized(this) {
            irx = Instruction.asInstr(instruction);
            viewState();
        }    
    }
  
    // Stop execution after the current instruction
    public void stop() {
        running.set(false);
        computer.console.setStep(true);
        cpuCycles.set(0);
    }

    // Reset the CPU - clears overflow and busy states and stops execution
    public void reset() {
        stop();
        computer.busyClear();
        computer.console.setOverflow(false, false);
        computer.console.setBusy(false);
        synchronized (this) {
            acc = ar = 0;
            scr2 = scr = 0;
            ir = irx = 0;
            overflow = fpOverflow = false;
        }
    }
    
    // Exit execution.  Does not advance the instruction counter, so
    // execution can resume and restart the same instruction.
    public void exit() {
        stop();
        synchronized (this) {
            jump = true;
        }    
    }

    // Normal execution, run instructions until told to stop.
    public void run() {
        computer.console.setStep(false);
        
        cpuStart.set(System.currentTimeMillis());
        cpuBusy.set(0);
        cpuCycles.set(0);

        long now = System.nanoTime();
        long end = now;
        
        running.set(true);
        while (running.get()) {
            synchronized (this) {
                obey();
                cpuCycles.addAndGet(cycles);

                // It is hard to get timings exact in Java.  This logic assumes we 
                // are running too fast and adds a delay when enough time has 
                // accumulated to for a delay to get us back on track.  This means 
                // that each instruction may not have an exact timing but the CPU 
                // should average out at the correct speed.
                if (realTime.get()) {
                    // If the last instruction caused a 'busy' wait, timings will
                    // be messed up, so simply reset them.  There's no need to pause
                    // as the busy wait will have more than covered the time. 
                    if (busyStart != 0) {
                        now = end = System.nanoTime();
                    } else {
                        end += cycles*cycleNano;
                        long pause = end-now;
                        if (pause > sleepPause) {
                            try { 
                                Thread.sleep(pause/1000000, (int)pause%1000000);
                            } catch (InterruptedException e) { }
                            now = System.nanoTime();
                        } else if (useSpin) {
                            while (end-now > spinPause) { 
                                now = System.nanoTime();
                            }    
                        }
                    }    
                }

                // Ensure the block transfer and busy lights are off when an instruction 
                // finally completes.  We have to do this here because we want the lights
                // to remain on while an I/O operation/ occurs, including any delay added
                // to simulate real-time speed.  This is a little bit of a hack.
                computer.console.setBlockTr(false);
                computer.console.setBusy(false);
            }
        }
    }

    // Obey the next instruction. 
    public void obey() {
        synchronized (this) {
            // Execute the instruction
            execute();

            // Step to the next instruction, unless we had jump in which case the 
            // new address will already be set in scr/scr2.
            if (!jump) {
                if (scr2 == 0) {
                    scr2 = 1;
                } else {
                    scr2 = 0;
                    scr = Instruction.getAddr(scr + 1);
                }
            }
            
            // Fetch next instruction and display state
            fetch();
            viewState();
        }
    }
    
    // Fetch the next instruction
    void fetch() {
        if (scr2 == 0) {
            // Processing first instruction, so just fetch from store
            ir = computer.core.fetch(scr);
            irx = Word.getInstr1(ir);
        } else {
            // Processing of second instruction depends on B digit setting,
            // unless we got here via a jump.
            if (jump || Word.getB(ir) == 0) {
                // No modification needed but we must re-fetch the instruction
                // word in case the previous instruction modified it.
                ir = computer.core.fetch(scr);
                irx = Word.getInstr2(ir);
            } else {
                // B-line modification is applied to the original value of the 
                // second instruction (even if it has been modified in store).
                long b = computer.core.read(Instruction.getAddr(irx));
                irx = Word.getInstr2(ir) + Word.getInstr2(b);
            }
        }

        // Trace each new instruction pair or following a jump
        if (trace != null) {
            if (jump || scr2 == 0)
                trace.trace(scr, scr2, ir, acc, overflow);
        }  
    }

    // Execute a single instruction
    void execute() {
        int op = Instruction.getOp(irx);
        int addr = Instruction.getAddr(irx);

        // Start the initial sound sample
        computer.console.soundSpeaker(op > 037, 1);
        
        // Perform the operation.  Default cycle time is 576us (2 cycles)
        busyStart = 0;
        jump = false;
        cycles = 2;
        switch (op >> 3) {
            case 0: case 1: case 2: 
            case 3: group0123(op, addr);  break;
            case 4: group4(op, addr);  break;
            case 5: group5(op, addr);  break;
            case 6: group6(op, addr);  break;
            case 7: group7(op, addr);  break;
        }
        
        // Complete the sound sample unless we had a busy wait
        if (busyStart == 0) {
            computer.console.soundSpeaker(false, cycles-1);
        }    

        // Update console lights to track overflow states
        computer.console.setOverflow(overflow, fpOverflow);
        if (fpOverflow) {
            // Floating point overflow should wait for Operate before continuing
            computer.console.suspend();
            fpOverflow = false;
            computer.console.setOverflow(overflow, fpOverflow);
        }
    }

    // Execute a group 0 to group 3 arithmetic/storage instruction
    void group0123(int op, int addr) {
        // We need the current content of the storage address and, for some instructions
        // either the store value or the accumulator.
        long a = acc;
        long n = computer.core.read(addr);
        long x = ((op & 010) == 0) ? a : n;

        // Calculate the result
        long result = 0;
        switch (op & 007) {
            case 0: result = computer.alu.add(0, x);  break;    // Do nothing
            case 1: result = computer.alu.sub(0, x);  break;    // Negate
            case 2: result = computer.alu.add(1, n);  break;    // Increment
            case 3: result = computer.alu.and(a, n);  break;    // Collate
            case 4: result = computer.alu.add(a, n);  break;    // Add
            case 5: result = computer.alu.sub(a, n);  break;    // Subtract
            case 6: result = computer.alu.add(0, 0);  break;    // Clear
            case 7: result = computer.alu.sub(n, a);  break;    // Negate and add
        }
        overflow |= computer.alu.isOverflow();

        // Destination of result depends on opcode group
        switch (op >> 3) {
            case 0: acc = result;  x = n;  break;
            case 1: acc = result;  x = a;  break;
            case 2: acc = a;  x = result;  break;
            case 3: acc = n;  x = result;  break;
        }
        computer.core.write(addr, x);
        cycles = 2;                         // All instructions take 576us (2 cycles)
    }

    // Execute a group 4 jump instruction
    void group4(int op, int addr) {
        switch (op & 003) {
            case 0: jump = true;  break;                        // Unconditional
            case 1: jump = computer.alu.isNeg(acc);  break;     // Jump if accumulator negative
            case 2: jump = computer.alu.isZero(acc);  break;    // Jump if accumulator zero
            case 3: jump = overflow;  overflow = false;  break; // Jump if overflow (and clear)
        }

        if (jump) {                         // Jump required
            scr = addr;                     //   set new sequence control address
            scr2 = (op & 007) >> 2;         //   and set first/second instruction indication
        }
        cycles = 1;                         // Jumps take just 288us (1 cycle)
    }

    // Execute a group5 logical/arithmetic instruction
    void group5(int op, int addr) {
        long n = computer.core.read(addr);
        int s = addr & 0x7F;                // Shifts work with lower 7 bits only 
        if ((op & 001) != 0) {
            switch (op & 007) {
                case 1: acc = computer.alu.shr(acc, s); ar = 0; cycles = s+2; break;
                case 5: acc = computer.alu.shl(acc, s); ar = 0; cycles = s+2; break;
                case 3: acc = computer.alu.mul(acc, n); ar = 0; cycles = 43-y(); break;
                case 7: acc = ar; cycles = 2; break;  // Note: does NOT clear aux register
            }
        } else {
            switch (op & 007) {
                case 0: acc = computer.alu.longShr(acc, ar, s); cycles = s+2; break;
                case 4: acc = computer.alu.longShl(acc, ar, s); cycles = s+2; break;
                case 2: acc = computer.alu.longMul(acc, n); cycles = 42-y();  break;
                case 6: acc = computer.alu.longDiv(acc, ar, n); cycles = 42;  break;
            }
            ar = computer.alu.getExtension();
        }
        overflow |= computer.alu.isOverflow();
    }
    
    // Returns the number of consecutive 1's or 0's at the left hand end (MSB)
    // of the accumulator - this is needed for some instruction timings.
    int y() {
        int y;
        long a = acc;
        long bit = acc & Word.SIGN_BIT;
        for (y = 0; y < 39 && (a & Word.SIGN_BIT) == bit; y++)
            a <<= 1;
        return y;
    }

    // Execute a group 6 floating point instruction
    void group6(int op, int addr) {
        // These instructions are the same as an 00 op code, unless the FPU is available.
        if (computer.fpu != null) {
            long n = computer.core.read(addr);
            switch (op & 007) {
                case 0: acc = computer.fpu.add(acc, n); cycles = 3;   break;
                case 1: acc = computer.fpu.sub(acc, n); cycles = 3;   break;
                case 2: acc = computer.fpu.sub(n, acc); cycles = 3;   break;
                case 3: acc = computer.fpu.mul(acc, n); cycles = 17;  break;
                case 4: acc = computer.fpu.div(acc, n); cycles = 34;  break;
                case 5:
                    if (addr < 4096) {
                        acc = computer.fpu.shl(acc, addr % 64); cycles = 2;
                    } else {
                        acc = computer.fpu.convert(acc); cycles = 2;
                    }
                    break;
                case 6: acc = computer.fpu.sdiv(acc, n); cycles = 16; break;
                case 7: acc = computer.fpu.sqrt(acc); cycles = 15;    break;
            }
            // All group 6 instructions clear the aux register
            ar = 0;

            // Set the overflow flags
            overflow |= computer.fpu.isOverflow();
            fpOverflow |= computer.fpu.isFpOverflow();
        }
    }

    // Execute a group 7 control/peripheral instruction
    void group7(int op, int addr) {
        switch (op & 007) {
            // 70 and 73 are standard instructions
            case 0:
                acc = computer.console.read();
                break;
                
            case 3:
                // According to Bill Purvis (on his excellent web pages about the 803 Algol compiler),
                // the 73 instruction puts the address in the upper and lower half of the storage word.
                // I can't think why it would need to do this ... but I believe him, so I'll do the same!
                computer.core.write(addr, Word.asInstr(scr, 0, scr));
                break;

            // 71 and 74 read and write the paper tape readers and punches via the PTS.
            case 1:
                acc |= computer.pts.read(addr);
                break;
            case 4:
                computer.pts.write(addr);
                break;

            // 72 and 75 write and read the 'control' mode devices
            case 2:
                computer.devices.controlWrite(addr, acc);
                break;
            case 5:
                long a = computer.devices.controlRead(addr);
                if (a != Word.NOTHING)
                    acc = a;
                break;

            // 76 and 77 access the 'block' mode devices
            case 6: case 7:
                // TODO: block devices not implemented yet!
                break;
        }
    }

    // Set Real time execution speed
    public void setRealTime(boolean rt) {
        realTime.set(rt);
    }
 
    // Return the approximate CPU speed as a multiple of a real 803 CPU.
    // This method is expected to be called periodically.
    public float getSpeed() {
        float factor = 0;
        if (cpuCycles.get() > 0) {
            float cpuTime = System.currentTimeMillis() - cpuStart.get() - cpuBusy.get();
            factor = (float)(cpuCycles.get()*(cycleNano/1000))/(cpuTime*1000);

            // Reset counters ready for next call
            cpuStart.set(System.currentTimeMillis());
            cpuBusy.set(0);
            cpuCycles.set(0);
        }  
        return factor;
    }
    
    // Called to indicate start/end of 'busy' wait.  The time in busy
    // waits needs to be excluded when calculating CPU speed.
    public synchronized void busy(boolean start) {
        if (start) {
            busyStart = System.currentTimeMillis();
        } else {
            if (busyStart != 0) {
                cpuBusy.addAndGet(System.currentTimeMillis() - busyStart);
            }    
        }
    }

    // Add additional 'cycles' for real-time device control
    public synchronized void addDelay(int us) {
        cycles += (us*1000)/cycleNano;
    }
    
    // Attempt to calibrate the CPU timings used for real-time operation
    void calibrate() {
        // We need to know roughly how long it takes to read the nanosecond
        // timer and what the typical minimum Thread.sleep() period is.
        long nt = 0, st = 0;
        for (int i = 0; i < 5; i++) {
            long t1 = System.nanoTime();
            long t2 = System.nanoTime();
            try { Thread.sleep(0, 1); } catch (InterruptedException e) { }
            long t3 = System.nanoTime();
            nt += (t2-t1);
            st += (t3-t2);
        }    
        spinPause = nt/5;
        sleepPause = st/5;
        
        // If the minimum thread sleep time is short enough to cover a few 
        // typical instructions (say 20 576us instructions) we can use sleeps
        // to control timing, otherwise we need to use spin loops.
        useSpin = (sleepPause > 20*2*cycleNano);
    }

    // Dump
    public synchronized void dump(Dump dump) {
        dump.acc = acc;
        dump.ar = ar;
        dump.ir = ir;
        dump.ix = irx;
        dump.scr = scr;
        dump.scr2 = scr2;
        dump.overflow = overflow;
        dump.fpOverflow = fpOverflow;
    }

    // Trace
    public synchronized void trace(Trace trace) {
        this.trace = trace;
        viewTrace();
    }

    // Mainly for debugging
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("CPU:");
        sb.append(" acc=").append(Word.toOctalString(acc));
        sb.append(" scr=").append(scr).append(".").append(scr2);
        sb.append(" ir=\"").append(Word.toInstrString(ir)).append("\"");
        sb.append(" overflow=").append(overflow);
        return sb.toString();
    }

    /*
     * GUI visualisation
     */

    CpuView view;

    public void setView(CpuView view) {
        this.view = view;
    }

    void viewState() {
        if (view != null) {
            view.updateRegisters(acc, ar, irx, scr, ir);
            view.updateFlags(overflow, fpOverflow);
        }
    }
    
    void viewTrace() {
        if (view != null) {
            view.updateTrace(trace != null);
        }
    }
}
