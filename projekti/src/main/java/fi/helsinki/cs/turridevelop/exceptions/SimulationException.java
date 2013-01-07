package fi.helsinki.cs.turridevelop.exceptions;

/**
 * Exception thrown by logic.Simulation when the start machine or start state
 * is not found or there is a submachine error, i.e. unknown machine as
 * submachine, missing start state on a submachine or infinite submachine loop.
 */
public class SimulationException extends Exception {

    /**
     * Creates a new instance of
     * <code>SimulationException</code> without detail message.
     */
    public SimulationException() {
    }

    /**
     * Constructs an instance of
     * <code>SimulationException</code> with the specified detail message.
     *
     * @param msg the detail message.
     */
    public SimulationException(String msg) {
        super(msg);
    }
}
