package fi.helsinki.cs.turridevelop.logiikka;

import fi.helsinki.cs.turridevelop.exceptions.NameInUseException;

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
}
