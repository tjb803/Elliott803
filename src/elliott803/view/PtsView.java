/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2009,2010
 */
package elliott803.view;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;

import elliott803.machine.PaperTapeStation;

/**
 * The visual representation of the paper tape station.
 *
 * This doesn't do much, but does allow the readers and punches to be 
 * swapped.
 *
 * @author Baldwin
 */
public class PtsView  extends JInternalFrame implements ActionListener {
    private static final long serialVersionUID = 1L;
    
    public static final String SWAP_READER = "Exchange Readers";
    public static final String SWAP_PUNCH = "Exchange Punches";
    
    PaperTapeStation pts;
    ReaderView[] reader;
    PunchView[] punch;
    TeletypeView teletype;
    
    JCheckBox swapReader, swapPunch;

    public PtsView(PaperTapeStation pts) {
        super("PaperTape Station", false, false, false, true);
        this.pts = pts;
        
        // Create the views of the tape readers 
        reader = new ReaderView[2];
        reader[0] = new ReaderView(pts.readers[PaperTapeStation.READER1], 1);
        reader[1] = new ReaderView(pts.readers[PaperTapeStation.READER2], 2);

        // Create the views of the punches and the teletype
        punch = new PunchView[2];
        punch[0] = new PunchView(pts.punches[PaperTapeStation.PUNCH1], 1);
        punch[1] = new PunchView(pts.punches[PaperTapeStation.PUNCH2], 2);
        teletype = new TeletypeView(pts.punches[PaperTapeStation.TELETYPE]);
        
        swapReader = new JCheckBox(SWAP_READER);
        swapReader.addActionListener(this);
        swapPunch = new JCheckBox(SWAP_PUNCH);
        swapPunch.setHorizontalTextPosition(JCheckBox.LEADING);
        swapPunch.addActionListener(this);
        
        JPanel p1 = new JPanel();
        p1.setLayout(new BoxLayout(p1, BoxLayout.X_AXIS));
        p1.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        p1.add(swapReader);
        p1.add(Box.createHorizontalGlue());
        p1.add(swapPunch);

        Container content = getContentPane();
        content.add(p1, BorderLayout.CENTER);
        pack();
        setVisible(true);
    }
    
    /*
     * Button actions
     */

    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals(SWAP_READER)) {
            pts.setReaders(swapReader.isSelected());
        } else if (e.getActionCommand().equals(SWAP_PUNCH)) {
            pts.setPunches(swapPunch.isSelected());
        }
    }
}
