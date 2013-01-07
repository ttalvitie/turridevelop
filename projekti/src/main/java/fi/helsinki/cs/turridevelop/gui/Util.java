package fi.helsinki.cs.turridevelop.gui;

import fi.helsinki.cs.turridevelop.logic.Transition;

/**
 * GUI utilities.
 */
public class Util {
    /**
     * Gets the text used to describe transition.
     * 
     * @param transition The transition to describe.
     * @param destination Set to true if the text should include the name of the
     * destination state.
     * @return The string describing the transition.
     */
    public static String getTransitionText(
        Transition transition,
        boolean destination
    ) {
        String input = transition.getInputCharacters();
        Character output = transition.getOutputCharacter();
        char movement = 'X';
        switch(transition.getMovement()) {
            case -1:
                movement = 'L';
                break;
            case 0:
                movement = 'S';
                break;
            case 1:
                movement = 'R';
                break;
        }
        
        String destination_str = "";
        if(destination) {
            destination_str = transition.getDestination().getName() +  " : ";
        }
        
        if(output == null) {
            return destination_str + input + " -> " + movement;
        } else {
            return destination_str + input + " -> " + output + ", " + movement;
        }
    }
}
