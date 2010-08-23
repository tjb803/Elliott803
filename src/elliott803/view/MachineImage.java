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
    public ViewImage imageView;

    public MachineImage() {
    }
    
    public MachineImage(Computer computer, ComputerView view) {
        imageDump = new Dump(computer);
        imageView = new ViewImage(view);
    }
    
    /*
     * Apply the machine image to the current simulation
     */
    public boolean apply(Computer computer, ComputerView view) {
        boolean layout = false;
        computer.cpu.reset();
        computer.core.restore(imageDump);
        if (imageView != null) {
            layout = imageView.layout(view);
        }  
        return layout;
    }
    
    /*
     * Write this machine image to a file
     */
    public void write(File file) {
        try {
            OutputStream stream = new DeflaterOutputStream(new FileOutputStream(file));
            imageDump.write(stream);
            if (imageView != null)
                imageView.write(stream);
            stream.close();
        } catch (IOException e) {
            System.err.println(e);
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
            machine.imageView = ViewImage.readImage(stream);
            stream.close();
        } catch (Exception e) {
            System.err.println(e);
        }
        return machine;
    }
}
