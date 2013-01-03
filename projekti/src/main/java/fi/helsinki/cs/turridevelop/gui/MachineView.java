
package fi.helsinki.cs.turridevelop.gui;

import fi.helsinki.cs.turridevelop.logic.Machine;
import fi.helsinki.cs.turridevelop.logic.State;
import fi.helsinki.cs.turridevelop.util.Vec2;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
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
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        
        graphics.translate(
            getWidth() / 2 - (int)centerpos.x,
            getHeight() / 2 - (int)centerpos.y
        );
        
        Graphics2D g = (Graphics2D) graphics;
        
        for(String name : machine.getStateNames()) {
            State state = machine.getState(name);
            Vec2 pos = state.getPosition();
            drawCenteredText(g, name, pos);
            g.draw(getStateEllipse(g, state));
        }
    }
    
    /**
     * Get the ellipse that should be drawn around the state name.
     * 
     * @param g The graphics context used.
     * @param state The state in the machine whose name will be inside the
     * ellipse.
     * @return The ellipse that fits the name centered inside it.
     */
    private static Ellipse2D.Double getStateEllipse(Graphics2D g, State state) {
        Vec2 textsize = getTextSize(g, state.getName());
        Vec2 pos = Vec2.sub(state.getPosition(), textsize);
        Vec2 size = textsize.mul(2.0);
        
        return new Ellipse2D.Double(pos.x, pos.y, size.x, size.y);
    }
    
    /**
     * Get the size vector of text.
     * 
     * @param g The graphics context used for drawing the text.
     * @param text The text to draw.
     * @return The vector consisting of the width and the height of the text.
     */
    private static Vec2 getTextSize(Graphics2D g, String text) {
        FontMetrics metrics = g.getFontMetrics();
        return new Vec2(
            metrics.stringWidth(text),
            metrics.getAscent() + metrics.getDescent()
        );
    }
    
    /**
     * Draw text centered around given position.
     * 
     * @param g The graphics context.
     * @param text The text to draw.
     * @param pos The position to center the text to.
     */
    private static void drawCenteredText(Graphics2D g, String text, Vec2 pos) {
        FontMetrics metrics = g.getFontMetrics();
        double x = pos.x - 0.5 * metrics.stringWidth(text); 
        double y = pos.y + 0.5 * (metrics.getAscent() - metrics.getDescent());
        g.drawString(text, (int)x, (int)y);
    }
}
