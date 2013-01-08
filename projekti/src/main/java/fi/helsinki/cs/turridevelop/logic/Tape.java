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
     * Constructs tape.
     * 
     * @param empty_char The character of all positions of the tape initially.
     * @param input The content of the initial portion of the tape.
     */
    public Tape(String input, char empty_char) {
        this.empty_char = empty_char;
        tape = new StringBuilder(input);
    }
    
    /**
     * Equivalent to Tape("", '␣').
     */
    public Tape() {
        this("", '␣');
    }
    
    /**
     * Equivalent to Tape(input, '␣').
     */
    public Tape(String input) {
        this(input, '␣');
    }
    
    /**
     * Equivalent to Tape("", empty_char).
     */
    public Tape(char empty_char) {
        this("", empty_char);
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
    
    /**
     * Get the contents of the tape as a string.
     * 
     * @return The minimal leading portion of the tape such that all characters
     * after it are empty characters.
     */
    public String getContents() {
        removeEmptyCharacters();
        
        return tape.toString();
    }
    
    /**
     * Get the length of getContents().
     * 
     * @return The length of string returned by getContents().
     */
    public int getContentsLength() {
        removeEmptyCharacters();
        
        return tape.length();
    }
    
    /**
     * Remove empty characters from the end of 'tape'.
     */
    private void removeEmptyCharacters() {
        while(
            tape.length() != 0 &&
            tape.charAt(tape.length() - 1) == getEmptyCharacter()
        ) {
            tape.deleteCharAt(tape.length() - 1);
        }
    }
    
    /**
     * Set the contents of the tape.
     * 
     * @param contents The new content of the tape. The characters after the end
     * of the string will be set to the empty character.
     */
    public void setContents(String contents) {
        tape = new StringBuilder(contents);
    }
}
