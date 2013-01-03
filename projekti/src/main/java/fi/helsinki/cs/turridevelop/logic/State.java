package fi.helsinki.cs.turridevelop.logic;

import fi.helsinki.cs.turridevelop.exceptions.NameInUseException;
import fi.helsinki.cs.turridevelop.util.ByNameContainer;
import fi.helsinki.cs.turridevelop.util.ByNameStored;
import fi.helsinki.cs.turridevelop.util.Vec2;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * State of a Turing machine.
 */
public class State implements ByNameStored {
    /**
     * Name of the state.
     */
    private String name;
    
    /**
     * The container containing the state.
     */
    private ByNameContainer<State> container;
    
    /**
     * Transitions from this state.
     */
    private HashSet<Transition> transitions;
    
    /**
     * Hashmap from input characters to transitions that accept the character.
     */
    private HashMap<Character, Transition> transitions_by_input;
    
    /**
     * Is the state accepting?
     */
    private boolean accepting;
    
    /**
     * The position of the state in the diagram representation.
     */
    Vec2 pos;
    
    /**
     * Constructs a Turing machine state.
     * 
     * The state is set to non-accepting and placed in origin initially.
     * 
     * @param name The name of the state.
     * @param container Container containing the state that is notified on name
     * changes.
     */
    public State(String name, ByNameContainer<State> container) {
        this.name = name;
        this.container = container;
        transitions = new HashSet<Transition>();
        transitions_by_input = new HashMap<Character, Transition>();
        accepting = false;
        pos = new Vec2();
    }
    
    /**
     * Tests whether the state is accepting.
     * 
     * @return true if accepting, false otherwise.
     */
    public boolean isAccepting() {
        return accepting;
    }
    
    /**
     * Sets the state accepting/non-accepting.
     * 
     * @param accepting true if the state should be set to accepting, false if
     * it should be set to non-accepting.
     */
    public void setAccepting(boolean accepting) {
        this.accepting = accepting;
    }
    
    /**
     * Gets the name of the state.
     * 
     * @return The name of the state.
     */
    public String getName() {
        return name;
    }
    
    /**
     * Sets the name of the state.
     * 
     * @param name New name of the state.
     * @throws NameInUseException if the name is already in use.
     */
    public void setName(String name) throws NameInUseException {
        if(container.has(name)) {
            throw new NameInUseException();
        }
        String oldname = this.name;
        this.name = name;
        container.nameChanged(oldname);
    }
    
    /**
     * Gets all transitions from the state.
     * 
     * @return HashSet of all the transitions.
     */
    public Set<Transition> getTransitions() {
        return Collections.unmodifiableSet(transitions);
    }
    
    /**
     * Gets transition by input characters.
     * 
     * @param c The input character.
     * @return The Transition from the state that has c in its input characters.
     */
    public Transition getTransitionByInput(char c) {
        return transitions_by_input.get(c);
    }
    
    /**
     * Gets the set of input characters for the state.
     * 
     * @return The set of characters such that there is transition that reads
     * the character.
     */
    public Set<Character> getInputCharacters() {
        return Collections.unmodifiableSet(transitions_by_input.keySet());
    }
    
    /**
     * Adds transition to the state.
     * 
     * @param transition The transition to be added.
     * @throws NameInUseException if some transition already has same input
     * character as transition.
     */
    public void addTransition(Transition transition) throws NameInUseException {
        String inputchars = transition.getInputCharacters();
        
        // Check for clashes with other transitions.
        for(int i = 0; i < inputchars.length(); i++) {
            char c = inputchars.charAt(i);
            if(
                transitions_by_input.containsKey(c) &&
                transitions_by_input.get(c) != transition
            ) {
                throw new NameInUseException(
                    "State already has transition for character \"" + c + "\""
                );
            }
        }
        
        // Add the transition and the mappings by input.
        transitions.add(transition);
        for(int i = 0; i < inputchars.length(); i++) {
            char c = inputchars.charAt(i);
            transitions_by_input.put(c, transition);
        }
    }
    
    /**
     * Removes transition from the state.
     * 
     * @param transition The transition to remove.
     */
    public void removeTransition(Transition transition) {
        // Remove from transitions.
        transitions.remove(transition);
        
        // Remove from transitions_by_input.
        String inputchars = transition.getInputCharacters();
        for(int i = 0; i < inputchars.length(); i++) {
            char c = inputchars.charAt(i);
            if(transitions_by_input.get(c) == transition) {
                transitions_by_input.remove(c);
            }
        }
    }
    
    /**
     * Get the position of the state.
     * 
     * @return The position of the state in the state diagram.
     */
    public Vec2 getPosition() {
        return pos;
    }
    
    /**
     * Set the position of the state.
     * 
     * @param pos The new position of the state in the state diagram.
     */
    public void setPosition(Vec2 pos) {
        this.pos = pos;
    }
}
