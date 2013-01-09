
package fi.helsinki.cs.turridevelop.file;

import fi.helsinki.cs.turridevelop.logic.Machine;
import fi.helsinki.cs.turridevelop.logic.Project;
import fi.helsinki.cs.turridevelop.logic.State;
import fi.helsinki.cs.turridevelop.logic.Transition;
import fi.helsinki.cs.turridevelop.util.Vec2;

/**
 * Utility functions for file tests.
 */
public class Util {
    
    /**
     * Test whether projects are equal.
     */
    public static boolean projectsEqual(Project p1, Project p2) {
        if(!p1.getMachineNames().equals(p2.getMachineNames())) {
            return false;
        }
        
        for(String name : p1.getMachineNames()) {
            Machine m1 = p1.getMachine(name);
            Machine m2 = p2.getMachine(name);
            
            if(!machinesEqual(m1, m2)) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Test whether machines are equal.
     */
    public static boolean machinesEqual(Machine m1, Machine m2) {
        if(!m1.getStateNames().equals(m2.getStateNames())) {
            return false;
        }
        
        for(String name : m1.getStateNames()) {
            State s1 = m1.getState(name);
            State s2 = m2.getState(name);
            
            if(!statesEqual(s1, s2)) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Test whether statesd are equal.
     */
    public static boolean statesEqual(State s1, State s2) {
        if(s1.isAccepting() != s2.isAccepting()) {
            return false;
        }
        
        String sub1 = s1.getSubmachine();
        String sub2 = s2.getSubmachine();
        if(sub1 == null) {
            if(sub2 != null) {
                return false;
            }
        } else {
            if(!sub1.equals(sub2)) {
                return false;
            }
        }
        
        if(Vec2.sub(s1.getPosition(), s2.getPosition()).getNorm() != 0.0) {
            return false;
        }
        
        if(!s1.getInputCharacters().equals(s2.getInputCharacters())) {
            return false;
        }
        
        for(char inputchar : s1.getInputCharacters()) {
            Transition t1 = s1.getTransitionByInput(inputchar);
            Transition t2 = s2.getTransitionByInput(inputchar);
            
            if(!transitionsEqual(t1, t2)) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Test whether transitions are equal.
     */
    public static boolean transitionsEqual(Transition t1, Transition t2) {
        String name1 = t1.getDestination().getName();
        String name2 = t2.getDestination().getName();
        
        Character outchar1 = t1.getOutputCharacter();
        Character outchar2 = t2.getOutputCharacter();
        
        boolean outchars_equal;
        if(outchar1 == null) {
            outchars_equal = outchar2 == null;
        } else {
            outchars_equal = outchar1.equals(outchar2);
        }
        
        return name1.equals(name2) &&
               t1.getInputCharacters().equals(t2.getInputCharacters()) &&
               outchars_equal &&
               t1.getMovement() == t2.getMovement();
            
    }
}
