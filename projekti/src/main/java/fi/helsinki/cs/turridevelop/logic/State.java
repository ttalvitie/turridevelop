package fi.helsinki.cs.turridevelop.logic;

import fi.helsinki.cs.turridevelop.exceptions.NameInUseException;
import java.util.HashMap;
import java.util.HashSet;

/**
 * State of a Turing machine.
 */
public class State {
    /**
     * Name of the state.
     */
    private String name;
    
    /**
     * Storage that is notified when name is changed.
     */
    private StateNameStorage name_storage;
    
    /**
     * Transitions from this state.
     */
    private HashSet<Transition> transitions;
    
    /**
     * Hashmap from input characters to transitions that accept the character.
     */
    private HashMap<Character, Transition> transitions_by_input;
    
    /**
     * Constructs a Turing machine state.
     * 
     * @param name The name of the state.
     * @param name_storage Object for which onStateNameChange is called with
     * the old name of the State after the name has changed. 
     */
    public State(String name, StateNameStorage name_storage) {
        this.name = name;
        this.name_storage = name_storage;
        transitions = new HashSet<Transition>();
        transitions_by_input = new HashMap<Character, Transition>();
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
        String oldname = this.name;
        this.name = name;
        if(!name_storage.onStateNameChange(oldname)) {
            this.name = oldname;
            throw new NameInUseException();
        }
    }
    
    /**
     * Gets all transitions from the state.
     * 
     * @return HashSet of all the transitions.
     */
    HashSet<Transition> getTransitions() {
        return transitions;
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
}
