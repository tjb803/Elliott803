/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2009
 */
package elliott803.view.component;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

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
        int i = addr/ROWSIZE, j = addr%ROWSIZE;
        int b = (value == 0) ? 0 : store[i][j] + 1;
        store[i][j] = b;
        Graphics g = getGraphics();
        if (g != null) {
            g.setColor((b == 0) ? Color.WHITE : ((b&1) == 0) ? Color.DARK_GRAY : Color.LIGHT_GRAY);
            g.fillRect(BLOCKSIZE*j, BLOCKSIZE*i, BLOCKSIZE, BLOCKSIZE);
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (int i = 0, y = 0; i < ROWCOUNT; i++) {
            for (int j = 0, x = 0; j < ROWSIZE; j++) {
                int b = store[i][j];
                g.setColor((b == 0) ? Color.WHITE : ((b&1) == 0) ? Color.DARK_GRAY : Color.LIGHT_GRAY);
                g.fillRect(x, y, BLOCKSIZE, BLOCKSIZE);
                x += BLOCKSIZE;
            }
            y += BLOCKSIZE;
        }
    }
}
