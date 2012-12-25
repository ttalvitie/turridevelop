package fi.helsinki.cs.turridevelop.logic;

import fi.helsinki.cs.turridevelop.exceptions.NameInUseException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;


class DummyStateObserver implements StateObserver {
    @Override
    public boolean onStateNameChange(String oldname) {
        return true;
    }
}

public class SimulationTest {
    DummyStateObserver dummy_obs;
    
    public SimulationTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        dummy_obs = new DummyStateObserver();
    }
    
    @After
    public void tearDown() {
    }
    
    @Test
    public void testFirstAcceptWorks() throws NameInUseException {
        State x = new State("x", dummy_obs);
        x.setAccepting(true);
        Simulation sim = new Simulation(new Tape(), x);
        assertEquals(SimulationStatus.ACCEPTED, sim.getStatus());
        assertEquals(x, sim.getState());
    }
    
    @Test
    public void testAcceptedStepDoesNothing() throws NameInUseException {
        State x = new State("x", dummy_obs);
        x.setAccepting(true);
        Simulation sim = new Simulation(new Tape(), x);
        sim.step();
        assertEquals(SimulationStatus.ACCEPTED, sim.getStatus());
        assertEquals(x, sim.getState());
    }
    
    @Test
    public void testRejectedStepDoesNothing() throws NameInUseException {
        State x = new State("x", dummy_obs);
        Simulation sim = new Simulation(new Tape(), x);
        for(int i = 0; i < 2; i++) {
            sim.step();
            assertEquals(SimulationStatus.REJECTED, sim.getStatus());
            assertEquals(x, sim.getState());
        }
    }
    
    @Test
    public void testAcceptWorks() throws NameInUseException {
        State x = new State("x", dummy_obs);
        State y = new State("y", dummy_obs);
        y.setAccepting(true);
        x.addTransition(new Transition(y, "abc", 0));
        Simulation sim = new Simulation(new Tape("a"), x);
        sim.run();
        assertEquals(SimulationStatus.ACCEPTED, sim.getStatus());
        assertEquals(y, sim.getState());
    }
    
    @Test
    public void testRejectWorks() throws NameInUseException {
        State x = new State("x", dummy_obs);
        State y = new State("y", dummy_obs);
        y.setAccepting(true);
        x.addTransition(new Transition(y, "bc", 0));
        Simulation sim = new Simulation(new Tape("a"), x);
        sim.run();
        assertEquals(SimulationStatus.REJECTED, sim.getStatus());
        assertEquals(x, sim.getState());
    }
    
    @Test
    public void testWriteWorks() throws NameInUseException {
        State x = new State("x", dummy_obs);
        State y = new State("y", dummy_obs);
        y.setAccepting(true);
        x.addTransition(new Transition(y, "a", 'b', 0));
        Simulation sim = new Simulation(new Tape("a"), x);
        sim.run();
        assertEquals('b', sim.getHead().getTape().getCharacterAt(0));
    }
    
    @Test
    public void testMoveRightWorks() throws NameInUseException {
        State x = new State("x", dummy_obs);
        State y = new State("y", dummy_obs);
        y.setAccepting(true);
        x.addTransition(new Transition(y, "a", 1));
        Simulation sim = new Simulation(new Tape("a"), x);
        sim.run();
        assertEquals(1, sim.getHead().getPosition());
    } 
    
    @Test
    public void testMoveLeftInLeftmostWorks() throws NameInUseException {
        State x = new State("x", dummy_obs);
        State y = new State("y", dummy_obs);
        y.setAccepting(true);
        x.addTransition(new Transition(y, "a", -1));
        Simulation sim = new Simulation(new Tape("a"), x);
        sim.run();
        assertEquals(0, sim.getHead().getPosition());
    } 
    
    @Test
    public void testMoveLeftWorks() throws NameInUseException {
        State x = new State("x", dummy_obs);
        State y = new State("y", dummy_obs);
        State z = new State("z", dummy_obs);
        State w = new State("w", dummy_obs);
        w.setAccepting(true);
        x.addTransition(new Transition(y, "a", 1));
        y.addTransition(new Transition(z, "b", 1));
        z.addTransition(new Transition(w, "c", -1));
        Simulation sim = new Simulation(new Tape("abc"), x);
        sim.run();
        assertEquals(SimulationStatus.ACCEPTED, sim.getStatus());
        assertEquals(w, sim.getState());
        assertEquals(1, sim.getHead().getPosition());
    } 
}
