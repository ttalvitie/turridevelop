
package fi.helsinki.cs.turridevelop.gui;

import fi.helsinki.cs.turridevelop.exceptions.NameInUseException;
import fi.helsinki.cs.turridevelop.logic.Machine;
import fi.helsinki.cs.turridevelop.logic.State;
import fi.helsinki.cs.turridevelop.util.Vec2;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * A panel showing the diagram representation of a machine and allows editing
 * it.
 */
public class MachineView
extends JPanel implements MouseListener, MouseMotionListener {
    /**
     * Machine being edited.
     */
    private Machine machine;
    
    /**
     * Font used in the panel.
     */
    private Font font;
    
    /**
     * The position of the machine that is shown in the center.
     */
    private Vec2 centerpos;
    
    /**
     * The currently active state.
     */
    private State active_state;
    
    /**
     * The mouse button used on current drag, MouseEvent.NOBUTTON if there is
     * no drag.
     */
    private int drag_button;
    
    /**
     * If drag_button != MouseEvent.NOBUTTON, the original value of the dragged
     * vector if applicable.
     */
    private Vec2 drag_original;
    
    /**
     * If drag_button != MouseEvent.NOBUTTON, the position from which the
     * dragging started.
     */
    private Vec2 drag_start;
    
    /**
     * Constructs a MachineView panel.
     * 
     * @param machine The machine to be edited in the view.
     */
    public MachineView(Machine machine) {
        this.machine = machine;
        centerpos = new Vec2();
        
        setBackground(Color.white);
        addMouseListener(this);
        addMouseMotionListener(this);
        
        font = new Font("SansSerif", Font.PLAIN, 20);
        drag_button = MouseEvent.NOBUTTON;
    }
    
    /**
     * Get the machine being edited.
     * 
     * @return The machine being edited.
     */
    public Machine getMachine() {
        return machine;
    }
    
    /**
     * Add new state to the machine.
     */
    public void addState() {
        int statenumber = 1;
        while(machine.getState("State #" + statenumber) != null) {
            statenumber++;
        }
        
        try {
            State state = machine.addState("State #" + statenumber);
            state.setPosition(centerpos);
        } catch(NameInUseException e) {
            throw new RuntimeException();
        }
        
        repaint();
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        Vec2 pos = getMousePosition(e);
        Vec2 abspos = new Vec2(e.getX(), e.getY());
        
        if(e.getButton() == MouseEvent.BUTTON1) {
            // If we hit a state, set it active.
            boolean found = false;
            for(String statename : machine.getStateNames()) {
                State state = machine.getState(statename);
                if(getStateEllipse(state).contains(pos.x, pos.y)) {
                    active_state = state;
                    found = true;
                }
            }

            // Otherwise, deactivate state.
            if(!found) {
                active_state = null;
            }
        }
        
        repaint();
        
        // Start the drag if there is none already.
        if(drag_button == MouseEvent.NOBUTTON) {
            drag_button = e.getButton();
            
            // State drag
            if(drag_button == MouseEvent.BUTTON1 && active_state != null) {
                drag_original = active_state.getPosition();
                drag_start = pos;
            }
            
            // View drag
            if(drag_button == MouseEvent.BUTTON3) {
                drag_original = centerpos;
                drag_start = abspos;
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        drag_button = MouseEvent.NOBUTTON;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
    
    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        Vec2 pos = getMousePosition(e);
        Vec2 abspos = new Vec2(e.getX(), e.getY());
        
        // Update dragged object if there is an active drag.
        if(drag_button == MouseEvent.BUTTON1 && active_state != null) {
            Vec2 newpos = Vec2.sub(Vec2.add(drag_original, pos), drag_start);
            active_state.setPosition(newpos);
            repaint();
        }
        if(drag_button == MouseEvent.BUTTON3) {
            centerpos = Vec2.add(Vec2.sub(drag_original, abspos), drag_start);
            repaint();
        }
    }
    
    @Override
    public void paintComponent(Graphics outg) {
        super.paintComponent(outg);
        
        Graphics2D g = (Graphics2D) outg.create();
        g.setFont(font);
        
        g.translate(
            getWidth() / 2 - (int)centerpos.x,
            getHeight() / 2 - (int)centerpos.y
        );
        
        for(String name : machine.getStateNames()) {
            State state = machine.getState(name);
            drawState(g, state);
        }
    }
    
    /**
     * Get the position of the mouse event in the coordinates of the diagram.
     * 
     * @param e The mouse event.
     * @return The position of the pointer in diagram coordinates.
     */
    private Vec2 getMousePosition(MouseEvent e) {
        Vec2 xy = new Vec2(e.getX(), e.getY());
        Vec2 halfsize = new Vec2(getWidth(), getHeight()).mul(0.5);
        return Vec2.add(Vec2.sub(xy, halfsize), centerpos);
    }
    
    /**
     * Gets the ellipse that should be drawn around the state name.
     * 
     * @param state The state in the machine whose name will be inside the
     * ellipse.
     * @return The ellipse that fits the name centered inside it.
     */
    private Ellipse2D.Double getStateEllipse(State state) {
        Vec2 size = getTextSize(state.getName()).mul(1.7);
        if(size.x < size.y) {
            size = new Vec2(size.y, size.y);
        }
        Vec2 pos = Vec2.sub(state.getPosition(), size.mul(0.5));
        
        return new Ellipse2D.Double(pos.x, pos.y, size.x, size.y);
    }
    
    /**
     * Gets the size vector of text.
     * 
     * @param text The text to draw.
     * @return The vector consisting of the width and the height of the text.
     */
    private Vec2 getTextSize(String text) {
        FontMetrics metrics = getFontMetrics(font);
        return new Vec2(
            metrics.stringWidth(text),
            metrics.getAscent() + metrics.getDescent()
        );
    }
    
    private void drawState(Graphics2D g, State state) {
        Vec2 pos = state.getPosition();
        Ellipse2D.Double ellipse = getStateEllipse(state);
        
        // Hilight active state.
        Graphics2D g2 = (Graphics2D) g.create();
        if(state == active_state) {
            g2.setColor(Color.YELLOW);
        } else {
            g2.setColor(Color.WHITE);
        }
        g2.fill(ellipse);
        
        g.draw(ellipse);
        drawCenteredText(g, state.getName(), pos);
    }
    
    /**
     * Draws text centered around given position.
     * 
     * @param g The graphics context to use.
     * @param text The text to draw.
     * @param pos The position to center the text to.
     */
    private void drawCenteredText(Graphics2D g, String text, Vec2 pos) {
        FontMetrics metrics = getFontMetrics(font);
        double x = pos.x - 0.5 * metrics.stringWidth(text); 
        double y = pos.y + 0.5 * (metrics.getAscent() - metrics.getDescent());
        g.drawString(text, (int)x, (int)y);
    }
}
