/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2009
 */
package elliott803.view;

import java.io.EOFException;
import java.io.InputStream;
import java.io.ObjectInputStream;

/**
 * Definition of a UI simulator view.  This contains the sizes and positions of all
 * the windows.
 *
 * @author Baldwin
 */
public class ViewDefinition {

    public boolean empty;

    public int frameX, frameY;
    public int viewWidth, viewHeight;

    /*
     * Default view definition
     */
    public ViewDefinition() {
        empty = true;
    }
    
    /*
     * Read a view definition.  Input stream may be empty, in which case
     * there is nothing to read.
     */
    public static ViewDefinition readViewDef(InputStream stream) throws EOFException {
        ViewDefinition def = null;
         try {
             ObjectInputStream in = new ObjectInputStream(stream);
             def = (ViewDefinition)in.readObject();
         } catch (EOFException e) {
             // View definition not present in the stream, so return a default
             def = new ViewDefinition();
         } catch (Exception e) {
             e.printStackTrace(System.err);
         }
         return def; 
     }
}
