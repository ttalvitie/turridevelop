package fi.helsinki.cs.turridevelop.exceptions;

/**
 * Exception thrown when a file cannot be parsed.
 */
public class MalformedFileException extends Exception {

    /**
     * Creates a new instance of
     * <code>MalformedFileException</code> without detail message.
     */
    public MalformedFileException() {
    }

    /**
     * Constructs an instance of
     * <code>MalformedFileException</code> with the specified detail message.
     *
     * @param msg the detail message.
     */
    public MalformedFileException(String msg) {
        super(msg);
    }
}
