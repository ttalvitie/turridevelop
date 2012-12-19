package fi.helsinki.cs.turridevelop.logic;

/**
 * Tape of a Turing machine.
 * 
 * Infinite string of characters, initially set to given empty character.
 */
public class Tape {
    /**
     * The character used outside tape.
     */
    private final char empty_char;
    
    /**
     * The explicitly stored part of the tape.
     */
    private StringBuilder tape;
    
    /**
     * Constructs empty tape.
     * 
     * @param empty_char The character of all positions of the tape initially.
     */
    public Tape(char empty_char) {
        this.empty_char = empty_char;
        tape = new StringBuilder();
    }
    
    /**
     * Equivalent to Tape('␣').
     */
    public Tape() {
        this('␣');
    }
    
    /**
     * Get the character used as empty character.
     * 
     * @return The empty character for this tape.
     */
    public char getEmptyCharacter() {
        return empty_char;
    }
    
    /**
     * Gets the character at given position on the tape.
     * 
     * @param pos The position on the tape. The leftmost position is 0.
     * @return The character at pos.
     */
    public char getCharacterAt(int pos) {
        if(pos < tape.length()) {
            return tape.charAt(pos);
        } else {
            return empty_char;
        }
    }
    
    /**
     * Sets the character at given position on the tape.
     * 
     * @param pos The position on the tape. The leftmost position is 0.
     * @param c The character to put.
     */
    public void setCharacterAt(int pos, char c) {
        // Extend the tape with empty characters until pos is inside it.
        while(pos >= tape.length()) {
            tape.append(empty_char);
        }
        
        tape.setCharAt(pos, c);
    }
}