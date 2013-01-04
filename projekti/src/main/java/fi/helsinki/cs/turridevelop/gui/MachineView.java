
package fi.helsinki.cs.turridevelop.gui;

import fi.helsinki.cs.turridevelop.exceptions.NameInUseException;
import fi.helsinki.cs.turridevelop.logic.Machine;
import fi.helsinki.cs.turridevelop.logic.State;
import fi.helsinki.cs.turridevelop.logic.Transition;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import javax.swing.JButton;
import javax.swing.JFrame;
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
     * The frame containing the view.
     */
    private JFrame frame;
    
    /**
     * Font used in the panel.
     */
    private Font font;
    
    /**
     * The position of the machine that is shown in the center.
     */
    private Vec2 centerpos;
    
    /**
     * The currently active state. Change only through setActiveState.
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
     * The panel to put the state editor into.
     */
    private JPanel editpanel;
    
    /**
     * If the view is in state choice mode, the handler for notifying of the
     * choice. Otherwise null.
     */
    private StateChoiceHandler choice_handler;
    
    /**
     * If choice_handler != null, the text used to describe the choice.
     */
    private String choice_text;
    
    /**
     * Constructs a MachineView panel.
     * 
     * @param machine The machine to be edited in the view.
     * @param editpanel The panel to put the editing panel into.
     * @param frame The frame containing the view.
     */
    public MachineView(Machine machine, JPanel editpanel, JFrame frame) {
        this.machine = machine;
        centerpos = new Vec2();
        this.editpanel = editpanel;
        this.frame = frame;
        
        setBackground(Color.white);
        
        font = new Font("SansSerif", Font.PLAIN, 14);
        drag_button = MouseEvent.NOBUTTON;
        
        setActiveState(null);
        
        addMouseListener(this);
        addMouseMotionListener(this);
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
     * Notifies the view that a state of the machine has been modified.
     */
    public void stateModified() {
        // If changes were made in states, choice mode is probably invalid.
        choice_handler = null;
        
        // Check if the active state has been removed.
        if(
            active_state != null &&
            machine.getState(active_state.getName()) != active_state
        ) {
            setActiveState(null);
        }
        
        repaint();
    }
    
    /**
     * Puts the view into mode for choosing a state.
     * 
     * @param handler Handler that is notified if a choice is made or choosing
     * is abandoned.
     * @param text Text used to describe the choice for the user.
     */
    public void startStateChoice(StateChoiceHandler handler, String text) {
        choice_handler = handler;
        choice_text = text;
        
        repaint();
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
            // If we hit a state, set it active or choose it depending on
            // whether we are in choice mode or not.
            State clicked_state = null;
            for(String statename : machine.getStateNames()) {
                State state = machine.getState(statename);
                if(getStateEllipse(state).contains(pos.x, pos.y)) {
                    clicked_state = state;
                }
            }
            
            if(choice_handler == null) {
                // No choice mode, set as active.
                setActiveState(clicked_state);
            } else {
                // Choice mode, notify and end choice mode.
                StateChoiceHandler handler = choice_handler;
                choice_handler = null;
                repaint();
                handler.stateChosen(clicked_state);
            }
        }
        
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
        outg.setFont(font);
        
        Graphics2D g = (Graphics2D) outg.create();
        
        g.translate(
            getWidth() / 2 - (int)centerpos.x,
            getHeight() / 2 - (int)centerpos.y
        );
        
        // Draw the states.
        for(String name : machine.getStateNames()) {
            State state = machine.getState(name);
            drawState(g, state);
        }
        
        // For each state, draw the transitions grouped by destination.
        for(String name : machine.getStateNames()) {
            State state = machine.getState(name);
            HashMap<State, ArrayList<Transition>> transitions =
                new HashMap<State, ArrayList<Transition>>();
            
            for(Transition transition : state.getTransitions()) {
                State destination = transition.getDestination();
                if(!transitions.containsKey(destination)) {
                    transitions.put(destination, new ArrayList<Transition>());
                }
                transitions.get(destination).add(transition);
            }
            
            for(State dest : transitions.keySet()) {
                drawTransitions(g, state, dest, transitions.get(dest));
            }
        }
        
        // Draw the choice text if we are in choice mode.
        if(choice_handler != null) {
            Graphics2D g2 = (Graphics2D) outg.create();
            g2.setColor(Color.RED);
            g2.drawString(choice_text, 5, 5 + getFontMetrics(font).getAscent());
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
    
    /**
     * Gets the starting and ending points of line between ellipses.
     * 
     * @param ellipse1 The starting ellipse.
     * @param ellipse2 The ending ellipse.
     * @return 2-element array of the starting and ending points of the line.
     */
    private Vec2[] getLineBetweenEllipses(
        Ellipse2D ellipse1,
        Ellipse2D ellipse2
    ) {
        Vec2 center1 = new Vec2(ellipse1.getCenterX(), ellipse1.getCenterY());
        Vec2 center2 = new Vec2(ellipse2.getCenterX(), ellipse2.getCenterY());
        
        Vec2 diff = Vec2.sub(center2, center1).normalized();
        
        // Get the sizes of the ellipses.
        double w1 = ellipse1.getWidth();
        double h1 = ellipse1.getHeight();
        double w2 = ellipse2.getWidth();
        double h2 = ellipse2.getHeight();
        
        // Write the ellipses in form a(x-x0)^2 + b(y-y0)^2 = 1 where (x0, y0)
        // is the center.
        double a1 = 4.0 / (w1 * w1);
        double b1 = 4.0 / (h1 * h1);
        double a2 = 4.0 / (w2 * w2);
        double b2 = 4.0 / (h2 * h2);
        
        // Solve t from equation a(dx*t)^2 + b(dy*t)^2 = 1 where dx, dy are
        // diff's components, i.e. get the intersection of an ellipse and a line
        // going through its center. For start choose the positive solution and
        // for end the negative solution because diff runs from center1 to
        // center2.
        double dx2 = diff.x * diff.x;
        double dy2 = diff.y * diff.y;
        double t1 = 1.0 / Math.sqrt(a1 * dx2 + b1 * dy2);
        double t2 = -1.0 / Math.sqrt(a2 * dx2 + b2 * dy2);
        
        // Now the endpoints are the intersection points center + t * diff.
        Vec2[] ret = new Vec2[2];
        ret[0] = Vec2.add(center1, diff.mul(t1));
        ret[1] = Vec2.add(center2, diff.mul(t2));
        return ret;
    }
    
    /**
     * Draws transitions between two states.
     * 
     * @param g The graphics context.
     * @param from The source state of all transitions.
     * @param to The destination state of all transitions.
     * @param transitions List of the transitions.
     */
    private void drawTransitions(
        Graphics2D g,
        State from,
        State to,
        ArrayList<Transition> transitions
    ) {
        Vec2[] line = getLineBetweenEllipses(
            getStateEllipse(from),
            getStateEllipse(to)
        );
        
        g.drawLine(
            (int)line[0].x,
            (int)line[0].y,
            (int)line[1].x,
            (int)line[1].y
        );
        
        // Signify endpoint with a disc. TODO: create an arrow.
        g.fillOval((int)line[1].x - 4, (int)line[1].y - 4, 8, 8);
        
        // Create the text for the transitions.
        ArrayList<String> parts = new ArrayList<String>();
        for(Transition transition : transitions) {
            parts.add(Util.getTransitionText(transition, false));
        }
        Collections.sort(parts);
        StringBuilder text = new StringBuilder();
        boolean first = true;
        for(String part : parts) {
            if(!first) {
                text.append("; ");
            }
            text.append(part);
            first = false;
        }
        
        // Render the text.
        Vec2 midpoint = Vec2.add(line[0], line[1]).mul(0.5);
        g.fillOval((int)midpoint.x - 2, (int)midpoint.y - 2, 4, 4);
        FontMetrics metrics = getFontMetrics(font);
        double textx = midpoint.x + 4.0;
        double textymid = 0.5 * (metrics.getAscent() - metrics.getDescent());
        double texty = midpoint.y + textymid;
        g.drawString(text.toString(), (float)textx, (float)texty);
    }
    
    /**
     * Set the active state of the view.
     * 
     * @param state The new active state or null if there should be no active
     * state.
     */
    private void setActiveState(State state) {
        active_state = state;
        
        // Update the editing panel.
        editpanel.removeAll();
        if(state != null) {
            editpanel.add(new StateEditor(state, this, frame));
        }
        
        editpanel.revalidate();
        editpanel.repaint();
        
        repaint();
    }
}
