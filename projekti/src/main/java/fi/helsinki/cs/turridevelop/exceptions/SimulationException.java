package fi.helsinki.cs.turridevelop.exceptions;

/**
 * Exception thrown by logic.Simulation when the project is inconsistent and
 * cannot be simulated further.
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
