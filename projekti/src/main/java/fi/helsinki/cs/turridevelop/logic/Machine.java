package fi.helsinki.cs.turridevelop.logic;

import fi.helsinki.cs.turridevelop.exceptions.NameInUseException;
import fi.helsinki.cs.turridevelop.util.ByNameContainer;
import fi.helsinki.cs.turridevelop.util.ByNameStored;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

/**
 * A Turing machine.
 */
public class Machine implements ByNameStored {
    /**
     * Name of the machine.
     */
    private String name;
    
    /**
     * The container containing the machine.
     */
    private ByNameContainer<Machine> container;
    
    /**
     * States of the machine.
     */
    private ByNameContainer<State> states;
    
    /**
     * Constructs empty Turing machine.
     * 
     * @param name Name of the machine.
     * @param container Container containing the machine that is notified on
     * name changes.
     */
    public Machine(String name, ByNameContainer<Machine> container) {
        this.name = name;
        this.container = container;
        this.states = new ByNameContainer<State>();
    }
    
    /**
     * Gets the name of the machine.
     * 
     * @return The name of the machine.
     */
    public String getName() {
        return name;
    }
    
    /**
     * Sets the name of the machine.
     * 
     * @param name 
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
     * Gets the state by name.
     * 
     * @param name The name of the state.
     * @return The state or null if not found.
     */
    public State getState(String name) {
        return states.get(name);
    }
    
    /**
     * Get the set of state names.
     * 
     * @return The set of state names.
     */
    public Set<String> getStateNames() {
        return states.getNames();
    }
    
    /**
     * Adds a state to the machine.
     * 
     * @param name The name of the state.
     * @throws NameInUseException if the name is already in use.
     * @return The added state.
     */
    public State addState(String name) throws NameInUseException {
        State state = new State(name, states);
        states.add(state);
        return state;
    }
}
