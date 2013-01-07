package fi.helsinki.cs.turridevelop.logic;

import fi.helsinki.cs.turridevelop.exceptions.NameInUseException;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author topi
 */
public class MachineTest {
    Project proj;
    Machine mac;
    
    public MachineTest() {
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
    }
    
    @After
    public void tearDown() {
    }
    
    @Test
    public void testSetNameWorks() throws NameInUseException {
        mac.setName("linux");
        assertEquals("linux", mac.getName());
    }
    
    @Test(expected=NameInUseException.class)
    public void testDetectsNameClashWhenAdding() throws NameInUseException {
        mac.addState("asd");
        mac.addState("asd");
    }
    
    @Test(expected=NameInUseException.class)
    public void testDetectsNameClashWhenRenaming() throws NameInUseException {
        mac.addState("asd");
        mac.addState("bsd");
        mac.getState("bsd").setName("asd");
    }
    
    @Test
    public void testNameClashDoesNothingWhenAdding() throws NameInUseException {
        mac.addState("asd");
        State m = mac.getState("asd");
        try {
            mac.addState("asd");
        } catch(NameInUseException e) { }
        assertEquals(m, mac.getState("asd"));
    }
    
    @Test
    public void testNameClashDoesNothingWhenRenaming() throws NameInUseException {
        mac.addState("asd");
        mac.addState("bsd");
        State masd = mac.getState("asd");
        State mbsd = mac.getState("bsd");
        try {
            mac.getState("bsd").setName("asd");
        } catch(NameInUseException e) { }
        assertEquals(masd, mac.getState("asd"));
        assertEquals("asd", masd.getName());
        assertEquals(mbsd, mac.getState("bsd"));
        assertEquals("bsd", mbsd.getName());
    }
    
    @Test
    public void testRemoveStateRemovesTransitions() throws NameInUseException {
        State a = mac.addState("a");
        State b = mac.addState("b");
        a.addTransition(new Transition(b, "x", 'y', 0));
        mac.removeState("b");
        assertEquals(0, a.getTransitions().size());
    }
}
