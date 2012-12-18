/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.helsinki.cs.turridevelop.logiikka;

import fi.helsinki.cs.turridevelop.exceptions.NameInUseException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

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
        proj.addMachine("mac");
        mac = proj.getMachine("mac");
    }
    
    @After
    public void tearDown() {
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
}
