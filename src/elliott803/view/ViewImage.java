/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2009,2010
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
    public Rectangle position, normalPosition;
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
        normalPosition = desktop.getBounds();
        
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
        position = frame.getBounds();
        normalPosition = frame.getNormalBounds();
        isMin = frame.isIcon();
        isMax = frame.isMaximum();
    }

    /*
     * Layout windows according to this saved image
     */
    public void layout(JDesktopPane desktop) {
        // Restore the desktop position
        JFrame app = (JFrame)desktop.getTopLevelAncestor();
        desktop.setPreferredSize(normalPosition.getSize());
        app.setBounds(position);
        app.pack();
        
        // Remove all the existing frames from the desktop and index them
        // by title, remembering their original order.
        Map<String,JInternalFrame> index = new LinkedHashMap<String,JInternalFrame>();
        for (JInternalFrame frame : desktop.getAllFrames()) {
            index.put(frame.getTitle(), frame);
            desktop.remove(frame);
        }
        
        // Now add back any saved frames in the saved order and positions
        for (ViewImage def : windows) {
            JInternalFrame frame = index.get(def.title);
            if (frame != null) {
                desktop.add(frame);
                def.layout(frame);
                index.remove(def.title);
            }
        }
        
        // Add back any frames not present in the saved image
        for (JInternalFrame frame : index.values()) {
            desktop.add(frame);
        }
    }
    
    public void layout(JInternalFrame frame) {
        frame.setBounds(position);
        frame.setNormalBounds(normalPosition);
        try {
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
    public static ViewImage readViewDef(InputStream stream) throws IOException, ClassNotFoundException {
        ViewImage def = null;
         try {
             ObjectInputStream in = new ObjectInputStream(stream);
             def = (ViewImage)in.readObject();
         } catch (EOFException e) {
             // End of stream, so return null
         }
         return def; 
     }
}
