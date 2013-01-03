
package fi.helsinki.cs.turridevelop.gui;

import fi.helsinki.cs.turridevelop.logic.Machine;
import fi.helsinki.cs.turridevelop.logic.State;
import fi.helsinki.cs.turridevelop.util.Vec2;
import java.awt.Color;
import java.awt.Graphics;
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
    
    /**
     * The position of the machine that is shown in the center.
     */
    private Vec2 centerpos;
    
    public MachineView(Machine machine) {
        this.machine = machine;
        centerpos = new Vec2();
        
        setBackground(Color.white);
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        g.translate(
            getWidth() / 2 - (int)centerpos.x,
            getHeight() / 2 - (int)centerpos.y
        );
        
        for(String name : machine.getStateNames()) {
            State state = machine.getState(name);
            Vec2 pos = state.getPosition();
            g.drawString(name, (int)pos.x, (int)pos.y);
        }
    }
}
