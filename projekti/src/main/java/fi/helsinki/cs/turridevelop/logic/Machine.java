package fi.helsinki.cs.turridevelop.logic;

import fi.helsinki.cs.turridevelop.exceptions.NameInUseException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

/**
 * A Turing machine.
 */
public class Machine implements StateObserver {
    /**
     * Name of the machine.
     */
    private String name;
    
    /**
     * Observer that is notified on changes.
     */
    private MachineObserver observer;
    
    /**
     * Hash map of the states of the machine by their names.
     */
    private HashMap<String, State> states;
    
    /**
     * Constructs empty Turing machine.
     * 
     * @param name Name of the machine.
     * @param observer Observer that is notified of changes in the Machine.
     */
    public Machine(String name, MachineObserver observer) {
        this.name = name;
        this.observer = observer;
        this.states = new HashMap<String, State>();
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
        String oldname = this.name;
        this.name = name;
        if(!observer.onMachineNameChange(oldname)) {
            this.name = oldname;
            throw new NameInUseException();
        }
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
        return Collections.unmodifiableSet(states.keySet());
    }
    
    /**
     * Adds a state to the machine.
     * 
     * @param name The name of the state.
     * @throws NameInUseException if the name is already in use.
     * @return The added state.
     */
    public State addState(String name) throws NameInUseException {
        if(states.containsKey(name)) {
            throw new NameInUseException();
        }
        State state = new State(name, this);
        states.put(name, state);
        return state;
    }

    @Override
    public boolean onStateNameChange(String oldname) {
        State state = states.get(oldname);
        if(states.containsKey(state.getName())) {
            return false;
        }
        states.remove(oldname);
        states.put(state.getName(), state);
        return true;
    }
}
