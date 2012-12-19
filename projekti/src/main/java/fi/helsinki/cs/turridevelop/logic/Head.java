/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.helsinki.cs.turridevelop.logic;

/**
 * Turing machine read/write head for accessing elements on tape.
 */
public class Head {
    /**
     * The tape the head is on.
     */
    private final Tape tape;
    
    /**
     * Position of the head on the tape.
     */
    private int pos;
    
    /**
     * Constructs Head.
     * 
     * The position is initially set to zero.
     * 
     * @param tape Tape to put the head on.
     */
    public Head(Tape tape) {
        this.tape = tape;
        pos = 0;
    }
    
    /**
     * Gets the tape.
     * 
     * @return The tape the head is on.
     */
    public Tape getTape() {
        return tape;
    }
    
    /**
     * Gets position on the tape.
     * 
     * @return The position of the head on the tape. Position zero is the
     * leftmost position.
     */
    public int getPosition() {
        return pos;
    }
    
    /**
     * Moves the head by movement.
     * 
     * If the head is moved past the leftmost position, it is reset to the
     * leftmost position.
     * 
     * @param movement The amount of movement. If negative, moves to the left,
     * and if positive, moves to the right.
     */
    public void move(int movement) {
        pos += movement;
        if(pos < 0) {
            pos = 0;
        }
    }
    
    /**
     * Reads character under the head on the tape.
     * 
     * @return The character under the head on the tape.
     */
    public char read() {
        return tape.getCharacterAt(pos);
    }
    
    /**
     * Write character under the head on the tape.
     * 
     * @param c The character to be written.
     */
    public void write(char c) {
        tape.setCharacterAt(pos, c);
    }
}
