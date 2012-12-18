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
        proj.addMachine("mac");
        mac = proj.getMachine("mac");
        mac.addState("state");
        state = mac.getState("state");
    }
    
    @After
    public void tearDown() {
    }
    
    @Test
    public void testSetNameWorks() throws NameInUseException {
        state.setName("asd");
        assertEquals("asd", state.getName());
    }
}
