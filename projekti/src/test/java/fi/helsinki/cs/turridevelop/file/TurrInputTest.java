package fi.helsinki.cs.turridevelop.file;

import fi.helsinki.cs.turridevelop.exceptions.MalformedFileException;
import fi.helsinki.cs.turridevelop.exceptions.NameInUseException;
import fi.helsinki.cs.turridevelop.logic.Machine;
import fi.helsinki.cs.turridevelop.logic.Project;
import fi.helsinki.cs.turridevelop.logic.State;
import fi.helsinki.cs.turridevelop.logic.Transition;
import fi.helsinki.cs.turridevelop.util.Vec2;
import java.util.HashMap;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class TurrInputTest {
    
    public TurrInputTest() {
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
    public void testJSONToProjectWorks() throws JSONException, MalformedFileException, NameInUseException {
        HashMap<String, JSONObject> json = new HashMap<String, JSONObject>();
        json.put("mac", new JSONObject(
            "{states: {start: {transitions: [{destination: \"x\", inchar: \"y\", outchar: \"z\", move: \"R\"}, {destination: \"y\", inchar: \"e\", outchar: \"f\", move: \"S\"}], accepting: false, submachine: null, x: 15, y: -13.2}, x: {transitions: [], accepting: true, submachine: \"win\", x: 0, y: 0}, y: {transitions: [], accepting: false, submachine: null, x: 0, y: 0}}}"
        ));
        json.put("win", new JSONObject(
            "{states: {start: {transitions: [{destination: \"accept\", inchar: \"b\", outchar: \"5\", move: \"S\"}], accepting: false, submachine: null, x: 500, y: 2}, accept: {transitions: [], accepting: true, submachine: null, x: 12, y: 13.5}}}"
        ));
        
        Project proj = TurrInput.JSONToProject(json);
        
        Project cmpproj = new Project();
        Machine mac = cmpproj.addMachine("mac");
        Machine win = cmpproj.addMachine("win");
        
        State macstart = mac.addState("start");
        State macx = mac.addState("x");
        State macy = mac.addState("y");
        State winstart = win.addState("start");
        State winaccept = win.addState("accept");
        
        macx.setAccepting(true);
        winaccept.setAccepting(true);
        
        macx.setSubmachine("win");
        
        macstart.addTransition(new Transition(macx, "y", 'z', 1));
        macstart.addTransition(new Transition(macy, "e", 'f', 0));
        winstart.addTransition(new Transition(winaccept, "b", '5', 0));
        
        macstart.setPosition(new Vec2(15.0, -13.2));
        winstart.setPosition(new Vec2(500.0, 2.0));
        winaccept.setPosition(new Vec2(12.0, 13.5));
        
        assertTrue(Util.projectsEqual(proj, cmpproj));
    }
    
    @Test(expected=MalformedFileException.class)
    public void testUnknownDestinationThrows() throws JSONException, MalformedFileException {
        HashMap<String, JSONObject> json = new HashMap<String, JSONObject>();
        json.put("mac", new JSONObject(
            "{states: {start: {transitions: [{destination: \"asd\", inchar: \"a\", outchar: null, move: \"L\"}], submachine: null, accepting: false, x: 0, y: 0}}}"
        ));
        TurrInput.JSONToProject(json);
    }
    
    @Test(expected=MalformedFileException.class)
    public void testInvalidMoveThrows() throws JSONException, MalformedFileException {
        HashMap<String, JSONObject> json = new HashMap<String, JSONObject>();
        json.put("mac", new JSONObject(
            "{states: {start: {transitions: [{destination: \"start\", inchar: \"a\", outchar: null, move: \"G\"}], submachine: null, accepting: false, x: 0, y: 0}}}"
        ));
        TurrInput.JSONToProject(json);
    }
    
    @Test(expected=MalformedFileException.class)
    public void testInvalidOutcharThrows() throws JSONException, MalformedFileException {
        HashMap<String, JSONObject> json = new HashMap<String, JSONObject>();
        json.put("mac", new JSONObject(
            "{states: {start: {transitions: [{destination: \"start\", inchar: \"a\", outchar: \"abba\", move: \"S\"}], submachine: null, accepting: false, x: 0, y: 0}}}"
        ));
        TurrInput.JSONToProject(json);
    }
}
