package fi.helsinki.cs.turridevelop.logiikka;

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
     * Automatically calls onMachineNameChange of the MachineNameStorage.
     * 
     * @param name 
     */
    public void setName(String name) {
        String oldname = name;
        this.name = name;
        name_storage.onMachineNameChange(oldname);
    }
    
    /**
     * Gets the state by name.
     * 
     * @param name The name of the state.
     * @return The state or null if not found.
     */
    State getState(String name) {
        return states.get(name);
    }

    public void onStateNameChange(String oldname) {
        State state = states.get(oldname);
        states.remove(oldname);
        states.put(state.getName(), state);
    }
}
