/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2009
 */
package elliott803.view.component;

import elliott803.machine.Instruction;

/**
 * Display an address value.
 *
 * @author Baldwin
 */
public class DisplayAddress extends DisplayWord {
    private static final long serialVersionUID = 1L;

    public DisplayAddress() {
        this(null);
    }

    public DisplayAddress(String name) {
        super(name, Type.TEXT);
        setValue(0);
    }

    public void setValue(int value) {
        String txt = Instruction.toAddrString(value) + ":";
        text.setText(txt);
    }
}
