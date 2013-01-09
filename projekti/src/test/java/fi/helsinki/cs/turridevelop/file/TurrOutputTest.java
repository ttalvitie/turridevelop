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
import java.util.Iterator;
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
    public void testBasicMachineToJSONWorks() throws NameInUseException, JSONException {
        Machine mac = proj.addMachine("mac");
        
        State x = mac.addState("x");
        State y = mac.addState("y");
        
        x.addTransition(new Transition(y, "asd", 'y', -1));
        y.setAccepting(true);
        
        JSONObject json = TurrOutput.machineToJSON(mac);
        
        JSONObject cmp = new JSONObject(
            "{states: {x: {transitions: [{outchar: \"y\", inchar: \"asd\", move: \"L\", destination: \"y\"}], submachine: null, accepting: false, x: 0, y: 0}, y: {transitions: [], submachine: null, accepting: true, x: 0, y: 0}}}"
        );
        
        System.out.println(json);
        System.out.println(cmp);
        
        assertTrue(jsonEquals(json, cmp));
    }
    
    /**
     * Check whether two objects from JSON are equal.
     */
    private boolean jsonEquals(Object a, Object b) {
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
