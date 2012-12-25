package fi.helsinki.cs.turridevelop.logic;

/**
 * Observer for tracking changes of States.
 */
public interface StateObserver {
    /**
     * Function called after the name of a state changes.
     * 
     * @param oldname The old name of the state.
     * @return If false, the new name is already in use and the old name should
     * be restored.
     */
    boolean onStateNameChange(String oldname);
}
