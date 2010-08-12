/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2009,2010
 */
package elliott803.view.component;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JPanel;

/**
 * Display 8K words of store as a 128 x 64 matrix of dots.  Toggle the colour
 * of a dot when the content of a storage address is changed.
 *
 * @author Baldwin
 */
public class DisplayCore extends JPanel {
    private static final long serialVersionUID = 1L;

    static final int STORESIZE = 8192;
    static final int ROWSIZE = 64;
    static final int ROWCOUNT = STORESIZE/ROWSIZE;
    static final int BLOCKSIZE = 2;

    int[][] store = new int[ROWCOUNT][ROWSIZE];

    public DisplayCore() {
        setPreferredSize(new Dimension(ROWSIZE*BLOCKSIZE, ROWCOUNT*BLOCKSIZE));
    }

    public void setValue(int addr, long value) {
        int i = addr%ROWSIZE, j = addr/ROWSIZE;
        int b = (value == 0) ? 0 : store[j][i] + 1;
        store[j][i] = b;
        repaint(BLOCKSIZE*i, BLOCKSIZE*j, BLOCKSIZE, BLOCKSIZE);
    }
    
    public void setValues(long[] core) {
        for (int addr = 4; addr < core.length; addr++) {
            int i = addr%ROWSIZE, j = addr/ROWSIZE;
            store[j][i] = (core[addr] == 0) ? 0 : 1;
        }
        repaint();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Use the clipping rectangle to determine the area of output that needs
        // to be repainted.  Default is to paint everything.
        int startX = 0, startY = 0;
        int minI = 0, maxI = ROWSIZE, minJ = 0, maxJ = ROWCOUNT;
        Rectangle clip = g.getClipBounds();
        if (clip != null) {
            startX = clip.x;  startY = clip.y;
            minI = clip.x/BLOCKSIZE;  maxI = minI + clip.width/BLOCKSIZE;
            minJ = clip.y/BLOCKSIZE;  maxJ = minJ + clip.height/BLOCKSIZE;
        }
        
        // Paint the output area
        for (int j = minJ, y = startY; j < maxJ; j++) {
            for (int i = minI, x = startX; i < maxI; i++) {
                int b = store[j][i];
                g.setColor((b == 0) ? Color.WHITE : ((b&1) == 0) ? Color.DARK_GRAY : Color.LIGHT_GRAY);
                g.fillRect(x, y, BLOCKSIZE, BLOCKSIZE);
                x += BLOCKSIZE;
            }
            y += BLOCKSIZE;
        }
    }
}
