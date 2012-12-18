package fi.helsinki.cs.turridevelop.logiikka;

/**
 * Observer for tracking changes of State names.
 */
public interface StateNameStorage {
    /**
     * Function called after the name of a state changes.
     * 
     * @param oldname The old name of the state.
     */
    void onStateNameChange(String oldname);
}
