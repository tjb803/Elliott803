/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2010
 */
package elliott803.view;

import java.awt.Rectangle;
import java.beans.PropertyVetoException;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;

/**
 * Image of the GUI simulator view.  This contains the sizes and positions of all
 * the windows.
 *
 * @author Baldwin
 */
public class ViewImage implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public String title;
    public Rectangle position, position2;
    public boolean isMin, isMax;
    public List<ViewImage> windows;
    
    /*
     * View image for the complete layout
     */
    public ViewImage(JDesktopPane desktop) {
        // Record the position of our top level frame
        JFrame app = (JFrame)desktop.getTopLevelAncestor();
        title = app.getTitle();
        position = app.getBounds();
        position2 = desktop.getBounds();
        
        // And the positions of everything within it. Need to maintain the 
        // original order here as that defines the window Z-ordering.
        windows = new ArrayList<ViewImage>();
        for (JInternalFrame frame : desktop.getAllFrames()) {
            windows.add(new ViewImage(frame));
        }
    }
    
    /*
     * View image for a single window
     */
    public ViewImage(JInternalFrame frame) {
        title = frame.getTitle();
        position = frame.getNormalBounds();
        isMin = frame.isIcon();
        isMax = frame.isMaximum();
    }

    /*
     * Layout windows according to this saved image
     */
    
    // Layout the main application desktop
    public void layout(JDesktopPane desktop) {
        // Restore the desktop position
        JFrame app = (JFrame)desktop.getTopLevelAncestor();
        desktop.setPreferredSize(position2.getSize());
        desktop.setBounds(position2);
        app.setBounds(position);
        app.validate();

        // Remove all the existing frames from the desktop and index them
        // by title, remembering their original order.
        Map<String,JInternalFrame> index = new LinkedHashMap<String,JInternalFrame>();
        for (JInternalFrame frame : desktop.getAllFrames()) {
            index.put(frame.getTitle(), frame);
        }
        desktop.removeAll();
 
        // Now add back any saved frames in their original order and position
        for (ViewImage window : windows) {
            JInternalFrame frame = index.remove(window.title);
            if (frame != null) {
                desktop.add(frame);
                window.layout(frame);
            }
        }
        
        // Add back any frames not present in the saved image
        for (JInternalFrame frame : index.values()) {
            desktop.add(frame);
        }
    }
    
    // Layout an individual window inside the desktop
    public void layout(JInternalFrame frame) {
        try {
            frame.setIcon(false);
            frame.setMaximum(false);
            
            // If the window should not be re-sized we just restore its 
            // position.  Re-sizable windows restore position and size.
            frame.setLocation(position.getLocation());
            if (frame.isResizable()) 
                frame.setSize(position.getSize());
            
            if (isMin) frame.setIcon(true);
            if (isMax) frame.setMaximum(true);
        } catch (PropertyVetoException e) {
        }
    }
    
    /*
     * Write a view image.
     */
    public void write(OutputStream stream) throws IOException {
        ObjectOutputStream out = new ObjectOutputStream(stream);
        out.writeObject(this);
        out.flush();
    }
    
    /*
     * Read a view image.  Input stream may be empty, in which case
     * there is nothing to read.
     */
    public static ViewImage readImage(InputStream stream) throws IOException, ClassNotFoundException {
        ViewImage view = null;
         try {
             ObjectInputStream in = new ObjectInputStream(stream);
             view = (ViewImage)in.readObject();
         } catch (EOFException e) {
             // End of stream, so return null
         }
         return view; 
     }
}
