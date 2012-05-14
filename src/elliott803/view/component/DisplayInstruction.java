/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2012
 */
package elliott803.view.component;

import elliott803.machine.Instruction;

/**
 * Display a single instruction.
 *
 * @author Baldwin
 */
public class DisplayInstruction extends DisplayWord {
    private static final long serialVersionUID = 1L;

    public DisplayInstruction() {
        this(null);
    }

    public DisplayInstruction(String name) {
        super(name, Type.TEXT);
        setValue(0);
    }

    public void setValue(int value) {
        String txt = Instruction.toInstrString(value);
        text.setText(txt);
    }
}
