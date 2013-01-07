package fi.helsinki.cs.turridevelop.gui;

/**
 * Wrapper for String for machine name list that handles empty names correctly.
 */
public class MachineName {
    private String name;
    
    /**
     * Constructs MachineName.
     * 
     * @param name The machine name to contain.
     */
    public MachineName(String name) {
        this.name = name;
    }
    
    /**
     * Gets the contained machine name.
     * 
     * @return The machine name.
     */
    public String getName() {
        return name;
    }
    
    @Override
    public boolean equals(Object other) {
        if(!(other instanceof MachineName)) {
            return false;
        }
        
        return name.equals(((MachineName) other).getName());
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
    
    @Override
    public String toString() {
        if(name.equals("")) {
            return "<empty name>";
        } else {
            return name;
        }
    }
}
