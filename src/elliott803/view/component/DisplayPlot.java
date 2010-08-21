/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2010
 */
package elliott803.view.component;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.Scrollable;

/**
 * Displays the output of the plotter.  The output is scaled so that the 
 * current panel width is equivalent to 1100 plotter units (the plotter pen
 * moves in 1/100 inch increments and the paper is 11 inches wide).  The
 * height is set to whatever size is needed to display all the current
 * output scaled by the same factor as the width. 
 * 
 * @author Baldwin
 */

// TODO: Currently this keeps a list of all the lines that need to be drawn to
// make the current image and redraws them all each time paintComponent is called.
// It relies on this being fast since firstly drawing lines is pretty quick and 
// secondly many of them will probably be clipped out (as we're only painting areas
// of the image that have changed).  But it is still probably not the best way to 
// do it as with the plotter moving in 1/100th inch there are likely to be a lot
// of little line segments to draw (although I do try to combine any that are in
// in same direction as the last one).
//
// Maybe better to draw into an off-screen image bitmap (kept in plotter units) 
// and then just scale and blit this onto the screen during paintComponent. The 
// off screen image will need to be resized as the y-extents change though.

public class DisplayPlot extends JPanel implements Scrollable, ComponentListener {
    private static final long serialVersionUID = 1L;

    // Minimum and maximum plotter y coordinate seen so far.
    int minY, maxY;
    
    // Transform to scale and translate plotter coordinates to output area.
    AffineTransform transform;
    
    // segments contains the set of move/draw instructions to draw the complete
    // output.  Segments are stored in plotter coordinates.
    // p1 and p2 contain the last two points in output area coordinates.
    // r1 is a rectangle containing the bounds of p1 and p2.
    List<Segment> segments;
    Point p1, p2;
    Rectangle r1;
    
    public DisplayPlot() {
        setBackground(Color.WHITE);
        addComponentListener(this);

        segments = Collections.synchronizedList(new ArrayList<Segment>());
        plotClear();
    }

    // Add a new line or move segment.  We need to retain all the drawing
    // instructions so the image can be redrawn if the window is repainted.
    public void plotDraw(int x, int y) {
        plotDraw(x, y, 0);
    }
    
    public void plotMove(int x, int y) {
        plotMove(x, y, 0);
    }
    
    public void plotDraw(int x, int y, int dir) {
        Segment lastSeg = segments.get(segments.size()-1);
        // Attempt to merge any drawing that is in the same direction last time
        if (lastSeg.draw && lastSeg.dir != 0 && lastSeg.dir == dir) {
            lastSeg.x = x;  lastSeg.y = y;
            mapToPoint(lastSeg, false);
        } else {
            segments.add(mapToPoint(new Segment(true, x, y, dir), true));
        }  
        
        // Calculate the area of the screen that needs redrawing to display
        // the new line segment.
        if (y < minY || y > maxY) {
            minY = Math.min(y - 10, minY);
            maxY = Math.max(y + 10, maxY);
            setTransform();     // Height has changed so recalculate the 
            revalidate();       // transform and reset any scrollbars.
            repaint();
        } else {
            repaint(r1);
        }
        scrollRectToVisible(r1);
    }
    
    public void plotMove(int x, int y, int dir) {
        Segment lastSeg = segments.get(segments.size()-1);
        // All move instructions can be merged as nothing is visible
        if (!lastSeg.draw) {
            lastSeg.x = x;  lastSeg.y = y;
            mapToPoint(lastSeg, false);
        } else {    
            segments.add(mapToPoint(new Segment(false, x, y, dir), true));
        } 
        
        // No need to redraw after a move, as there's nothing new to see.
    }
    
    // Clear current output
    public void plotClear() {
        segments.clear();
        segments.add(new Segment(false, 0 ,0, 0));
        p1 = new Point();  p2 = new Point();
        r1 = new Rectangle();
        minY = 9999;  maxY = -9999;
        setTransform();
        revalidate();
        repaint();
    }
    
    // Redraw the complete plotter output so far
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Redraw all the output.  We rely on the clipping rectangle to have been
        // set to avoid actually redrawing everything.
        p2.setLocation(0, 0);
        mapToPoint(segments.get(0), true);
        synchronized (segments) {
            for (int i = 1; i < segments.size(); i++) {
                if (mapToPoint(segments.get(i), true).draw) {
                    g.drawLine(p1.x, p1.y, p2.x, p2.y);
                }
            }   
        }    
    }
    
    // Create a transform to scale the output so that the window width is 
    // equivalent to 1100 plotter units and so that output is in the centre 
    // of the window.  Also reflect about the y-axis (y scale factor is set 
    // negative) to make positive y-values go up rather than down.
    private void setTransform() {
        double scale = getWidth()/1100.0;
        transform = new AffineTransform();
        transform.translate(0, (getHeight() + (maxY+minY)*scale)/2);
        transform.scale(scale, -scale);
    }
    
    // Map a segment in plotter space to a point in the window space.
    // Point p2 is updated with the new point, if 'add' is true previous
    // p2 is stored in p1 (as a new point has been added).  Note this reuses
    // p1/p2 Point instances to try to avoid excessive object allocations.
    private Segment mapToPoint(Segment seg, boolean add) {
        if (add) {
            Point p = p2;  p2 = p1;  p1 = p;
        }    
        p2.x = seg.x;  p2.y = seg.y;
        transform.transform(p2, p2);
        
        // Generate the bounding rectangle for the last two points.  This is extended
        // by a 1 pixel boarder to allow for rounding errors in the transform.
        r1.setBounds(p1.x, p1.y, 0, 0);  r1.add(p2);  r1.grow(1, 1);
        
        // Return segment parameter for convenience
        return seg;
    }
    
    /*
     * Implement the Scrollable interface so this panel works nicely 
     * inside a scroll pane.
     */
    public Dimension getPreferredSize() {
        return new Dimension(getWidth(), (int)((maxY-minY)*transform.getScaleX()));
    }

    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return visibleRect.height/10;   // Can only have vertical scrolling
    }

    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return visibleRect.height;      // Can only have vertical scrolling
    }

    public boolean getScrollableTracksViewportWidth() {
        return true;                    // Width always matches viewport width
    }

    public boolean getScrollableTracksViewportHeight() {
        return (getParent().getHeight() > getPreferredSize().height);
    }
    
    /*
     * Implement ComponentListener to detect window size changes
     */
    public void componentResized(ComponentEvent e) {
        setTransform();     // Resized so recalculate the transform
        repaint();          // and ensure we redraw everything
    }

    public void componentMoved(ComponentEvent e) {
    }

    public void componentShown(ComponentEvent e) {
    }

    public void componentHidden(ComponentEvent e) {
    }    
  
    /*
     * Internal class to hold drawing instructions.
     * draw = true if drawing, false if moving
     * x, y = plotter coordinate to draw/move to
     * dir = direction of last instruction. Can be any integer to indicate the direction, 
     *       segments will be combined if the direction matches.  Set to 0 if direction 
     *       is unknown.            
     */
    private static class Segment {
        boolean draw;
        int x, y, dir;

        Segment(boolean draw, int x, int y, int dir) {
            this.draw = draw;
            this.x = x;  this.y = y;
            this.dir = dir;
        }
        
        // Mainly for debugging
        public String toString() {
            return draw + ": [" + x + "," + y + "] " + dir;
        }
    }
}
