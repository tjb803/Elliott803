/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2009
 */
package elliott803.machine;

import elliott803.hardware.ALU;
import elliott803.hardware.CPU;
import elliott803.hardware.Console;
import elliott803.hardware.FPU;
import elliott803.hardware.PaperTapeStation;
import elliott803.hardware.Plotter;
import elliott803.hardware.Store;
import elliott803.machine.extension.RandomNumberDevice;
import elliott803.machine.extension.SimulatorControlDevice;

/**
 * The complete computer.
 *
 * @author Baldwin
 */
public class Computer extends Thread {

    public static final int ACT_WAIT = 0;
    public static final int ACT_CLEAR = 1;
    public static final int ACT_STEP = 2;
    public static final int ACT_RUN = 3;

    // Version
    public String name;
    public String version;

    // The collection of hardware that makes up the complete computer
    public Store core;
    public CPU cpu;
    public ALU alu;
    public FPU fpu;
    public PaperTapeStation pts;
    public Console console;
    public Plotter plotter;

    public DeviceManager devices;

    // Flags to handle "busy" signalling
    boolean busyExit;
    boolean busyWait;

    /**
     * Construct a computer
     */
    public Computer() {
        // Get name and version information from manifest, or set default
        name = getClass().getPackage().getImplementationTitle();
        version = getClass().getPackage().getImplementationVersion();
        if (name == null || version == null) {
            name = "Elliott 803B";
            version = "0.0.0";
        }  
        
        // Lower the priority of the CPU thread a little.  803 programs often used tight
        // spin loops to wait for console input or when they end and we don't want these
        // tight loops to make the GUI seem unresponsive.
        setName(name);
        setPriority(Math.max(Thread.MIN_PRIORITY, getPriority()/2));
        
        // An 8K core store
        core = new Store(this);

        // The CPU, ALU and FPU
        cpu = new CPU(this);
        alu = new ALU(this);
        fpu = new FPU(this);

        // The paper tape station
        pts = new PaperTapeStation(this);

        // Plotter
        plotter = new Plotter(this);

        // Register any additional control or block transfer devices
        devices = new DeviceManager(this);
        devices.addControlDevice(plotter);
        devices.addControlDevice(new RandomNumberDevice());
        devices.addControlDevice(new SimulatorControlDevice());

        // And the control console - start in step-by-step mode
        console = new Console(this);
        console.setStep(true);
        
        // Default to real time mode
        setRealTime(true);
    }

    /*
     * Signalling for the "busy wait" condition
     */
    public synchronized void busyWait() {
        console.setBusy(true);
        if (busyExit) {
            cpu.stop();
        } else {
            busyWait = true;
            cpu.busy(busyWait);
            while (busyWait) {
                try {
                    wait();
                } catch (InterruptedException e) {
                }
            }
        }
    }

    public synchronized void busyClear() {
        if (busyWait) {
            busyWait = false;
            cpu.busy(busyWait);
            notify();
        }
        console.setBusy(false);
    }

    public void dump(Dump dump) {
        dump.busy = busyWait;
    }
    
    /*
     * Set the simulation to run at real 803B speed
     */
    public void setRealTime(boolean rt) {
        // TODO: Currently only the CPU can run in real time - devices  
        // run as fast as possible.
        cpu.setRealTime(rt);
    }

    /*
     * Run the simulation on the current thread, stopping if the system
     * enters a busy wait condition.  This is used by the command line 
     * version of the simulator.
     */

    public void runInstructions(int addr) {
        cpu.setInstruction(Instruction.asInstr(040, addr));
        runInstructions();
    }

    public void runInstructions() {
        busyExit = true;
        cpu.run();
    }

    /*
     * Run the simulation on a worker thread.  This is used by the GUI 
     * version of the simulator - operated via the Console - to ensure the
     * Swing event dispatch thread does not perform any long running tasks. 
     */

    Boolean threadRun = Boolean.FALSE;
    int action = ACT_WAIT;

    public void run(int act) {
        synchronized (threadRun) {
            action = act;
            threadRun.notify();
        }
    }

    public void run() {
        // Flag to show we are running on a thread and to synchronise thread operations
        threadRun = new Boolean(true);
        
        while (true) {
            int act = ACT_WAIT;
            synchronized (threadRun) {
                try {
                    threadRun.wait();
                    act = action;
                    action = ACT_WAIT;
                } catch (InterruptedException e) {
                }
            }
            switch (act) {
                case ACT_STEP:
                    cpu.obey();
                    break;
                case ACT_RUN:
                    cpu.run();
                    break;
                case ACT_CLEAR:
                    cpu.stop();
                    core.clear();
                    break;
            }
        }
    }

    /*
     * Debugging functions
     */

    Trace trace;

    public void dump() {
        Dump dump = new Dump(this);
        dump.write();
    }

    public void traceStart() {
        if (trace == null) {
            trace = new Trace(this);
            cpu.trace(trace);
        }
    }

    public void traceStop() {
        if (trace != null) {
            cpu.trace(null);
            trace.write();
            trace = null;
        }
    }
    
    /*
     * Dummy constructor for unit tests
     */
    public Computer(boolean test) {
        // Dummy for unit tests only
        cpu = new CPU(this);
    }
}
