/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.helsinki.cs.turridevelop.file;

import fi.helsinki.cs.turridevelop.exceptions.NameInUseException;
import fi.helsinki.cs.turridevelop.logic.Machine;
import fi.helsinki.cs.turridevelop.logic.Project;
import fi.helsinki.cs.turridevelop.logic.State;
import fi.helsinki.cs.turridevelop.logic.Transition;
import fi.helsinki.cs.turridevelop.util.Vec2;
import java.util.Iterator;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class TurrOutputTest {
    Project proj;
    
    public TurrOutputTest() {
    }
    
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
    
    @Test
    public void testMachineToJSONWorks() throws NameInUseException, JSONException {
        Machine mac = proj.addMachine("mac");
        
        State x = mac.addState("x");
        State y = mac.addState("y");
        
        x.addTransition(new Transition(y, "asd", 'y', -1));
        y.setAccepting(true);
        
        x.setPosition(new Vec2(-10.0, 51.3));
        
        JSONObject json = TurrOutput.machineToJSON(mac);
        
        JSONObject cmp = new JSONObject(
            "{states: {x: {transitions: [{outchar: \"y\", inchar: \"asd\", move: \"L\", destination: \"y\"}], submachine: null, accepting: false, x: -10.0, y: 51.3}, y: {transitions: [], submachine: null, accepting: true, x: 0, y: 0}}}"
        );
        
        System.out.println(json);
        System.out.println(cmp);
        
        assertTrue(jsonEquals(json, cmp));
    }
    
    @Test
    public void testProjectToJSONWorks() throws NameInUseException, JSONException {
        Machine mac = proj.addMachine("mac");
        Machine win = proj.addMachine("win");
        
        State x = mac.addState("start");
        State y = mac.addState("accept");
        State a = win.addState("start");
        State b = win.addState("accept");
        
        y.setAccepting(true);
        b.setAccepting(true);
        
        x.setSubmachine("win");
        
        x.addTransition(new Transition(y, "asd", 'y', 1));
        a.addTransition(new Transition(b, "bsd", 'z', 0));
        
        Map<String, JSONObject> json = TurrOutput.projectToJSON(proj);
        assertEquals(2, json.size());
        
        JSONObject maccmp = new JSONObject(
            "{states: {start: {transitions: [{destination: \"accept\", inchar: \"asd\", outchar: \"y\", move: \"R\"}], accepting: false, submachine: \"win\", x: 0, y: 0}, accept: {transitions: [], accepting: true, submachine: null, x: 0, y: 0}}}"
        );
        assertTrue(jsonEquals(maccmp, json.get("mac")));
        
        JSONObject wincmp = new JSONObject(
            "{states: {start: {transitions: [{destination: \"accept\", inchar: \"bsd\", outchar: \"z\", move: \"S\"}], accepting: false, submachine: null, x: 0, y: 0}, accept: {transitions: [], accepting: true, submachine: null, x: 0, y: 0}}}"
        );
        assertTrue(jsonEquals(wincmp, json.get("win")));
    }
    
    /**
     * Check whether two objects from JSON are equal.
     */
    private boolean jsonEquals(Object a, Object b) {
        if(a == null) {
            return b == null;
        }
        
        if(a.equals(null)) {
            return b.equals(null);
        }
        
        if(a instanceof JSONObject) {
            if(!(b instanceof JSONObject)) {
                return false;
            }
            JSONObject A = (JSONObject) a;
            JSONObject B = (JSONObject) b;
            
            Iterator iter = A.keys();
            while(iter.hasNext()) {
                String name = (String) iter.next();
                if(!jsonEquals(A.opt(name), B.opt(name))) {
                    return false;
                }
            }
            iter = B.keys();
            while(iter.hasNext()) {
                String name = (String) iter.next();
                if(!jsonEquals(A.opt(name), B.opt(name))) {
                    return false;
                }
            }
            
            return true;
        }
        
        if(a instanceof JSONArray) {
            if(!(b instanceof JSONArray)) {
                return false;
            }
            JSONArray A = (JSONArray) a;
            JSONArray B = (JSONArray) b;
            
            if(A.length() != B.length()) {
                return false;
            }
            
            for(int i = 0; i < A.length(); i++) {
                if(!jsonEquals(A.opt(i), B.opt(i))) {
                    return false;
                }
            }
            
            return true;
        }
        
        if(a instanceof String || a instanceof Boolean) {
            return a.equals(b);
        }
        
        if(a instanceof Double || a instanceof Integer) {
            if(!(b instanceof Double || b instanceof Integer)) {
                return false;
            }
            double A;
            double B;
            if(a instanceof Double) {
                A = (Double) a;
            } else {
                A = (Integer) a;
            }
            if(b instanceof Double) {
                B = (Double) b;
            } else {
                B = (Integer) b;
            }
            
            return A == B;
        }
        
        throw new RuntimeException();
    }
}
