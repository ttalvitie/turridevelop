package fi.helsinki.cs.turridevelop.logic;

/**
 * State transition that reads any of given input characters from the tape,
 * optionally writes given character, and optionally moves to the left or to the
 * right.
 */
public class Transition {
    /**
     * The State the transition leads to.
     */
    private final State destination;
    
    /**
     * String of all possible input characters.
     */
    private final String inchar;
    
    /**
     * Output character or null if the tape should be left as is.
     */
    private final Character outchar;
    
    /**
     * The movement of the tape after reading the input and writing the output,
     * -1 means left, 0 stay and 1 right.
     */
    private final int movement;
    
    /**
     * Constructs Transition.
     * 
     * @param destination The destination state.
     * @param inchar String consisting of the possible characters to read in the
     * transition.
     * @param outchar Character to be written after writing, null if the tape
     * should be left as is.
     * @param movement Movement after reading and writing, negative moves left,
     * positive to the right and zero stays.
     */
    public Transition(State destination, String inchar, Character outchar, int movement) {
        this.destination = destination;
        this.inchar = inchar;
        this.outchar = outchar;
        if(movement == 0) {
            this.movement = 0;
        } else {
            if(movement > 0) {
                this.movement = 1;
            } else {
                this.movement = -1;
            }
        }
    }
    
    /**
     * Equivalent to Transition(State, inchar, null, movement).
     */
    public Transition(State destination, String inchar, int movement) {
        this(destination, inchar, null, movement);
    }
    
    /**
     * Gets the destination of the transition.
     * 
     * @return The destination.
     */
    public State getDestination() {
        return destination;
    }
    
    /**
     * Gets the string of possible input characters.
     * 
     * @return The possible input characters.
     */
    public String getInputCharacters() {
        return inchar;
    }
    
    /**
     * Gets the output character.
     * 
     * @return The output character or null if this transition doesn't write
     * anything.
     */
    public Character getOutputCharacter() {
        return outchar;
    }
    
    /**
     * Get the movement of the tape in this transition.
     * 
     * @return -1 if the transition moves to the left on the tape, 1 if to the
     * right and 0 if the transition doesn't move the tape
     */
    public int getMovement() {
        return movement;
    }
}
