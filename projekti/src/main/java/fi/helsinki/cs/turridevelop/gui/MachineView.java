
package fi.helsinki.cs.turridevelop.gui;

import fi.helsinki.cs.turridevelop.logic.Machine;
import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * A panel showing the diagram representation of a machine and allows editing
 * it.
 */
public class MachineView extends JPanel {
    /**
     * Machine being edited.
     */
    private Machine machine;
    
    public MachineView(Machine machine) {
        this.machine = machine;
        setBackground(Color.white);
        add(new JLabel("Machine view for: " + machine.getName()));
    }
}
