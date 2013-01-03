package fi.helsinki.cs.turridevelop.logic;

import fi.helsinki.cs.turridevelop.exceptions.NameInUseException;
import fi.helsinki.cs.turridevelop.file.TurrOutput;
import fi.helsinki.cs.turridevelop.util.ByNameContainer;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;


public class SimulationTest {
    Machine mac;
    
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
        mac = new Machine("mac", new ByNameContainer<Machine>());
    }
    
    @After
    public void tearDown() {
    }
    
    @Test
    public void testFirstAcceptWorks() throws NameInUseException {
        State x = mac.addState("x");
        x.setAccepting(true);
        Simulation sim = new Simulation(new Tape(), x);
        assertEquals(SimulationStatus.ACCEPTED, sim.getStatus());
        assertEquals(x, sim.getState());
    }
    
    @Test
    public void testAcceptedStepDoesNothing() throws NameInUseException {
        State x = mac.addState("x");
        x.setAccepting(true);
        Simulation sim = new Simulation(new Tape(), x);
        sim.step();
        assertEquals(SimulationStatus.ACCEPTED, sim.getStatus());
        assertEquals(x, sim.getState());
    }
    
    @Test
    public void testRejectedStepDoesNothing() throws NameInUseException {
        State x = mac.addState("x");
        Simulation sim = new Simulation(new Tape(), x);
        for(int i = 0; i < 2; i++) {
            sim.step();
            assertEquals(SimulationStatus.REJECTED, sim.getStatus());
            assertEquals(x, sim.getState());
        }
    }
    
    @Test
    public void testAcceptWorks() throws NameInUseException {
        State x = mac.addState("x");
        State y = mac.addState("y");
        y.setAccepting(true);
        x.addTransition(new Transition(y, "abc", 0));
        Simulation sim = new Simulation(new Tape("a"), x);
        sim.run();
        assertEquals(SimulationStatus.ACCEPTED, sim.getStatus());
        assertEquals(y, sim.getState());
    }
    
    @Test
    public void testRejectWorks() throws NameInUseException {
        State x = mac.addState("x");
        State y = mac.addState("y");
        y.setAccepting(true);
        x.addTransition(new Transition(y, "bc", 0));
        Simulation sim = new Simulation(new Tape("a"), x);
        sim.run();
        assertEquals(SimulationStatus.REJECTED, sim.getStatus());
        assertEquals(x, sim.getState());
    }
    
    @Test
    public void testWriteWorks() throws NameInUseException {
        State x = mac.addState("x");
        State y = mac.addState("y");
        y.setAccepting(true);
        x.addTransition(new Transition(y, "a", 'b', 0));
        Simulation sim = new Simulation(new Tape("a"), x);
        sim.run();
        assertEquals('b', sim.getHead().getTape().getCharacterAt(0));
    }
    
    @Test
    public void testMoveRightWorks() throws NameInUseException {
        State x = mac.addState("x");
        State y = mac.addState("y");
        y.setAccepting(true);
        x.addTransition(new Transition(y, "a", 1));
        Simulation sim = new Simulation(new Tape("a"), x);
        sim.run();
        assertEquals(1, sim.getHead().getPosition());
    } 
    
    @Test
    public void testMoveLeftInLeftmostWorks() throws NameInUseException {
        State x = mac.addState("x");
        State y = mac.addState("y");
        y.setAccepting(true);
        x.addTransition(new Transition(y, "a", -1));
        Simulation sim = new Simulation(new Tape("a"), x);
        sim.run();
        assertEquals(0, sim.getHead().getPosition());
    } 
    
    @Test
    public void testMoveLeftWorks() throws NameInUseException {
        State x = mac.addState("x");
        State y = mac.addState("y");
        State z = mac.addState("z");
        State w = mac.addState("w");
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
