package fi.helsinki.cs.turridevelop.file;

import fi.helsinki.cs.turridevelop.exceptions.FilesystemException;
import fi.helsinki.cs.turridevelop.logic.Machine;
import fi.helsinki.cs.turridevelop.logic.Project;
import fi.helsinki.cs.turridevelop.logic.State;
import fi.helsinki.cs.turridevelop.logic.Transition;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Functions for writing Turr files.
 */
public class TurrOutput {
    /**
     * Write the project to directory.
     * 
     * @param project The project to write.
     * @param dir The existent directory that the project should be written to.
     * 
     * @throws FilesystemException if some files could not be written.
     */
    public static void writeProject(
        Project project,
        File dir
    ) throws FilesystemException {
        HashMap<String, JSONObject> json = projectToJSON(project);
        for(String machine_name : json.keySet()) {
            String machine_json = json.get(machine_name).toString();
            
            File file = new File(dir, machine_name + ".turr");
            
            try {
                FileOutputStream out = new FileOutputStream(file);
                OutputStreamWriter writer = new OutputStreamWriter(out, "UTF-8");
                writer.write(machine_json, 0, machine_json.length());
                writer.close();
            } catch(Exception e) {
                throw new FilesystemException(
                    "Could not write machine file '" + file + "'."
                );
            }
        }
    }
    
    /**
     * Gets the representation of a Project as a directory of Turr JSON objects.
     * 
     * @param project The project to be represented as JSON.
     * @return Hashmap from machine names to their JSONObject representations.
     */
    public static HashMap<String, JSONObject> projectToJSON(Project project) {
        HashMap<String, JSONObject> ret = new HashMap<String, JSONObject>();
        for(String name : project.getMachineNames()) {
            ret.put(name, machineToJSON(project.getMachine(name)));
        }
        return ret;
    }
    
    /**
     * Gets the JSON object representation of a Machine in Turr format.
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
                state_json.put("joint", state.isJoint());
                
                state_json.put("x", state.getPosition().x);
                state_json.put("y", state.getPosition().y);
                
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
    private static JSONObject transitionToJSON(
        Transition transition
    ) throws JSONException {
        JSONObject ret_json = new JSONObject();
        
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
        
        return ret_json;
    }
}
