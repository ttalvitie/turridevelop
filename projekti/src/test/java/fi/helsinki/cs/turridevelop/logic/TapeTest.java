package fi.helsinki.cs.turridevelop.logic;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class TapeTest {
    
    public TapeTest() {
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
    public void testReadInsideWorks() {
        Tape tape = new Tape("asd");
        assertEquals('a', tape.getCharacterAt(0));
        assertEquals('s', tape.getCharacterAt(1));
        assertEquals('d', tape.getCharacterAt(2));
    }
    
    @Test
    public void testReadOutsideWorks() {
        Tape tape = new Tape("asd");
        assertEquals(tape.getEmptyCharacter(), tape.getCharacterAt(3));
        assertEquals(tape.getEmptyCharacter(), tape.getCharacterAt(4));
        assertEquals(tape.getEmptyCharacter(), tape.getCharacterAt(7));
    }
    
    @Test
    public void testOtherEmptyCharacterWorks() {
        Tape tape = new Tape('z');
        assertEquals('z', tape.getEmptyCharacter());
        assertEquals('z', tape.getCharacterAt(0));
        assertEquals('z', tape.getCharacterAt(5));
    }
    
    @Test
    public void testGetContentsWorks() {
        Tape tape = new Tape("abcdef");
        assertEquals("abcdef", tape.getContents());
    }
    
    @Test
    public void testGetContentsWorksWithEmptyInInputEnd() {
        Tape tape = new Tape("abcdefzzzz", 'z');
        assertEquals("abcdef", tape.getContents());
    }
    
    @Test
    public void testGetContentsWorksIfEmpty() {
        Tape tape = new Tape("zzzz", 'z');
        assertEquals("", tape.getContents());
    }
    
    @Test
    public void testSetWorks() {
        Tape tape = new Tape('z');
        tape.setCharacterAt(5, 'x');
        assertEquals("zzzzzx", tape.getContents());
    }
}
