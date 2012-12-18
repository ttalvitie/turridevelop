package fi.helsinki.cs.turridevelop.logiikka;

import fi.helsinki.cs.turridevelop.exceptions.NameInUseException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class StateTest {
    Project proj;
    Machine mac;
    State state;
    State state2;
    
    public StateTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() throws NameInUseException {
        proj = new Project();
        mac = proj.addMachine("mac");
        state = mac.addState("state");
        state2 = mac.addState("state2");
    }
    
    @After
    public void tearDown() {
    }
    
    @Test
    public void testSetNameWorks() throws NameInUseException {
        state.setName("asd");
        assertEquals("asd", state.getName());
    }
    
    @Test
    public void testAddTransitionWorks() throws NameInUseException {
        Transition t = new Transition(state2, "ab");
        state.addTransition(t);
        assertEquals(1, state.getTransitions().size());
        assertTrue(state.getTransitions().contains(t));
    }
    
    @Test
    public void testAddTransitionAddsByInput() throws NameInUseException {
        Transition t = new Transition(state2, "ab");
        state.addTransition(t);
        assertEquals(t, state.getTransitionByInput('a'));
        assertEquals(t, state.getTransitionByInput('b'));
        assertEquals(null, state.getTransitionByInput('c'));
    }
    
    @Test
    public void testRemoveTransitionRemovesByInput() throws NameInUseException {
        Transition t = new Transition(state2, "ab");
        state.addTransition(t);
        state.removeTransition(t);
        assertEquals(null, state.getTransitionByInput('a'));
        assertEquals(null, state.getTransitionByInput('b'));
        assertEquals(null, state.getTransitionByInput('c'));
    }
}
