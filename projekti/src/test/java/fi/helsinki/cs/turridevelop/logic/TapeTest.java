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
    public void readInsideWorks() {
        Tape tape = new Tape("asd");
        assertEquals('a', tape.getCharacterAt(0));
        assertEquals('s', tape.getCharacterAt(1));
        assertEquals('d', tape.getCharacterAt(2));
    }
    
    @Test
    public void readOutsideWorks() {
        Tape tape = new Tape("asd");
        assertEquals(tape.getEmptyCharacter(), tape.getCharacterAt(3));
        assertEquals(tape.getEmptyCharacter(), tape.getCharacterAt(4));
        assertEquals(tape.getEmptyCharacter(), tape.getCharacterAt(7));
    }
}
