package fi.helsinki.cs.turridevelop.gui;

/**
 * Handler for handling the closing of a RunPanel.
 */
public interface RunPanelCloseHandler {
    /**
     * Function called when a run panel should be closed.
     * 
     * @param panel The panel that should be closed.
     */
    public void runPanelClosed(RunPanel panel);
}
