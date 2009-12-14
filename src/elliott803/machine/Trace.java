/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2009
 */
package elliott803.machine;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

/**
 * Encapsulates the information that makes a trace file
 *
 * @author Baldwin
 */
public class Trace implements Serializable {
    private static final long serialVersionUID = 1L;

    static final int BUFFER_SIZE = 8192;

    public static class Entry implements Serializable {
        private static final long serialVersionUID = 1L;

        public int scr, scr2;
        public long ir;
        public long acc;
        public boolean oflow;

        public Entry(int scr, int scr2, long ir, long acc, boolean oflow) {
            this.scr = scr;
            this.scr2 = scr2;
            this.ir = ir;
            this.acc = acc;
            this.oflow = oflow;
        }
    }

    // Identification
    public String name;         // System name;
    public String version;      // System version
    public Date timestamp;      // Time-stamp of dump

    transient Entry[] traceLog;
    transient int maximum;
    transient int position;

    transient ObjectOutputStream out;
    transient ObjectInputStream in;

    public Trace(Computer computer) {
        name = computer.name;
        version = computer.version;
        timestamp = new Date();

        maximum = BUFFER_SIZE;
        position = 0;
        traceLog = new Entry[maximum];
    }

    // Log a new trace entry
    public void trace(int scr, int scr2, long ir, long acc, boolean oflow) {
        if (position == maximum) {
            flush(false);
            position = 0;
        }
        traceLog[position++] = new Entry(scr, scr2, ir, acc, oflow);
    }

    // Write and close the trace file
    public void write() {
        flush(true);
    }

    // Flush buffer to disk
    void flush(boolean close) {
        if (out == null) {
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd-HHmmss-SSS");
            String filename = "elliott-" + df.format(timestamp) + ".trace";
            try {
                FileOutputStream fout = new FileOutputStream(filename);
                DeflaterOutputStream zout = new DeflaterOutputStream(fout);
                out = new ObjectOutputStream(zout);
                out.writeObject(this);
            } catch (IOException e) {
                System.err.println(e);
            }
        }
        if (out != null) {
            try {
                for (int i = 0; i < position; i++) {
                    out.writeObject(traceLog[i]);
                }
                if (close) {
                    out.close();
                    out = null;
                }
            } catch (IOException e) {
                System.err.println(e);
            }
        }
    }

    // Return trace entries from saved trace file
    public Entry nextEntry() {
        Entry entry = null;
        if (in != null) {
            try {
                entry = (Entry)in.readObject();
            } catch (Exception e) {
                // Assume end of file reached
            }
        }
        return entry;
    }

    /*
     * Read a trace file
     */
    public static Trace readTrace(String filename) {
        Trace trace = null;
        try {
            FileInputStream fin = new FileInputStream(filename);
            InflaterInputStream zin = new InflaterInputStream(fin);
            ObjectInputStream in = new ObjectInputStream(zin);
            trace = (Trace)in.readObject();
            trace.in = in;
        } catch (EOFException e) {
            // End of file reached
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        return trace;
    }
}
