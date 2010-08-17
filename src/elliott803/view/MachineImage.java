/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2010
 */
package elliott803.view;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import elliott803.machine.Computer;
import elliott803.machine.Dump;

/**
 * A saved machine image.  Currently  this consists of a core dump 
 * and the size and position of all the windows on the screen.
 * 
 * @author Baldwin
 */
public class MachineImage {

    public Dump imageDump;
    public ViewImage imageViewDef;

    public MachineImage() {
    }
    
    public MachineImage(Computer computer, ComputerView view) {
        imageDump = new Dump(computer);
        imageViewDef = new ViewImage(view);
    }
    
    /*
     * Apply the machine image to the current simulation
     */
    public void apply(Computer computer, ComputerView view) {
        computer.core.restore(imageDump);
        if (imageViewDef != null) 
            imageViewDef.layout(view);
    }
    
    /*
     * Write this machine image to a file
     */
    public void write(File file) {
        try {
            OutputStream stream = new DeflaterOutputStream(new FileOutputStream(file));
            imageDump.write(stream);
            if (imageViewDef != null)
                imageViewDef.write(stream);
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /*
     * Read a previously saved image
     */
    public static MachineImage readImage(File file) {
        // Machine image contains a core dump followed by an 
        // optional view image.
        MachineImage machine = new MachineImage();
        try {
            InputStream stream = new InflaterInputStream(new FileInputStream(file));
            machine.imageDump = Dump.readDump(stream);
            machine.imageViewDef = ViewImage.readViewDef(stream);
            stream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return machine;
    }
}
