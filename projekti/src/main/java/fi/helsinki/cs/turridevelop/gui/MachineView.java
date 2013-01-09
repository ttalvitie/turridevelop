package fi.helsinki.cs.turridevelop.gui;

import fi.helsinki.cs.turridevelop.exceptions.NameInUseException;
import fi.helsinki.cs.turridevelop.logic.Machine;
import fi.helsinki.cs.turridevelop.logic.Project;
import fi.helsinki.cs.turridevelop.logic.State;
import fi.helsinki.cs.turridevelop.logic.Transition;
import fi.helsinki.cs.turridevelop.util.Vec2;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * A panel showing the diagram representation of a machine and allows editing
 * it.
 */
public class MachineView
extends JPanel implements MouseListener, MouseMotionListener {
    /**
     * The project of the machine being edited.
     */
    private Project project;
    
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
     * @param project Project of the machine.
     * @param machine The machine to be edited in the view.
     * @param editpanel The panel to put the editing panel into.
     * @param frame The frame containing the view.
     */
    public MachineView(
        Project project, Machine machine, JPanel editpanel, JFrame frame
    ) {
        this.project = project;
        this.machine = machine;
        centerpos = new Vec2();
        this.editpanel = editpanel;
        this.frame = frame;
        
        setBackground(Color.white);
        
        font = new Font("SansSerif", Font.PLAIN, 14);
        drag_button = MouseEvent.NOBUTTON;
        
        setActiveState((State) null);
        
        addMouseListener(this);
        addMouseMotionListener(this);
    }
    
    /**
     * Set given state as active if possible.
     * 
     * @param statename The name of the state.
     */
    public void setActiveState(String statename) {
        State state = machine.getState(statename);
        if(state != null) {
            setActiveState(state);
        }
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
            setActiveState((State) null);
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
        Vec2 abspos = new Vec2(e.getX(), e.getY());
        Vec2 pos = transformComponentPositionToDiagram(abspos);
        
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
                return;
            }
        }
        
        // Start the drag if there is none already.
        if(drag_button == MouseEvent.NOBUTTON) {
            drag_button = e.getButton();
            
            // State drag
            if(drag_button == MouseEvent.BUTTON1 && active_state != null) {
                drag_original = active_state.getPosition();
                drag_start = abspos;
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
        Vec2 abspos = new Vec2(e.getX(), e.getY());
        Vec2 pos = transformComponentPositionToDiagram(abspos);
        
        // Update dragged object if there is an active drag.
        if(drag_button == MouseEvent.BUTTON1 && active_state != null) {
            Vec2 newpos = Vec2.sub(Vec2.add(drag_original, abspos), drag_start);
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
        g.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON
        );
        g.setStroke(
            new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)
        );
        g.setColor(new Color(0.3f, 0.3f, 0.3f));
        
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
        
        /**
         * Draw the arrows showing the states outside the view.
         */
        for(String name : machine.getStateNames()) {
            State state = machine.getState(name);
            drawStateArrow(g, state);
        }
        
        // Draw the choice text if we are in choice mode.
        if(choice_handler != null) {
            Graphics2D g2 = (Graphics2D) outg.create();
            g2.setColor(Color.RED);
            g2.drawString(choice_text, 5, 5 + getFontMetrics(font).getAscent());
        }
    }
    
    /**
     * Transform a position in the component coordinates to the diagram
     * coordinates.
     * 
     * @param pos Position in component coordinates.
     * @return The position in diagram coordinates.
     */
    private Vec2 transformComponentPositionToDiagram(Vec2 pos) {
        Vec2 halfsize = new Vec2(getWidth(), getHeight()).mul(0.5);
        return Vec2.add(Vec2.sub(pos, halfsize), centerpos);
    }
    
    /**
     * Transform a position in the diagram coordinates to the component
     * coordinates.
     * 
     * @param pos Position in diagram coordinates.
     * @return The position in component coordinates.
     */
    private Vec2 transformDiagramPositionToComponent(Vec2 pos) {
        Vec2 halfsize = new Vec2(getWidth(), getHeight()).mul(0.5);
        return Vec2.add(Vec2.sub(pos, centerpos), halfsize);
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
    
    /**
     * Draw a state.
     * 
     * @param g The graphics context to use.
     * @param state The state to draw.
     */
    private void drawState(Graphics2D g, State state) {
        Vec2 pos = state.getPosition();
        Ellipse2D.Double ellipse = getStateEllipse(state);
        
        // Draw submachine.
        String submachine = state.getSubmachine();
        if(submachine != null) {
            Vec2 center = new Vec2(
                ellipse.getCenterX(), ellipse.getMaxY() + 30
            );
            g.drawLine((int)pos.x, (int)pos.y, (int)center.x, (int)center.y);
            Vec2 size = Vec2.add(getTextSize(submachine), new Vec2(4.0, 3.0));
            Vec2 min = Vec2.sub(center, size.mul(0.5));
            
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(Color.WHITE);
            g2.fillRect((int)min.x, (int)min.y, (int)size.x, (int)size.y);
            
            g.drawRect((int)min.x, (int)min.y, (int)size.x, (int)size.y);
            
            // If the submachine is missing, show in red.
            if(project.getMachine(submachine) == null) {
                g2.setColor(Color.RED);
            } else {
                g2.setColor(Color.BLACK);
            }
            drawCenteredText(g2, submachine, center);
        }
        
        // Hilight active state.
        Graphics2D g2 = (Graphics2D) g.create();
        if(state == active_state) {
            g2.setColor(Color.YELLOW);
        } else {
            g2.setColor(Color.WHITE);
        }
        g2.fill(ellipse);
        
        // Draw smaller ellipses to mark accepting states.
        if(state.isAccepting()) {
            Ellipse2D.Double ellipse2 = new Ellipse2D.Double(
                ellipse.getMinX() + 3,
                ellipse.getMinY() + 3,
                ellipse.getWidth() - 6,
                ellipse.getHeight() - 6
            );
            g.draw(ellipse2);
        }
       
        g.draw(ellipse);
        g2.setColor(Color.BLACK);
        drawCenteredText(g2, state.getName(), pos);
    }
    
    /**
     * If the state is outside visible area, draw an arrow pointing towards it.
     * 
     * @param g The graphics context to use.
     * @param state The state to draw.
     */
    private void drawStateArrow(Graphics2D g, State state) {
        Vec2 pos = state.getPosition();
        Vec2 diagpos = transformDiagramPositionToComponent(pos);
        
        if(
            diagpos.x < 0 || diagpos.x >= getWidth() ||
            diagpos.y < 0 || diagpos.y >= getHeight()
        ) {
            Vec2 min = transformComponentPositionToDiagram(new Vec2());
            Vec2 max = transformComponentPositionToDiagram(
                new Vec2(getWidth(), getHeight())
            );
            
            Vec2 arrowpos = new Vec2(
                Math.min(Math.max(pos.x, min.x), max.x),
                Math.min(Math.max(pos.y, min.y), max.y)
            );
            Vec2 arrowdir = new Vec2(1, 0);
            if(diagpos.x < 0) {
                if(diagpos.y < 0) {
                    arrowdir = new Vec2(-1.0, -1.0);
                } else if(diagpos.y >= getHeight()) {
                    arrowdir = new Vec2(-1.0, 1.0);
                } else {
                    arrowdir = new Vec2(-1.0, 0.0);
                }
            } else if(diagpos.x >= getWidth()) {
                if(diagpos.y < 0) {
                    arrowdir = new Vec2(1.0, -1.0);
                } else if(diagpos.y >= getHeight()) {
                    arrowdir = new Vec2(1.0, 1.0);
                } else {
                    arrowdir = new Vec2(1.0, 0.0);
                }
            } else {
                if(diagpos.y < 0) {
                    arrowdir = new Vec2(0.0, -1.0);
                } else {
                    arrowdir = new Vec2(0.0, 1.0);
                }
            }
            
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(Color.RED);
            g2.fill(Util.getArrowEnd(arrowpos, arrowdir, 12.0));
        }
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
        Vec2[] points = Util.getBezierBetweenEllipses(
            getStateEllipse(from),
            getStateEllipse(to)
        );
        
        Path2D.Double bezier = new Path2D.Double();
        bezier.moveTo(points[0].x, points[0].y);
        bezier.curveTo(
            points[1].x, points[1].y,
            points[2].x, points[2].y,
            points[3].x, points[3].y
        );
        g.draw(bezier);
        
        // Show the endpoint with an arrow.
        g.fill(
            Util.getArrowEnd(points[3], Vec2.sub(points[3], points[2]), 8.0)
        );
        
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
        Vec2 midpoint = Vec2.add(
            Vec2.add(points[0], points[3]).mul(0.125),
            Vec2.add(points[1], points[2]).mul(0.375)
        );
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(Color.BLACK);
        g2.fillOval((int)midpoint.x - 2, (int)midpoint.y - 2, 4, 4);
        FontMetrics metrics = getFontMetrics(font);
        double textx = midpoint.x + 7.0;
        double textymid = 0.5 * (metrics.getAscent() - metrics.getDescent());
        double texty = midpoint.y + textymid;
        g2.drawString(text.toString(), (float)textx, (float)texty);
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
