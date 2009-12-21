/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2009
 */
package elliott803.view;

import java.awt.Container;
import java.awt.Font;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;

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
import elliott803.telecode.TelecodeOutputStream;
import elliott803.telecode.TelecodeToChar;

/**
 * A visual representation of the teletype output
 *
 * @author Baldwin
 */
public class TeletypeView extends TapeDeviceView {
    private static final long serialVersionUID = 1L;

    Punch teletype;

    JTextArea paper;
    JScrollPane scroll;

    public TeletypeView(Punch teletype) {
        super("Teletype");
        this.teletype = teletype;

        paper = new JTextArea(15, 80);
        paper.setFont(Font.decode("Monospaced-bold"));
        paper.setLineWrap(true);
        paper.setEditable(false);

        scroll = new JScrollPane(paper);
        scroll.setAlignmentX(LEFT_ALIGNMENT);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        JPanel actions = new JPanel();
        actions.setLayout(new BoxLayout(actions, BoxLayout.X_AXIS));
        actions.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        actions.setAlignmentX(LEFT_ALIGNMENT);
        JButton clear = new JButton("Clear");
        clear.setActionCommand(DEV_EJECT);
        clear.addActionListener(this);
        JButton save = new JButton("Save...");
        save.addActionListener(this);
        actions.add(clear);
        actions.add(Box.createHorizontalStrut(5));
        actions.add(save);
        actions.add(Box.createHorizontalGlue());
        actions.add(file);

        teletype.setView(this);

        Container content = getContentPane();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.add(scroll);
        content.add(actions);
        pack();
        setVisible(true);
    }

    public void setChar(char ch) {
        paper.append(Character.toString(ch));
        if (ch == '\n')
            lineEnd();
        paper.setCaretPosition(paper.getDocument().getLength());
    }

    public void setText(String text) {
        if (text == null) {
            paper.setText(null);
        } else {
            paper.append(text);
            if (!text.endsWith("\n"))
               lineEnd();
        }
        paper.setCaretPosition(paper.getDocument().getLength());
    }

    /*
     * Action button handling - comes via setTape method
     */
    void setTape(File lfile, String mode, boolean ascii) {
        if (lfile == null) {         // Clear button
            setText(null);          // - clear text and close any output stream
            teletype.setTape(null);
        } else {                    // Otherwise Save, so set new output stream
            try {                   // in append mode.
                OutputStream output = new TelecodeOutputStream(new FileWriter(lfile, true), ascii);
                teletype.setTape(output);
                file.setText("Output log: " + lfile.getName());
            } catch (IOException e) {
                System.err.println(e);
            }
        }
    }

    private void lineEnd() {
        // At the end of a line discard some lines if we have printed more than 100.
        try {
            int lines = paper.getLineCount();
            if (lines > 100)
                paper.replaceRange(null, 0, paper.getLineStartOffset(lines-100));
        } catch (BadLocationException e) {
            System.err.println(e);
        }
    }


    /*
     * GUI Visualisation
     */

    TelecodeToChar converter = new TelecodeToChar();
    byte[] tc = new byte[1];
    char[] cc = new char[1];

    public void updateCh(int ch) {
        if (ch != Telecode.TELE_CR) {
            tc[0] = (byte)ch;
            if (converter.convert(tc, 1, cc) != 0) {
                setChar(cc[0]);
            }
        }
    }
}