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
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

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
        
        s11.setSubmachine("m2");
        s13.setSubmachine("m2");
        
        s11.addTransition(new Transition(s12, "abc", -1));
        s11.addTransition(new Transition(s13, "dxy", 'b', 0));
        s12.addTransition(new Transition(s11, "xyz", 'x', 1));
        s13.addTransition(new Transition(s11, "abz", 1));
        
        HashMap<String, JSONObject> saved = TurrOutput.projectToJSON(proj);
        Project proj2 = TurrInput.JSONToProject(saved);
        
        assertTrue(Util.projectsEqual(proj, proj2));
    }
}
