/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2009
 */
package elliott803.view.component;

import java.awt.Font;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import elliott803.machine.Word;

/**
 * Display a 39 bit word in various formats:
 *   BIN39: Display as 39 binary digits
 *   BIN38: Display as least significant 38 binary digits
 *   OCTAL: Display as 13 octal digits
 *   INTEGER: Display as a signed integer
 *   FLOAT: Display as floating point
 *   INSTRUCTION: Display as a pair of instructions
 */
public class DisplayWord extends JPanel {
    private static final long serialVersionUID = 1L;

    static final Font monoFont = Font.decode("monospaced");

    public enum Type {
        BIN39, BIN38, OCTAL, INTEGER, FLOAT, INSTRUCTION, TEXT
    }

    Type type;
    JLabel text;

    public DisplayWord(Type type) {
        this(null, type);
    }

    public DisplayWord(String name, Type type) {
        this.type = type;
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setAlignmentX(RIGHT_ALIGNMENT);
        if (name != null) {
            JLabel title = new JLabel(name +":");
            title.setAlignmentY(CENTER_ALIGNMENT);
            title.setAlignmentX(RIGHT_ALIGNMENT);
            add(title);
            add(Box.createHorizontalStrut(5));
        }
        text = new JLabel();
        text.setAlignmentY(CENTER_ALIGNMENT);
        text.setAlignmentX(LEFT_ALIGNMENT);
        text.setFont(monoFont);
        text.setHorizontalTextPosition(JLabel.LEFT);
        add(text);
        setValue(0);
    }

    public void setValue(long value) {
        String txt = null;
        switch (type) {
            case BIN39:       txt = Word.toBinaryString(value);   break;
            case BIN38:       txt = Word.toBin38String(value);    break;
            case OCTAL:       txt = Word.toOctalString(value);    break;
            case INTEGER:     txt = Word.toIntegerString(value);  break;
            case FLOAT:       txt = Word.toFloatString(value);    break;
            case INSTRUCTION: txt = Word.toInstrString(value);    break;
        }
        text.setText(txt);
    }
}
