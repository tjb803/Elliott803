/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2009
 */
package elliott803.view.component;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JRadioButton;

/**
 * Dialog option to choose reader/punch mode
 *
 * @author Baldwin
 */
public class DeviceModeSelect extends DeviceMode implements ActionListener {
    private static final long serialVersionUID = 1L;

    String mode;
    JCheckBox ascii;

    public DeviceModeSelect(boolean hasAuto, boolean hasElliott) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createTitledBorder("Tape format"));
        JRadioButton ab = new JRadioButton(MODE_AUTO);
        JRadioButton eb = new JRadioButton(MODE_ELLIOTT);
        JRadioButton sb = new JRadioButton(MODE_SYSTEM);
        ascii = new JCheckBox(MODE_ASCII, false);
        ascii.setEnabled(false);
        ButtonGroup group = new ButtonGroup();
        group.add(ab);  group.add(eb);  group.add(sb);
        ab.addActionListener(this);
        eb.addActionListener(this);
        sb.addActionListener(this);
        ascii.addActionListener(this);
        if (hasAuto) {
            add(ab); add(eb); add(sb);
            ab.doClick();
        } else if (hasElliott) {
            add(eb); add(sb); add(ascii);
            eb.doClick();
        } else {
            add(sb); add(ascii);
            sb.doClick();
        }
    }

    public String getMode() {
        return mode;
    }

    public boolean getAscii() {
        return ascii.isSelected();
    }

    // Set mode to button selected and enable/disable the ASCII check box
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand() != MODE_ASCII) {
            mode = e.getActionCommand();
            ascii.setEnabled(mode.equals(MODE_SYSTEM));
        }
    }
}
