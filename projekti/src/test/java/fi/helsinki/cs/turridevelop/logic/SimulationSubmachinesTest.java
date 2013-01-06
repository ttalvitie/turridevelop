package fi.helsinki.cs.turridevelop.logic;

import fi.helsinki.cs.turridevelop.exceptions.NameInUseException;
import fi.helsinki.cs.turridevelop.exceptions.SimulationException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test class for testing submachines in Simulation.
 */
public class SimulationSubmachinesTest {
    Project proj;
    Machine mac;
    State x;
    State y;
    State z;
    State w;
    Machine win;
    State a;
    State b;
    State c;
    State d;
    Machine lin;
    State m;
    State n;
    State o;
    State p;
    
    public SimulationSubmachinesTest() {
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
        x = mac.addState("start");
        y = mac.addState("statey");
        z = mac.addState("statez");
        w = mac.addState("statew");
        win = proj.addMachine("win");
        a = win.addState("start");
        b = win.addState("stateb");
        c = win.addState("statec");
        d = win.addState("stated");
        lin = proj.addMachine("lin");
        m = lin.addState("start");
        n = lin.addState("staten");
        o = lin.addState("stateo");
        p = lin.addState("statep");
    }
    
    @After
    public void tearDown() {
    }
    
    /**
     * Basic test case configuration for single submachine used by some tests.
     */
    private void basicTestConfiguration() throws NameInUseException {
        x.addTransition(new Transition(y, "a", 1));
        y.addTransition(new Transition(z, "a", 0));
        z.setAccepting(true);
        y.setSubmachine("win");
        
        a.addTransition(new Transition(b, "x", 'X', 1));
        b.addTransition(new Transition(c, "y", 'Y', -1));
        c.addTransition(new Transition(d, "X", -1));
        d.setAccepting(true);
    }
    
    @Test
    public void testSubmachinesWork() throws NameInUseException, SimulationException {
        basicTestConfiguration();
        
        Simulation sim = new Simulation(proj, "mac", new Tape("axy"));
        assertEquals(x, sim.getState());
        assertEquals(mac, sim.getMachine());
        sim.step();
        assertEquals(a, sim.getState());
        assertEquals(win, sim.getMachine());
        sim.step();
        assertEquals(b, sim.getState());
        assertEquals(win, sim.getMachine());
        sim.step();
        assertEquals(c, sim.getState());
        assertEquals(win, sim.getMachine());
        sim.step();
        assertEquals(y, sim.getState());
        assertEquals(mac, sim.getMachine());
        sim.step();
        assertEquals(z, sim.getState());
        assertEquals(mac, sim.getMachine());
        assertEquals(SimulationStatus.ACCEPTED, sim.getStatus());
        assertEquals("aXY", sim.getTape().getContents());
    }
    
    @Test
    public void testSubmachineRejectWorks() throws NameInUseException, SimulationException {
        basicTestConfiguration();
        
        Simulation sim = new Simulation(proj, "mac", new Tape("axY"));
        sim.run();
        assertEquals(SimulationStatus.REJECTED, sim.getStatus());
        assertEquals(b, sim.getState());
        assertEquals(win, sim.getMachine());
    }
    
    @Test
    public void testAcceptingStartStateWithSubmachine() throws NameInUseException, SimulationException {
        x.setAccepting(true);
        x.setSubmachine("win");
        a.addTransition(new Transition(b, "x", 'X', 1));
        b.addTransition(new Transition(c, "y", 'Y', 0));
        c.setAccepting(true);
        
        Simulation sim = new Simulation(proj, "mac", new Tape("xy"));
        sim.run();
        assertEquals(SimulationStatus.ACCEPTED, sim.getStatus());
        assertEquals(x, sim.getState());
        assertEquals(mac, sim.getMachine());
        assertEquals("XY", sim.getTape().getContents());
    }
    
    @Test
    public void testThreeMachines() throws NameInUseException, SimulationException {
        x.setAccepting(true);
        a.setAccepting(true);
        o.setAccepting(true);
        
        x.setSubmachine("win");
        a.setSubmachine("lin");
        m.addTransition(new Transition(n, "a", 'b', 0));
        n.addTransition(new Transition(o, "b", 'c', 0));
        
        Simulation sim = new Simulation(proj, "mac", new Tape("aasd"));
        sim.run();
        
        assertEquals(SimulationStatus.ACCEPTED, sim.getStatus());
        assertEquals(x, sim.getState());
        assertEquals(mac, sim.getMachine());
        assertEquals("casd", sim.getTape().getContents());
    }
}
