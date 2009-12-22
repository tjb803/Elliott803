/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2009
 */
package elliott803.view;


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
}
