package fi.helsinki.cs.turridevelop.gui;

import fi.helsinki.cs.turridevelop.logic.State;

/**
 * Handler for state choices from MachineView.
 */
public interface StateChoiceHandler {
    /**
     * Function called when a state is chosen or choosing is abandoned.
     * 
     * @param choice The chosen state or null if choosing was abandoned.
     */
    public void stateChosen(State choice);
}
