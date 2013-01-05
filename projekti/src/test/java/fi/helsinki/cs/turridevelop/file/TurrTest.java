package fi.helsinki.cs.turridevelop.file;

import fi.helsinki.cs.turridevelop.exceptions.MalformedFileException;
import fi.helsinki.cs.turridevelop.exceptions.NameInUseException;
import fi.helsinki.cs.turridevelop.logic.Machine;
import fi.helsinki.cs.turridevelop.logic.Project;
import fi.helsinki.cs.turridevelop.logic.State;
import fi.helsinki.cs.turridevelop.logic.Transition;
import fi.helsinki.cs.turridevelop.util.Vec2;
import java.util.HashMap;
import org.json.JSONObject;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests using both TurrInput and TurrOutput.
 */
public class TurrTest {
    
    public TurrTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }
    
    @Test
    public void testSavingAndLoadingDoesNotChange()
    throws NameInUseException, MalformedFileException {
        Project proj = new Project();
        
        Machine m1 = proj.addMachine("m1");
        Machine m2 = proj.addMachine("m2");
        
        State s11 = m1.addState("s11");
        s11.setPosition(new Vec2(51.21, -563.2));
        State s12 = m1.addState("s12");
        s12.setPosition(new Vec2(-3.0, 0.0));
        State s13 = m1.addState("s13");
        s13.setPosition(new Vec2(1.3, 3.7));
        State s21 = m2.addState("s21");
        s21.setPosition(new Vec2(-122.6231, 4.54325134));
        State s22 = m2.addState("s22");
        s22.setPosition(new Vec2(0.0, 0.0));
        
        s13.setAccepting(true);
        s22.setAccepting(true);
        
        s11.addTransition(new Transition(s12, "abc", -1));
        s11.addTransition(new Transition(s13, "dxy", 'b', 0));
        s12.addTransition(new Transition(s11, "xyz", 'x', 1));
        s13.addTransition(new Transition(s11, "abz", 1));
        
        HashMap<String, JSONObject> saved = TurrOutput.projectToJSON(proj);
        Project proj2 = TurrInput.JSONToProject(saved);
        
        assertTrue(projectsEqual(proj, proj2));
    }
    
    private static boolean projectsEqual(Project p1, Project p2) {
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
    
    private static boolean machinesEqual(Machine m1, Machine m2) {
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
    
    private static boolean statesEqual(State s1, State s2) {
        if(s1.isAccepting() != s2.isAccepting()) {
            return false;
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
    
    private static boolean transitionsEqual(Transition t1, Transition t2) {
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
