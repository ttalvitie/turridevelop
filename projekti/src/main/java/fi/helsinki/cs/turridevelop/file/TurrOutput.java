package fi.helsinki.cs.turridevelop.file;

import fi.helsinki.cs.turridevelop.logic.Machine;
import fi.helsinki.cs.turridevelop.logic.State;
import fi.helsinki.cs.turridevelop.logic.Transition;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Functions for writing Turr files.
 */
public class TurrOutput {
    /**
     * Gets the JSON object representation of a Machine.
     * 
     * @param machine The machine to be represented as JSON.
     * @return The JSON object representation.
     */
    public static JSONObject machineToJSON(Machine machine) {
        JSONObject ret_json = new JSONObject();
        
        try {
            // Add states.
            JSONObject states_json = new JSONObject();
            for(String statename : machine.getStateNames()) {
                State state = machine.getState(statename);
                JSONObject state_json = new JSONObject();
                
                // Add transitions.
                JSONArray transitions_json = new JSONArray();
                for(Transition transition : state.getTransitions()) {
                    transitions_json.put(transitionToJSON(transition));
                }
                
                state_json.put("transitions", transitions_json);
                
                state_json.put("accepting", state.isAccepting());
                
                states_json.put(state.getName(), state_json);
            }
            
            ret_json.put("states", states_json);
            
            // Add the submachines. TODO: implement.
            ret_json.put("submachines", JSONObject.NULL);
            
        } catch(JSONException e) {
            // All JSONExceptions are unexpected.
            throw new RuntimeException("JSON failure.", e);
        }
        
        return ret_json;
    }
    
    /**
     * Gets the JSON object representation of a Transition.
     * 
     * @param transition The transition to be represented as JSON.
     * @return The JSON object representation.
     */
    public static JSONObject transitionToJSON(Transition transition) {
        JSONObject ret_json = new JSONObject();
        
        try {
            ret_json.put("destination", transition.getDestination().getName());
            ret_json.put("inchar", transition.getInputCharacters());
            
            if(transition.getOutputCharacter() == null) {
                ret_json.put("outchar", JSONObject.NULL);
            } else {
                String outchar = transition.getOutputCharacter().toString();
                ret_json.put("outchar", outchar);
            }
            
            String move = "S";
            if(transition.getMovement() == -1) {
                move = "L";
            }
            if(transition.getMovement() == 1) {
                move = "R";
            }
            
            ret_json.put("move", move);
        } catch(JSONException e) {
            // All JSONExceptions are unexpected.
            throw new RuntimeException("JSON failure.", e);
        }
        
        return ret_json;
    }
}
