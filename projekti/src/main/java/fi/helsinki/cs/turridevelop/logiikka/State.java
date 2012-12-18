package fi.helsinki.cs.turridevelop.logiikka;

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
    
    public State(String name, StateNameStorage name_storage) {
        this.name = name;
        this.name_storage = name_storage;
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
     * Automatically calls onStateNameChange of the StateNameStorage.
     * 
     * @param name New name of the state.
     */
    public void setName(String name) {
        String oldname = name;
        this.name = name;
        name_storage.onStateNameChange(oldname);
    }
}
