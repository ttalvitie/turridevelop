package fi.helsinki.cs.turridevelop.file;

import fi.helsinki.cs.turridevelop.exceptions.MalformedFileException;
import fi.helsinki.cs.turridevelop.exceptions.NameInUseException;
import fi.helsinki.cs.turridevelop.logic.Machine;
import fi.helsinki.cs.turridevelop.logic.Project;
import fi.helsinki.cs.turridevelop.logic.State;
import fi.helsinki.cs.turridevelop.logic.Transition;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Functions for reading Turr files.
 */
public class TurrInput {
    /**
     * Reads a project directory.
     * 
     * @param dir The project directory to read.
     * @return The project read from all .turr-machine files from the directory.
     * @throws MalformedFileException if the project directory can not be read
     * from.
     */
    public static Project readProjectDirectory(
        File dir
    ) throws MalformedFileException {
        // Get the files in the directory.
        File[] files = dir.listFiles();
        
        if(files == null) {
            throw new MalformedFileException(
                "Could not open project directory '" + dir + "'"
            );
        }
        
        // Read the JSON machine files.
        HashMap<String, JSONObject> json = new HashMap<String, JSONObject>();
        for(File file : files) {
            String filename = file.getName();
            String extension = ".turr";
            int extension_start = filename.length() - extension.length();
            if(
                extension_start < 0 ||
                !filename.substring(extension_start).equals(extension)
            ) {
                break;
            }
            String name = filename.substring(0, extension_start);
            
            if(json.containsKey(name)) {
                throw new MalformedFileException(
                    "Multiple machines with name '" +
                    name + "' in the directory."
                );
            }
            
            StringBuilder source = new StringBuilder();
            try {
                FileInputStream in = new FileInputStream(file);
                InputStreamReader reader = new InputStreamReader(in, "UTF-8");
                
                char[] buffer = new char[1024];
                int read;
                while((read = reader.read(buffer, 0, 1024)) != -1) {
                    source.append(buffer, 0, read);
                }
            } catch(Exception e) {
                throw new MalformedFileException(
                    "Could not open machine file '" + file + "'."
                );
            }
            
            JSONObject machine_json;
            try {
                machine_json = new JSONObject(source.toString());
            } catch(JSONException e) {
                throw new MalformedFileException(
                    "Could not parse JSON from machine file '" + file + "'."
                );
            }
            json.put(name, machine_json);
        }
        
        Project project;
        try {
            project = JSONToProject(json);
        } catch(MalformedFileException e) {
            throw new MalformedFileException("Malformed machine files.");
        }
        
        return project;
    }
    
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
        Iterator iter = states_json.keys();
        while(iter.hasNext()) {
            String statename = (String) iter.next();
            State state = machine.addState(statename);
            JSONObject state_json = states_json.getJSONObject(statename);
            
            if(state_json.get("accepting").equals(true)) {
                state.setAccepting(true);
            }
        }
        
        // Then add transitions, because now all destination states should
        // exist.
        iter = states_json.keys();
        while(iter.hasNext()) {
            String statename = (String) iter.next();
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
