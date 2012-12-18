package fi.helsinki.cs.turridevelop.exceptions;

/**
 * Exception thrown when name is already in use.
 */
public class NameInUseException extends Exception {

    /**
     * Creates a new instance of
     * <code>NameInUseException</code> without detail message.
     */
    public NameInUseException() {
    }

    /**
     * Constructs an instance of
     * <code>NameInUseException</code> with the specified detail message.
     *
     * @param msg the detail message.
     */
    public NameInUseException(String msg) {
        super(msg);
    }
}
