/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2009
 */
package elliott803.machine;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

/**
 * Encapsulates the information that makes up the system dump file
 *
 * @author Baldwin
 */
public class Dump implements Serializable {
    private static final long serialVersionUID = 1L;

    // Identification
    public String name;        // System name;
    public String version;     // System version
    public Date timestamp;     // Time-stamp of dump

    // Computer state
    public boolean busy;       // Busy state

    // CPU status
    public long acc;           // Accumulator
    public long ar;            // Auxiliary register
    public long br;            // B-register
    public long ir;            // Executing instruction
    public int scr;            // Sequence control;
    public int scr2;           // First/second instruction
    public boolean overflow;   // Overflow state;
    public boolean fpOverflow;

    // Storage
    public long[] core;        // Core store

    /*
     * Create a dump
     */
    public Dump(Computer computer) {
        name = computer.name;
        version = computer.version;
        timestamp = new Date();

        computer.dump(this);
        computer.cpu.dump(this);
        computer.core.dump(this);
    }

    /*
     * Write a dump file.
     */
    public void write() {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd-HHmmss-SSS");
        String filename = "elliott-" + df.format(timestamp) + ".core";
         try {
            FileOutputStream fout = new FileOutputStream(filename);
            DeflaterOutputStream zout = new DeflaterOutputStream(fout);
            ObjectOutputStream out = new ObjectOutputStream(zout);
            out.writeObject(this);
            out.close();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

   /*
    * Read a dump file.
    */
    public static Dump readDump(String filename) {
        Dump dump = null;
        try {
            FileInputStream fin = new FileInputStream(filename);
            InflaterInputStream zin = new InflaterInputStream(fin);
            ObjectInputStream in = new ObjectInputStream(zin);
            dump = (Dump)in.readObject();
            in.close();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        return dump;
    }
}
