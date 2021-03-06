package fi.helsinki.cs.turridevelop.logic;

import fi.helsinki.cs.turridevelop.logic.Machine;
import fi.helsinki.cs.turridevelop.logic.Project;
import fi.helsinki.cs.turridevelop.exceptions.NameInUseException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class ProjectTest {
    Project proj;
    
    public ProjectTest() { }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        proj = new Project();
    }
    
    @After
    public void tearDown() {
    }
    
    @Test(expected=NameInUseException.class)
    public void testDetectsNameClashWhenAdding() throws NameInUseException {
        proj.addMachine("asd");
        proj.addMachine("asd");
    }
    
    @Test(expected=NameInUseException.class)
    public void testDetectsNameClashWhenRenaming() throws NameInUseException {
        proj.addMachine("asd");
        proj.addMachine("bsd");
        proj.getMachine("bsd").setName("asd");
    }
    
    @Test
    public void testNameClashDoesNothingWhenAdding() throws NameInUseException {
        proj.addMachine("asd");
        Machine m = proj.getMachine("asd");
        try {
            proj.addMachine("asd");
        } catch(NameInUseException e) { }
        assertEquals(m, proj.getMachine("asd"));
    }
    
    @Test
    public void testNameClashDoesNothingWhenRenaming() throws NameInUseException {
        proj.addMachine("asd");
        proj.addMachine("bsd");
        Machine masd = proj.getMachine("asd");
        Machine mbsd = proj.getMachine("bsd");
        try {
            proj.getMachine("bsd").setName("asd");
        } catch(NameInUseException e) { }
        assertEquals(masd, proj.getMachine("asd"));
        assertEquals("asd", masd.getName());
        assertEquals(mbsd, proj.getMachine("bsd"));
        assertEquals("bsd", mbsd.getName());
    }
    
    @Test
    public void testRemoveMachineWorks() throws NameInUseException {
        proj.addMachine("a");
        proj.addMachine("b");
        
        proj.removeMachine("a");
        
        assertEquals(1, proj.getMachineNames().size());
        assertEquals("b", proj.getMachineNames().iterator().next());
    }
}
