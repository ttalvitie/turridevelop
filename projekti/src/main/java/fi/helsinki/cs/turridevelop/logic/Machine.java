package fi.helsinki.cs.turridevelop.logic;

import fi.helsinki.cs.turridevelop.exceptions.NameInUseException;
import java.util.HashMap;

/**
 * A Turing machine.
 */
public class Machine implements StateNameStorage {
    private String name;
    private MachineNameStorage name_storage;
    private HashMap<String, State> states;
    
    /**
     * Constructs empty Turing machine.
     * 
     * @param name Name of the machine.
     * @param name_storage Object for which onMachineNameChange is called with
     * the old name of the Machine after the name has changed.
     */
    public Machine(String name, MachineNameStorage name_storage) {
        this.name = name;
        this.name_storage = name_storage;
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
        if(!name_storage.onMachineNameChange(oldname)) {
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
