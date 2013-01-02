/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.helsinki.cs.turridevelop.exceptions;

/**
 *
 * @author topi
 */
public class FilesystemException extends Exception {

    /**
     * Creates a new instance of
     * <code>FilesystemException</code> without detail message.
     */
    public FilesystemException() {
    }

    /**
     * Constructs an instance of
     * <code>FilesystemException</code> with the specified detail message.
     *
     * @param msg the detail message.
     */
    public FilesystemException(String msg) {
        super(msg);
    }
}
