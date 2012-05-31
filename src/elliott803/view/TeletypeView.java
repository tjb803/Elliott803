/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2009,2010
 */
package elliott803.view;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;

import elliott803.hardware.Punch;
import elliott803.telecode.Telecode;
import elliott803.telecode.TelecodeInputStream;
import elliott803.telecode.TelecodeOutputStream;
import elliott803.telecode.TelecodeToChar;

/**
 * A visual representation of the teletype output
 *
 * @author Baldwin
 */
public class TeletypeView extends TapeDeviceView implements ActionListener {
    private static final long serialVersionUID = 1L;

    static final int TT_COLUMNS = 80;
    static final String TT_CURSOR = "\u220e";
    static final String TT_CLEAR = "Clear";
    static final String TT_SCROLL = "Scroll";
    static final String TT_SAVE = "Save...";
    
    Punch teletype;

    JTextArea paper;

    public TeletypeView(Punch teletype) {
        super("Teletype");
        this.teletype = teletype;

        paper = new JTextArea(TT_CURSOR, 15, TT_COLUMNS);
        paper.setFont(Font.decode("Monospaced-bold"));
        paper.setLineWrap(false);
        paper.setEditable(false);

        JScrollPane scroll = new JScrollPane(paper);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        JPanel actions = new JPanel();
        actions.setLayout(new BoxLayout(actions, BoxLayout.X_AXIS));
        actions.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        actions.setAlignmentX(LEFT_ALIGNMENT);
        JButton clear = new JButton(TT_CLEAR);
        clear.setActionCommand(DEV_EJECT);
        clear.addActionListener(this);
        JButton scb = new JButton(TT_SCROLL);
        scb.addActionListener(this);
        open = new JButton(TT_SAVE);
        open.setActionCommand(DEV_OPEN);
        open.addActionListener(this);
        actions.add(scb);
        actions.add(Box.createHorizontalStrut(5));
        actions.add(clear);
        actions.add(Box.createHorizontalStrut(5));
        actions.add(open);
        actions.add(Box.createHorizontalGlue());
        actions.add(file);

        teletype.setView(this);

        Container content = getContentPane();
        content.add(scroll, BorderLayout.CENTER);
        content.add(actions, BorderLayout.SOUTH);
        pack();
        setVisible(true);
    }

    public void setChar(char ch) {
        // Force a new line if we hit the 80-column limit
        int pos = paper.getDocument().getLength()-1;
        try {
            int line = paper.getLineCount() - 1;
            if (line >= 0 && ch != '\n') {
                if (paper.getLineEndOffset(line) - paper.getLineStartOffset(line) >= TT_COLUMNS) 
                    paper.insert("\n", pos++);
            }    
        } catch (BadLocationException e) {  // Should not happen!
            System.err.println(e);
        }
        
        paper.insert(Character.toString(ch), pos);
        paper.setCaretPosition(pos);
    }

    public void clearText() {
        paper.setText(TT_CURSOR);
        paper.setCaretPosition(0);
    }

    /*
     * Action button handling - comes via setTape method
     */
    void setTape(File lfile, String mode, boolean ascii) {
        if (lfile == null) {        // Clear button
            clearText();            // - clear text and close any output stream
            teletype.setTape(null);
        } else {                    // Otherwise Save, so set new output stream
            try {                   // in append mode.
                // Open output file and write anything we have so far.  This is written
                // from the JTextArea, so it is a Java String. 
                TelecodeOutputStream output = new TelecodeOutputStream(new FileWriter(lfile, true), ascii);
                TelecodeInputStream input = new TelecodeInputStream(new StringReader(paper.getText()));
                output.write(input);

                // Set the teletype output tape for any subsequent output.
                teletype.setTape(output);
                file.setText("Output log: " + lfile.getName());
            } catch (IOException e) {
                System.err.println(e);
            }
        }
    }
    
    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);   // Must super to TapeDeviceView 
        if (e.getActionCommand().equals(TT_SCROLL)) {
            setChar('\n');
        }
    }

    /*
     * GUI Visualisation
     */

    TelecodeToChar converter = new TelecodeToChar();
    byte[] tc = new byte[1];
    char[] cc = new char[1];

    public void updateCh(int ch) {
        if (ch != Telecode.TELE_BL && ch != Telecode.TELE_CR) {
            tc[0] = (byte)ch;
            if (converter.convert(tc, 1, cc) != 0) {
                setChar(cc[0]);
            }
        }
    }
}
