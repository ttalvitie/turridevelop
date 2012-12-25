package fi.helsinki.cs.turridevelop.logic;

import fi.helsinki.cs.turridevelop.exceptions.NameInUseException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;


class DummyStateNameStorage implements StateNameStorage {
    @Override
    public boolean onStateNameChange(String oldname) {
        return true;
    }
}

public class SimulationTest {
    DummyStateNameStorage dummy_obs;
    
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
        dummy_obs = new DummyStateNameStorage();
    }
    
    @After
    public void tearDown() {
    }
    
    @Test
    public void testAcceptWorks() throws NameInUseException {
        State x = new State("x", dummy_obs);
        State y = new State("y", dummy_obs);
        y.setAccepting(true);
        x.addTransition(new Transition(y, "abc"));
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
        x.addTransition(new Transition(y, "bc"));
        Simulation sim = new Simulation(new Tape("a"), x);
        sim.run();
        assertEquals(SimulationStatus.REJECTED, sim.getStatus());
        assertEquals(x, sim.getState());
    }
}
