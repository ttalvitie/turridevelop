package fi.helsinki.cs.turridevelop.gui;

/**
 * Handler for handling the events from a RunPanel.
 */
public interface RunPanelEventHandler {
    /**
     * Function called when a run panel should be closed.
     * 
     * @param panel The panel that should be closed.
     */
    public void runPanelClosed(RunPanel panel);
    
    /**
     * Function called when a state should be shown as current state for the
     * run.
     * 
     * @param machine The machine of the state.
     * @param state The state.
     */
    public void runPanelShowState(String machine, String state);
}
