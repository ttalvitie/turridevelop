package fi.helsinki.cs.turridevelop.file;

import fi.helsinki.cs.turridevelop.exceptions.MalformedFileException;
import fi.helsinki.cs.turridevelop.exceptions.NameInUseException;
import fi.helsinki.cs.turridevelop.logic.Machine;
import fi.helsinki.cs.turridevelop.logic.Project;
import fi.helsinki.cs.turridevelop.logic.State;
import fi.helsinki.cs.turridevelop.logic.Transition;
import java.util.Map;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

/**
 * Functions for reading Turr files.
 */
public class TurrInput {
    /**
     * Reads a directory of JSON object representations of machines in Turr
     * format, and creates a Project from the representations.
     * 
     * @param json Map from the machine names to their JSON object
     * representations.
     * @return The project read from json.
     * @throws MalformedFileException if the file cannot be parsed.
     */
    public static Project JSONToProject(
        Map<String, JSONObject> json
    ) throws MalformedFileException {
        Project ret = new Project();
        try {
            // Read the states.
            for(String name : json.keySet()) {
                Machine machine = ret.addMachine(name);
                readStates(machine, json.get(name));
            }
        } catch(NameInUseException e) {
            throw new MalformedFileException();
        } catch(JSONException e) {
            throw new MalformedFileException();
        }
        
        return ret;
    }
    
    /**
     * Reads states from a Turr JSON object representation of a Machine.
     * 
     * @param machine The Machine to add the states to.
     * @param json The JSON object of the machine to read the states from.
     */
    private static void readStates(
        Machine machine,
        JSONObject json
    ) throws JSONException, NameInUseException, MalformedFileException {
        JSONObject states_json = json.getJSONObject("states");
        
        // First add states without transitions.
        for(Object statename_obj : states_json.keySet()) {
            String statename = (String) statename_obj;
            State state = machine.addState(statename);
            JSONObject state_json = states_json.getJSONObject(statename);
            
            if(state_json.get("accepting").equals(true)) {
                state.setAccepting(true);
            }
        }
        
        // Then add transitions, because now all destination states should
        // exist.
        for(Object statename_obj : states_json.keySet()) {
            String statename = (String) statename_obj;
            State state = machine.getState(statename);
            JSONObject state_json = states_json.getJSONObject(statename);
            JSONArray transitions_json = state_json.getJSONArray("transitions");
            
            for(int i = 0; i < transitions_json.length(); i++) {
                JSONObject transition_json = transitions_json.getJSONObject(i);
                
                String destination = transition_json.getString("destination");
                String inchar = transition_json.getString("inchar");
                
                Character outchar = null;
                Object outchar_obj = transition_json.get("outchar");
                if(outchar_obj instanceof String) {
                    String outchar_str = (String) outchar_obj;
                    if(outchar_str.length() != 1) {
                        throw new MalformedFileException();
                    }
                    outchar = outchar_str.charAt(0);
                }
                
                int move;
                String move_str = transition_json.getString("move");
                if(move_str.equals("L")) {
                    move = -1;
                } else if(move_str.equals("R")) {
                    move = 1;
                } else if(move_str.equals("S")) {
                    move = 0;
                } else {
                    throw new MalformedFileException();
                }
                
                state.addTransition(new Transition(
                    machine.getState(destination), inchar, outchar, move
                ));
            }
        }
    }
}
