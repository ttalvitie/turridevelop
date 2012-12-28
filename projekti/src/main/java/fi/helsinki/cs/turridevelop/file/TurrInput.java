package fi.helsinki.cs.turridevelop.file;

import fi.helsinki.cs.turridevelop.exceptions.MalformedFileException;
import fi.helsinki.cs.turridevelop.exceptions.NameInUseException;
import fi.helsinki.cs.turridevelop.logic.Machine;
import fi.helsinki.cs.turridevelop.logic.Project;
import fi.helsinki.cs.turridevelop.logic.State;
import java.util.Map;
import java.util.Set;
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
     */
    public static Project JSONToProject(Map<String, JSONObject> json) {
        Project ret = new Project();
        try {
            // Read the states.
            for(String name : json.keySet()) {
                Machine machine = ret.addMachine(name);
                readStates(machine, json.get(name));
            }
        } catch(NameInUseException e) {
            throw new RuntimeException("Project reading failure.");
        } catch(JSONException e) {
            throw new RuntimeException("Project reading failure.");
        }
        
        return ret;
    }
    
    /**
     * Read states from a Turr JSON object representation of a Machine.
     * 
     * @param machine The Machine to add the states to.
     * @param json The JSON object to read the states from.
     */
    private static void readStates(
        Machine machine,
        JSONObject json
    ) throws JSONException, NameInUseException {
        JSONObject states_json = (JSONObject) json.get("states");
        
        for(Object statename_obj : states_json.keySet()) {
            String statename = (String) statename_obj;
            State state = machine.addState(statename);
            JSONObject state_json = (JSONObject) states_json.get(statename);
            
            if(state_json.get("accepting").equals(true)) {
                state.setAccepting(true);
            }
        }
    }
}
